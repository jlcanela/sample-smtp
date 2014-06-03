import java.net.Socket
import scala.concurrent._
import scala.concurrent.duration._

import scala.concurrent.ExecutionContext.Implicits.global

object HardcodedSmtpClient {

  def createClient(msgPerCnx: Int = 1000) = {
    val bigbuffer = ("a" * (1024*100) + "\r\n").getBytes("UTF-8")
    val buffer = new Array[Byte](1024*32)
    val socket = new Socket("127.0.0.1", 25)
    val is = socket.getInputStream
    val os = socket.getOutputStream

    def readline = {
      val res = is.read(buffer)
      new String(buffer, 0, res)
    }

    def cmd(cmd:String) = {
      os.write(s"$cmd\r\n".getBytes("UTF-8"))
    }
    def data(d:Array[Byte]) = {
      os.write(d)
    }

    def send = {
      cmd("MAIL test@localhost")
      readline
      cmd("RCPT test@localhost")
      readline
      cmd("DATA")
      data(bigbuffer)
      cmd(".")
    }

    //val start=System.currentTimeMillis
    cmd("EHLO")
    readline

    (1 to msgPerCnx).foreach(_ => send)

    cmd("QUIT")
    readline
    socket.close
  }

  def go(count:Int, msgPerCnx: Int) = {
    val start=System.currentTimeMillis
    val seq = (1 to count).map(_ => future { createClient(msgPerCnx) })
    Await.result(Future.sequence(seq), 100 seconds)
    val end=System.currentTimeMillis
    println(s"${end - start} ms - ${1.0*count*1000*1000/(end - start)} msg/s ${1.0*count*1000*1000/(end - start)*100/1024} MB/s ")
  }

  def main(args: Array[String]) {
    go(10, 1000)
  }

}

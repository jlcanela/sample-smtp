sample-smtp
===========

To create an smtp-sink
* sudo smtp-sink -u theuser -c 127.0.0.1:25 1000

To check max speed
* time ./smtp-source -s 16 -l 102400 -m 10000 -c -f sender@example.com -t recipient@example.com 127.0.0.1:25

Current perf on mac book pro quadcore
* 11.681s per 10k messages of 100KB each with smtp-source
* 28.676s per 10k messages of 100KB each with scala/java generated message
* 55s per 10k messages of 100KB each with akka actors
* 9.430s per 10k messages of 100KB each with direct socket connection (1060 msg/s or 103 MB/s)

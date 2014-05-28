name := "SampleSmtp"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.4"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.3"

libraryDependencies += "javax.mail" % "mail" % "1.4"

libraryDependencies += "com.sun.mail" % "smtp" % "1.4.5"
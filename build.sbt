name := "d2proxy"

version := "0.1"

scalaVersion := "2.11.8"

libraryDependencies ++= {
  Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.5.19",
    "org.scodec" %% "scodec-bits" % "1.1.6",
    "org.scodec" %% "scodec-core" % "1.10.3",
    "default" %% "scala-unsigned" % "0.1",
    "org.slf4j" % "slf4j-api" % "1.7.5",
    "org.slf4j" % "slf4j-simple" % "1.7.5",
    "org.clapper" %% "grizzled-slf4j" % "1.3.2"
  )
}

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.5"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"

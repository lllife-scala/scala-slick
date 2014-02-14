name := "HelloSlick"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache
//  "com.typesafe.slick" %% "slick" % "2.0.0",
//  "org.slf4j" % "slf4j-nop" % "1.6.4"
)

libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.18"

libraryDependencies += "com.typesafe.slick" %% "slick" % "2.0.0-RC1"

libraryDependencies += "org.java-websocket" % "Java-WebSocket" % "1.3.0"

//libraryDependencies += "com.typesafe.play" %% "play-slick" % "0.6.0.0"

play.Project.playScalaSettings

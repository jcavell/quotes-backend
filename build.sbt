name := "play-scala-anorm-example"

version := "2.6.0-SNAPSHOT"

scalaVersion := "2.12.2"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

libraryDependencies += guice
libraryDependencies += jdbc
libraryDependencies += evolutions

libraryDependencies += "com.h2database" % "h2" % "1.4.194"

libraryDependencies += "com.typesafe.play" %% "anorm" % "2.5.3"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.2.1",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.1"
)

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "3.0.1"
//  "com.typesafe.play" %% "play-slick-evolutions" % "3.0.1"
)

// https://mvnrepository.com/artifact/org.postgresql/postgresql
libraryDependencies += "org.postgresql" % "postgresql" % "42.1.4"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-ws-standalone" % "1.0.4",
  "com.typesafe.play" %% "play-ws-standalone-xml" % "1.0.4",
  "com.typesafe.play" %% "play-ws-standalone-json" % "1.0.4",
  "com.typesafe.play" %% "play-ahc-ws-standalone" % "1.0.4",
  "com.typesafe.play" % "shaded-asynchttpclient" % "1.0.4",
  "com.typesafe.play" % "shaded-oauth" % "1.0.4"
)

libraryDependencies += ws
libraryDependencies += ehcache

// https://mvnrepository.com/artifact/org.specs2/specs2-core_2.12
libraryDependencies += "org.specs2" % "specs2-core_2.12" % "3.9.4" % "test"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.0" % Test

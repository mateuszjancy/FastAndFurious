import sbt._

object Dependencies {

  lazy val akka = Seq(
    "com.typesafe.akka" %% "akka-slf4j"  % "2.6.9",
    "com.typesafe.akka" %% "akka-stream" % "2.6.9",
    "com.typesafe.akka" %% "akka-http"   % "10.2.0"
  )

  lazy val fp = Seq(
    "org.slf4j"        % "slf4j-nop"  % "1.6.4",
    "com.typesafe"     % "config"     % "1.4.0",
    "org.typelevel"    %% "cats-core" % "2.2.0",
    "com.google.guava" % "guava"      % "29.0-jre"
  )

  lazy val logging = Seq(
    "ch.qos.logback" % "logback-classic" % "1.1.2",
    "ch.qos.logback" % "logback-core"    % "1.1.2"
  )

  lazy val json = Seq(
    "de.heikoseeberger" %% "akka-http-circe" % "1.34.0",
    "io.circe"          %% "circe-core"      % "0.13.0",
    "io.circe"          %% "circe-generic"   % "0.13.0",
    "io.circe"          %% "circe-parser"    % "0.13.0"
  )

  lazy val tests = Seq(
    "org.scalamock"     %% "scalamock"           % "5.0.0"  % Test,
    "org.scalatest"     %% "scalatest"           % "3.2.2"  % Test,
    "com.wix"           % "wix-embedded-mysql"   % "4.6.1"  % Test,
    "com.typesafe.akka" %% "akka-testkit"        % "2.6.9"  % Test,
    "com.typesafe.akka" %% "akka-stream-testkit" % "2.6.9"  % Test,
    "com.typesafe.akka" %% "akka-http-testkit"   % "10.2.0" % Test
  )

  lazy val persistance = Seq(
    "mysql"              % "mysql-connector-java" % "8.0.21",
    "com.typesafe.slick" %% "slick"               % "3.3.3",
    "com.typesafe.slick" %% "slick-hikaricp"      % "3.3.3"
  )

}

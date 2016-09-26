val buildName = "picture-voting"

name := buildName

version := "0.0.1"

organization := "codes.github"

scalaVersion := "2.11.8"

scalacOptions := Seq(
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked",
  "-Xfatal-warnings",
  "-language:existentials",
  "-Xlint",
  "-language:implicitConversions",
  "-Yinline-warnings",
  "-Yno-adapted-args",
  "-Ywarn-dead-code",
  "-Ywarn-value-discard",
  "-Ypatmat-exhaust-depth", "off",
  "-Xfuture"
)

lazy val finchVersion = "0.11.0-M3"

lazy val finagleVersion = "6.38.0"

lazy val circeVersion = "0.5.2"

libraryDependencies ++= {
  Seq(
    "com.github.finagle"    %%  "finch-core"        %   finchVersion,
    "com.github.finagle"    %%  "finch-circe"       %   finchVersion,
    "com.twitter"           %%  "twitter-server"    %   "1.23.0",
    "com.twitter"           %%  "finagle-http"      %   finagleVersion, 
    "com.twitter"           %%  "finagle-stats"     %   finagleVersion, 
    "io.circe"              %%  "circe-core"        %   circeVersion
  )
}

resolvers ++= {
  Seq(
    "Twitter Maven repo" at "http://maven.twttr.com/"
  )
}
resolvers += Resolver.sonatypeRepo("snapshots")

lazy val root = (project in file(".")).
  settings(
    name := buildName
)

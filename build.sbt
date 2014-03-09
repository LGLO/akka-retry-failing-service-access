
name := "akka-retry-failing-service-access"

version := "0.1-SNAPSHOT"

organization := "pl.lechglowiak"

scalaVersion := "2.10.3"

scalacOptions += "-deprecation"

scalacOptions += "-feature"

resolvers ++= Seq("Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
                  "Sonatype snapshots"  at "http://oss.sonatype.org/content/repositories/snapshots/")

libraryDependencies ++= {
  val akkaVersion       = "2.3.0"
  Seq(
    "com.typesafe.akka" %% "akka-actor"      % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j"      % akkaVersion,
    "ch.qos.logback"    %  "logback-classic" % "1.0.10",
    "com.typesafe.akka" %%  "akka-testkit"   % akkaVersion   % "test",
    "org.scalatest"     %% "scalatest"       % "1.9.2"       % "test",
    "com.google.guava"  % "guava"            % "16.0.1"
  )
}

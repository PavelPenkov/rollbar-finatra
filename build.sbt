name := """rollbar-finatra"""

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.twitter.finatra" %% "finatra-http" % "2.1.6",
  "com.rollbar" % "rollbar" % "0.5.3"
)

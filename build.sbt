testOptions in Test += Tests.Argument(TestFrameworks.JUnit, "-a", "-v", "-s")
parallelExecution in Test := false

scalaVersion := "2.13.1"

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-encoding", "UTF-8",
  "-unchecked",
  "-Xlint",
)

libraryDependencies += "junit" % "junit" % "4.13" % Test



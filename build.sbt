name := "Akka-Assignment-01"

version := "1.0"

scalaVersion := "2.11.8"



val akkaVersion = "2.4.17"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "log4j" % "log4j" % "1.2.17",
  "org.scalatest" %% "scalatest" % "3.0.1"


)

coverageExcludedPackages := "com\\.knoldus\\.MobilePurchaseApp.*"

name := "crdt"

version := "1.0"

scalaVersion := "2.11.8"

resolvers += "Kompics Releases" at "http://kompics.sics.se/maven/repository/"
resolvers += "Kompics Snapshots" at "http://kompics.sics.se/maven/snapshotrepository/"

libraryDependencies += "se.sics.kompics" %% "kompics-scala" % "0.9.2-SNAPSHOT"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "0.9.28"
libraryDependencies += "org.scalatest" % "scalatest_2.11" % "3.0.1" % "test"
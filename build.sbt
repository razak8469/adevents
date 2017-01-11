name := "adevents"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.11.8"

resolvers += "Spark Packages Repo" at "https://dl.bintray.com/spark-packages/maven"


libraryDependencies ++= {
    val connectorVersion = "1.6.0-M1"
    val sparkVersion = "1.6.1"
  val akkaHttpVersion = "10.0.0"
  Seq(
    "com.datastax.spark" % "spark-cassandra-connector_2.11" % connectorVersion,
    "org.apache.spark" % "spark-sql_2.11" % sparkVersion,
    "org.apache.spark" %% "spark-core" % sparkVersion,
    "org.apache.spark" % "spark-streaming_2.11" % sparkVersion,
    "org.apache.spark" % "spark-streaming-kafka_2.11" % sparkVersion,
    "org.apache.kafka" % "kafka_2.11" % "0.10.0.0",
    "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
    "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion
  )
}


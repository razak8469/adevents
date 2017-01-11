package adevents

import org.apache.spark.{SparkConf, SparkContext}
import com.datastax.spark.connector._

object AdEventsProcessor {
  def main(args: Array[String]) = {
    val conf = new SparkConf(true)
      .setMaster("local[*]").setAppName(getClass.getName)
      .set("spark.cassandra.connecion.host", "127.0.0.1")

    val sc = new SparkContext(conf)
    val table = sc.cassandraTable("system", "size_estimates")
    val rowCount = table.count
    println(s"Total rows in the table is : $rowCount")

    sc.stop()
  }
}
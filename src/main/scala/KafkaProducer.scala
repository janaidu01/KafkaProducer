import java.time.LocalDateTime

import scala.io.Source.fromFile
import java.util.Properties

import com.google.gson.Gson
import org.apache.kafka.clients.producer._

/**
  * Created by yury on 24.06.17.
  */
object KafkaProducer extends App {


  val props = new Properties()
  props.put("bootstrap.servers", "localhost:9092")

  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props)

  val TOPIC = "test"

  val gson = new Gson

  for (us <- readData("ab_browser_test.csv")) {
    println(gson.toJson(us))
    val record = new ProducerRecord(TOPIC, "key", gson.toJson(us))
    Thread.sleep(50)
    producer.send(record)
  }

  producer.close()

  private def readData(fileName: String): List[UserStat] = {
    fromFile(fileName).getLines().drop(1)
      .map(line => line.split(",").map(_.trim))
      .map(parts => UserStat(parts(0).toInt, parts(1), parts(2), parts(3).toInt, parts(4).toInt, parts(5).toInt))
      .toList
  }
}
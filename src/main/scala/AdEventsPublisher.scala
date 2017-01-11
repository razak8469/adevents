package adevents

import java.util.Properties

import akka.actor.{Actor, ActorLogging, Props}
import AdEvents._
import com.typesafe.config.ConfigFactory
import kafka.producer.{KeyedMessage, Producer, ProducerConfig}
import spray.json._


object AdEventsPublisher {
  def props = Props(new AdEventsPublisher)
  def name = "publisher"
}

class AdEventsPublisher extends Actor with ActorLogging {

  var adservePublisher: Producer[String, String] = _
  var urlopenPublisher: Producer[String, String] = _
  var videoviewPublisher: Producer[String, String] = _

  val adservTopic = "AD_TOPIC"
  val urlopenTopic = "URL_TOPIC"
  val videoViewTopic = "VIDEO_TOPIC"

  override def preStart() = {
    val config = getProducerConfig
    adservePublisher = new Producer[String, String](config)
    urlopenPublisher = new Producer[String, String](config)
    videoviewPublisher = new Producer[String, String](config)
  }

  override def postStop(): Unit = {
    adservePublisher.close()
    urlopenPublisher.close()
    videoviewPublisher.close()
  }
  def receive = {
    case event: AdServe =>
      adservePublisher.send(new KeyedMessage[String, String](adservTopic, event.toJson.toString))
      log.info(s"send ad event: ${event.toJson.toString}")
    case event: UrlOpen =>
      urlopenPublisher.send(new KeyedMessage[String, String](urlopenTopic, event.toJson.toString))
      log.info(s"send url-open event: ${event.toJson.toString}")

    case event: VideoView =>
      videoviewPublisher.send(new KeyedMessage[String, String](videoViewTopic, event.toJson.toString))
      log.info(s"send video-view event: ${event.toJson.toString}")
  }

  def getProducerConfig(): ProducerConfig = {
    val keyBrokers = "metadata.broker.list"
    val keySerializer = "serializer.class"
    val keyRequiredAcks = "request.required.acks"
    val keyMaxRetries = "message.send.max.retries"
    val keyProducerType = "producer.type"
    val keyBatchNumMessages = "batch.num.messages"
    val keys = Seq(keyBrokers, keySerializer, keyMaxRetries,
      keyProducerType, keyRequiredAcks, keyBatchNumMessages)

    val config = ConfigFactory.load("producer")
    val props = new Properties()
    keys.map{ key =>
      if(config.hasPath(key))
        props.put(key, config.getString(key))
    }
    new ProducerConfig(props)
  }
}
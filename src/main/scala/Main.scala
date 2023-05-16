import io.apicurio.registry.serde.AbstractKafkaSerializer
import io.apicurio.registry.serde.protobuf.{ProtobufSerde, ProtobufSerdeHeaders}
import io.apicurio.registry.utils.protobuf.schema.ProtobufSchema
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.Headers
import org.apache.kafka.common.header.internals.RecordHeaders
import tutorial.addressbook.Person
import zio.*
import zio.Console.*
import zio.*
import zio.kafka.consumer.*
import zio.kafka.producer.{Producer, ProducerSettings}
import zio.kafka.serde.*
import zio.stream.ZStream

object MyApp extends ZIOAppDefault {

  val samplePersons = Seq(Person("John", 123), Person("Jane", 456))

  val configs = new java.util.HashMap[String, AnyRef]()
  val protobufSerdeHeaders = new ProtobufSerdeHeaders(configs, false)

val topic = "persons"
  val producer: ZStream[Producer, Throwable, Nothing] =
    ZStream.fromIterable(samplePersons)
      .schedule(Schedule.fixed(2.seconds))
      .mapZIO { (person: Person) => {
        val headers: Headers = new RecordHeaders
        protobufSerdeHeaders.addProtobufTypeNameHeader(headers, "Persons")

        val record = new ProducerRecord[Long, Array[Byte]](topic, null, null, person.id, person.toByteArray, headers)

        Producer.produce[Any, Long, Array[Byte]](
          record,
          keySerializer = Serde.long,
          valueSerializer = Serde.byteArray
        )
      }
      }
      .drain

  val consumer: ZStream[Consumer, Throwable, Nothing] =
    Consumer
      .plainStream(Subscription.topics("persons"), Serde.long, Serde.byteArray)
      .tap(r => Console.print(Person.parseFrom(r.value)))
      .map(_.offset)
      .aggregateAsync(Consumer.offsetBatches)
      .mapZIO(_.commit)
      .drain

  def producerLayer =
    ZLayer.scoped(
      Producer.make(
        settings = ProducerSettings(List("localhost:9092"))
      )
    )

  def consumerLayer =
    ZLayer.scoped(
      Consumer.make(
        ConsumerSettings(List("localhost:9092")).withGroupId("group")
      )
    )

  override def run =
    for {
      _ <- producer.merge(consumer)
        .runDrain
        .provide(producerLayer, consumerLayer)
      _ <-  ZIO.system.exit
    } yield ()
}
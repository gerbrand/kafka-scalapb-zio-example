import org.apache.kafka.common.header.Headers
import scalapb.GeneratedMessage
import zio.RIO
import zio.kafka.serde.*
class ZioPbSerde[-R, A <: GeneratedMessage] extends Serde[R, A] {
  override def deserialize(topic: String, headers: Headers, data: Array[Byte]): RIO[R, A] = ???

  override def serialize(topic: String, headers: Headers, value: A): RIO[R, Array[Byte]] = ???
}

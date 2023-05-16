package io.apicurio.registry.serde.protobuf.scalapb

import com.google.protobuf.Message
import io.apicurio.registry.resolver.{ParsedSchema, SchemaParser}
import io.apicurio.registry.serde.AbstractKafkaSerializer
import io.apicurio.registry.serde.protobuf.{ProtobufKafkaSerializerConfig, ProtobufSchemaParser, ProtobufSerdeHeaders}
import io.apicurio.registry.utils.protobuf.schema.ProtobufSchema
import org.apache.kafka.common.header.Headers
import scalapb.{GeneratedMessage, GeneratedMessageCompanion}

import java.io.OutputStream
import java.util
import java.util.HashMap

class ScalaPbKafkaSerializer[A <: GeneratedMessage, V <: GeneratedMessageCompanion[A]](companion: V) extends AbstractKafkaSerializer[ProtobufSchema, A] {

  private var serdeHeaders: ProtobufSerdeHeaders = null
  private val parser = new ScalaPbSchemaParser[A, V](companion)

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = {
    val config: ProtobufKafkaSerializerConfig = new ProtobufKafkaSerializerConfig(configs)
    super.configure(config, isKey)
    serdeHeaders = new ProtobufSerdeHeaders(new util.HashMap[String, AnyRef](configs), isKey)
  }


  override def serializeData(schema: ParsedSchema[ProtobufSchema], data: A, out: OutputStream): Unit = ???

  override def serializeData(headers: Headers, schema: ParsedSchema[ProtobufSchema], data: A, out: OutputStream): Unit = ???

  override def schemaParser(): SchemaParser[ProtobufSchema, A] = parser
}

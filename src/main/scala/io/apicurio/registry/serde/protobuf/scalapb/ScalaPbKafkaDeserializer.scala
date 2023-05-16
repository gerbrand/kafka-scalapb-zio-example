package io.apicurio.registry.serde.protobuf.scalapb

import io.apicurio.registry.resolver.{ParsedSchema, SchemaParser}
import io.apicurio.registry.serde.AbstractKafkaDeserializer
import io.apicurio.registry.utils.protobuf.schema.ProtobufSchema
import org.apache.kafka.common.header.Headers
import scalapb.GeneratedMessage

import java.nio.ByteBuffer

class ScalaPbKafkaDeserializer[A <: GeneratedMessage] extends AbstractKafkaDeserializer[ProtobufSchema, A]{
  override def readData(schema: ParsedSchema[ProtobufSchema], buffer: ByteBuffer, start: Int, length: Int): A = ???

  override def readData(headers: Headers, schema: ParsedSchema[ProtobufSchema], buffer: ByteBuffer, start: Int, length: Int): A = ???

  override def schemaParser(): SchemaParser[ProtobufSchema, A] = ???
}

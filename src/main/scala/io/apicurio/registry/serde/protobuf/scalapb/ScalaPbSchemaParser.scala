package io.apicurio.registry.serde.protobuf.scalapb

import com.google.protobuf.{Descriptors, Message}
import com.squareup.wire.schema.internal.parser.{MessageElement, ProtoFileElement, ProtoParser}
import io.apicurio.registry.resolver.{ParsedSchema, ParsedSchemaImpl, SchemaParser}
import io.apicurio.registry.resolver.data.Record
import io.apicurio.registry.serde.protobuf.ProtobufSchemaParser
import io.apicurio.registry.types.ArtifactType
import io.apicurio.registry.utils.IoUtil
import io.apicurio.registry.utils.protobuf.schema.{FileDescriptorUtils, ProtobufSchema}
import org.apache.kafka.common.errors.SerializationException
import scalapb.GeneratedMessage
import scalapb.GeneratedMessageCompanion
import scalapb.descriptors.DescriptorValidationException

import java.util
import java.util.{ArrayList, HashMap, List, Map}

class ScalaPbSchemaParser[U <: GeneratedMessage, V <: GeneratedMessageCompanion[U]](companion: V) extends SchemaParser[ProtobufSchema, U] {

  val javaProtoBufSchemaParser = new ProtobufSchemaParser[Message]()

  /**
   * @see io.apicurio.registry.serde.SchemaParser#artifactType()
   */
  override def artifactType: String = ArtifactType.PROTOBUF

  /**
   * @see io.apicurio.registry.serde.SchemaParser#parseSchema(byte[])
   */
  override def parseSchema(rawSchema: Array[Byte], resolvedReferences: util.Map[String, ParsedSchema[ProtobufSchema]]): ProtobufSchema = javaProtoBufSchemaParser.parseSchema(rawSchema, resolvedReferences)

  /**
   * @see io.apicurio.registry.resolver.SchemaParser#getSchemaFromData(Record)
   */
  override def getSchemaFromData(data: Record[U]): ParsedSchema[ProtobufSchema] = {
    // We ignore the data and just return the schema for the companion class.
    val schemaFileDescriptor: Descriptors.FileDescriptor = companion.javaDescriptor.getFile
    val protoFileElement: ProtoFileElement = toProtoFileElement(schemaFileDescriptor)
    val protobufSchema: ProtobufSchema = new ProtobufSchema(schemaFileDescriptor, protoFileElement)
    val rawSchema: Array[Byte] = IoUtil.toBytes(protoFileElement.toSchema)
    new ParsedSchemaImpl[ProtobufSchema]().setParsedSchema(protobufSchema).setReferenceName(protobufSchema.getFileDescriptor.getName).setSchemaReferences(handleDependencies(schemaFileDescriptor)).setRawSchema(rawSchema)
  }

  override def getSchemaFromData(data: Record[U], dereference: Boolean): ParsedSchema[ProtobufSchema] = {
    //dereferencing not supported, just extract with references
    getSchemaFromData(data)
  }

  private def handleDependencies(fileDescriptor: Descriptors.FileDescriptor): util.List[ParsedSchema[ProtobufSchema]] = {
    val schemaReferences: util.List[ParsedSchema[ProtobufSchema]] = new util.ArrayList[ParsedSchema[ProtobufSchema]]
    fileDescriptor.getDependencies.forEach((referenceFileDescriptor: Descriptors.FileDescriptor) => {
      val referenceProtoFileElement: ProtoFileElement = toProtoFileElement(referenceFileDescriptor)
      val referenceProtobufSchema: ProtobufSchema = new ProtobufSchema(referenceFileDescriptor, referenceProtoFileElement)
      val rawSchema: Array[Byte] = IoUtil.toBytes(referenceProtoFileElement.toSchema)
      val referencedSchema: ParsedSchema[ProtobufSchema] = new ParsedSchemaImpl[ProtobufSchema]().setParsedSchema(referenceProtobufSchema).setReferenceName(referenceProtobufSchema.getFileDescriptor.getName).setSchemaReferences(handleDependencies(referenceFileDescriptor)).setRawSchema(rawSchema)
      schemaReferences.add(referencedSchema)

    })
    schemaReferences
  }

  /**
   * This method converts the Descriptor to a ProtoFileElement that allows to get a textual representation .proto file
   *
   * @param fileDescriptor
   * @return textual protobuf representation
   */
  def toProtoFileElement(fileDescriptor: Descriptors.FileDescriptor): ProtoFileElement = FileDescriptorUtils.fileDescriptorToProtoFile(fileDescriptor.toProto)
}

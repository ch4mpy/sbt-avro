package com.c4soft.sbtavro.utils

import java.io.File

import org.apache.avro.Schema
import play.api.libs.json._

import scala.io.Source

/**
  * Created by nathanmarin on 09/06/16.
  */
class SchemaParser(fromFile: File) {

  private[this] lazy val jsonValue = Json.parse(Source.fromFile(fromFile).mkString)

  // needs to be lazy so it's initialized after the enum values
  private lazy val avroDefaultTypeNames = Schema.Type.values().map(_.getName)

  def getFullyQualifiedName(): String = {
    val namespaceOpt = (jsonValue \ "namespace").asOpt[String]
    val name = (jsonValue \ "name").as[String]
    namespaceOpt.map(_ + ".").getOrElse("") + name
  }

  def getDependentSchemas(typeExclusions: Seq[String] = avroDefaultTypeNames): Seq[String] = {
    extractTypes(jsonValue).filterNot(typeExclusions.contains).distinct
  }

  private def extractTypes(value: JsValue): Seq[String] = {
    value match {
      case JsString(_type) => Seq(_type)
      case obj: JsObject =>
        obj \ "type" match {
          case JsArray(values) => values.flatMap(v => extractTypes(v))
          case JsString("record") => parseFields((obj \ "fields").as[JsArray])
          case JsString("array") => extractTypes(obj \ "items")
          case JsString(otherType) => Seq(otherType)
          case obj: JsObject => extractTypes(obj)
          case _ => Seq.empty
        }
      case JsArray(values) => values.flatMap(v => extractTypes(v))
      case other => Seq.empty
    }
  }

  private def parseFields(listOfFields: JsArray) = {
    listOfFields.value.flatMap {
      case obj: JsObject => extractTypes(obj)
      case _ => Seq.empty
    }
  }

}

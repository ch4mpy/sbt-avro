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
  private lazy val namespaceOpt = (jsonValue \ "namespace").asOpt[String]

  def getFullyQualifiedName(name: String = (jsonValue \ "name").as[String]): String = {
    namespaceOpt.map(_ + ".").getOrElse("") + name
  }

  def getDeclaredSchemas(typeExclusions: Seq[String] = avroDefaultTypeNames): Seq[String] = {
    extractTypes(jsonValue, declaredSchemas = true).filterNot(typeExclusions.contains).distinct
  }

  def getDependentSchemas(typeExclusions: Seq[String] = avroDefaultTypeNames): Seq[String] = {
    extractTypes(jsonValue, declaredSchemas = false).filterNot(typeExclusions.contains).distinct
  }

  private def extractTypes(value: JsValue, declaredSchemas: Boolean): Seq[String] = {
    value match {
      case JsString(_type) if !declaredSchemas => Seq(_type)
      case obj: JsObject =>
        obj \ "type" match {
          case JsArray(values) => values.flatMap(v => extractTypes(v, declaredSchemas))
          case JsString("record") =>
            val fromRecordName = (obj \ "name").asOpt[String].filter(_ => declaredSchemas) match {
              case Some(name) => Seq(getFullyQualifiedName(name))
              case None => Seq.empty
            }
            val fromRecordFields = (obj \ "fields").as[JsArray].value.flatMap {
              case obj: JsObject => extractTypes(obj, declaredSchemas)
              case _ => Seq.empty
            }

            fromRecordName ++ fromRecordFields
          case JsString("array") => extractTypes(obj \ "items", declaredSchemas)
          case JsString(otherType) if !declaredSchemas => Seq(otherType)
          case obj: JsObject => extractTypes(obj, declaredSchemas)
          case _ => Seq.empty
        }
      case JsArray(values) => values.flatMap(v => extractTypes(v, declaredSchemas))
      case other => Seq.empty
    }
  }

}

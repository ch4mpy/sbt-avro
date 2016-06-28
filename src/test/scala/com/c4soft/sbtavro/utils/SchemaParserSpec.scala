package com.c4soft.sbtavro.utils

import java.io.File

import org.specs2.mutable.Specification

/**
  * Created by nathanmarin on 09/06/16.
  */
class SchemaParserSpec extends Specification {
  val sourceFile = new File(getClass.getClassLoader.getResource("avro/d.avsc").toURI)
  val parser = new SchemaParser(sourceFile)
  "SchemaParser should use the namespace of the schema to get the fully qualified name" >> {
    parser.getFullyQualifiedName() must beEqualTo("com.c4soft.sbtavro.D")
  }
  "SchemaParser should be able to list the dependent schemas even in deeply nested fields" >> {
    parser.getDependentSchemas() must containTheSameElementsAs(Seq("com.c4soft.sbtavro.SchemaParsing1", "com.c4soft.sbtavro.SchemaParsing2"))
  }
  "SchemaParser should correctly ignore certain types when getting the dependent schemas" >> {
    parser.getDependentSchemas(Seq("com.c4soft.sbtavro.SchemaParsing2")) must containTheSameElementsAs(Seq("com.c4soft.sbtavro.SchemaParsing1"))
  }
  "SchemaParser should be able to find dependant type inside of arrays" >> {
    val fileWithArray = new File(getClass.getClassLoader.getResource("avro/RecordWithArray.avsc").toURI)
    val newParser = new SchemaParser(fileWithArray)
    newParser.getDependentSchemas() must containTheSameElementsAs(Seq("TypeWithinArray"))
  }
}

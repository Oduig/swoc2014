package com.gjos.scala.swoc.util

import org.json.simple.{JSONArray, JSONObject, JSONValue}
import scala.languageFeature.implicitConversions
import scala.collection.JavaConverters._

object JsonParser {
  def parse(jsonMessage: String): JSONObject = JSONValue.parse(jsonMessage).asInstanceOf[JSONObject]

  implicit class RichJsonObject(val obj: JSONObject) extends AnyVal {
    def getAs[T](key: String): T = obj.get(key).asInstanceOf[T]
    def getArray[T: Manifest](): Array[T] = obj.asInstanceOf[java.util.List[T]].asScala.toArray
    def getArray[T: Manifest](key: String): Array[T] = obj.get(key).asInstanceOf[java.util.List[T]].asScala.toArray
  }

  implicit class RichJsonArray(val obj: JSONArray) extends AnyVal {
    def getArray[T: Manifest](): Array[T] = obj.asInstanceOf[java.util.List[T]].asScala.toArray
  }
}

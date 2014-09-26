package com.gjos.scala.swoc.util

import org.json.simple.{JSONArray, JSONObject, JSONValue}
import scala.languageFeature.implicitConversions
import scala.collection.JavaConverters._

object JsonParser {
  def parse(jsonMessage: String): JSONObject = JSONValue.parse(jsonMessage).asInstanceOf[JSONObject]

  implicit class RichJsonObject(val obj: JSONObject) extends AnyVal {
    def getAs[T](key: String): T = obj.get(key).asInstanceOf[T]
    def getList[T: Manifest](): java.util.List[T] = obj.asInstanceOf[java.util.List[T]]
    def getList[T: Manifest](key: String): java.util.List[T] = obj.get(key).asInstanceOf[java.util.List[T]]
  }

  implicit class RichJsonArray(val obj: JSONArray) extends AnyVal {
    def getList[T: Manifest](): java.util.List[T] = obj.asInstanceOf[java.util.List[T]]
  }
}

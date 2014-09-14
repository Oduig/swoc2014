package com.gjos.scala.swoc.util

import org.json.simple.{JSONArray, JSONObject, JSONValue}
import scala.languageFeature.implicitConversions
import scala.collection.JavaConverters._

object JsonParser {
  def parse(jsonMessage: String): JSONObject = JSONValue.parse(jsonMessage).asInstanceOf[JSONObject]

  implicit class RichJsonObject(val obj: JSONObject) extends AnyVal {
    def getAs[T](key: String): T = obj.get(key).asInstanceOf[T]
    def getList[T](): List[T] = obj.asInstanceOf[java.util.List[JSONObject]].asScala.toList map (_.asInstanceOf[T])
    def getList[T](key: String): List[T] = obj.get(key).asInstanceOf[java.util.List[Any]].asScala.toList map (_.asInstanceOf[T])
  }

  implicit class RichJsonArray(val obj: JSONArray) extends AnyVal {
    def getList[T](): List[T] = obj.asInstanceOf[java.util.List[Any]].asScala.toList map (_.asInstanceOf[T])
  }
}

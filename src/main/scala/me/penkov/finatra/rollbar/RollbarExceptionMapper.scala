package me.penkov.finatra.rollbar

import java.net.InetAddress

import com.google.inject.{Inject, Singleton}
import com.rollbar.Rollbar
import com.rollbar.payload.data.Server
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.annotations.Flag
import com.twitter.finatra.http.exceptions.ExceptionMapper
import com.twitter.finatra.http.response.ResponseBuilder

import scala.collection.JavaConversions._

@Singleton
class RollbarExceptionMapper @Inject() (response: ResponseBuilder,
                              @Flag("rollbarToken") token: String,
                              @Flag("appEnv") env: Option[String] = None,
                              @Flag("codeVersion") version: Option[String]) extends ExceptionMapper[Exception] {

  val rollbar = new Rollbar(token, env.getOrElse("production")).server(server).codeVersion(version.orNull).language("scala")

  implicit def toRollbarRequest(req: Request): com.rollbar.payload.data.Request = {
    val params: java.util.Map[String, String] = req.params
    new com.rollbar.payload.data.Request(req.uri,
      req.method.toString(),
      req.headerMap.toMap,
      null,
      params,
      null,
      req.params.toMap,
      req.contentString,
      req.remoteAddress)
  }

  override def toResponse(request: Request, throwable: Exception): Response = {
    rollbar.request(request).error(throwable)
    response.internalServerError
  }

  lazy val codeVersion = version.orNull

  lazy val server = {
    val hostname = InetAddress.getLocalHost.getHostName
    new Server(hostname, null, null, codeVersion)
  }

}

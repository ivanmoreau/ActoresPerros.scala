package com.ivmoreau.actoresperros.api

import cats.implicits.*
import cats.effect.IO
import scala.concurrent.duration.FiniteDuration

trait ActorMethods[Msg, Result]:
  def get: IO[Msg]
  def getTimed(timeout: FiniteDuration): IO[Option[Msg]]
  def send(result: Result): IO[Unit]
end ActorMethods

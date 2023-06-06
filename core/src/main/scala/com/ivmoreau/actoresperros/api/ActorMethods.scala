package com.ivmoreau.actoresperros.api

import cats.implicits.*
import cats.effect.IO

trait ActorMethods[Msg, Result]:
  def get: IO[Msg]
  def send(result: Result): IO[Unit]
end ActorMethods

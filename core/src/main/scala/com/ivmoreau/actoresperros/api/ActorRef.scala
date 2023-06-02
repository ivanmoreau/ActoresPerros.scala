package com.ivmoreau.actoresperros.api

import cats.implicits.*
import cats.effect.IO

trait ActorRef[Msg]:
  def !(msg: Msg): IO[Unit]
  def cancel(): IO[Unit]
  def join(): IO[Unit]
end ActorRef

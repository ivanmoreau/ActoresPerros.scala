package com.ivmoreau.actoresperros.api

import cats.implicits.*
import cats.effect.IO

trait ActorMethods[Msg]:
  def get: IO[Msg]
end ActorMethods

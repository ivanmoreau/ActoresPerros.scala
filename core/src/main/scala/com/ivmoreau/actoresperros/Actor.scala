package com.ivmoreau.actoresperros

import cats.effect
import cats.implicits.*
import cats.effect.IO
import com.ivmoreau.actoresperros.internal.ActorInternal
import com.ivmoreau.actoresperros.factory.DefaultActorFactory
import com.ivmoreau.actoresperros.api.{ActorMethods, ActorRef}

trait Actor:

  type Msg
  type State

  def get(using aM: ActorMethods[Msg]): IO[Msg] = aM.get

  def function(st: State)(using ActorMethods[Msg]): IO[Unit]

  def apply(init: State): IO[ActorRef[Msg]] =
    DefaultActorFactory.createActor(init).flatMap {
      (ai: ActorInternal[Msg, State]) =>
        given ActorMethods[Msg] with
          override def get: IO[Msg] = ai.queue.take
        ai.actorRef(state => function(state))
    }
end Actor

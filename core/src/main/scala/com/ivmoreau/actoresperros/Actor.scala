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
  type Result

  def get(using aM: ActorMethods[Msg, Result]): IO[Msg] = aM.get
  def send(result: Result)(using aM: ActorMethods[Msg, Result]): IO[Unit] = aM.send(result)

  def function(st: State)(using ActorMethods[Msg, Result]): IO[Result]

  def apply(init: State): IO[ActorRef[Msg, Result]] =
    DefaultActorFactory.createActor(init).flatMap {
      (ai: ActorInternal[Msg, State, Result]) =>
        given ActorMethods[Msg, Result] with
          override def get: IO[Msg] = ai.queue.take
          override def send(result: Result): IO[Unit] = ai.outQueue.offer(result)
        ai.actorRef(state => function(state))
    }
end Actor

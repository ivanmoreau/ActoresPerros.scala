package com.ivmoreau.actoresperros.factory

import cats.effect
import cats.implicits.*
import cats.effect.{IO, Ref}
import cats.effect.std.Queue
import com.ivmoreau.actoresperros.factory.ActorFactory

object DefaultActorFactory extends ActorFactory:
  override def queueMaker[A]: IO[Queue[IO, A]] = Queue.bounded[IO, A](5)
  override def refMaker[A]: A => IO[Ref[IO, A]] = Ref.of[IO, A](_)
end DefaultActorFactory

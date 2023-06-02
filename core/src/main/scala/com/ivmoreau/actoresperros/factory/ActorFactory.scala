package com.ivmoreau.actoresperros.factory

import cats.implicits.*
import cats.effect.{IO, Ref}
import cats.effect.std.Queue
import com.ivmoreau.actoresperros.internal.ActorInternal

trait ActorFactory:
  def queueMaker[A]: IO[Queue[IO, A]]
  def refMaker[A]: A => IO[Ref[IO, A]]

  def createActor[Msg, State](
      initialState: State
  ): IO[ActorInternal[Msg, State]] = for {
    newQueue <- queueMaker[Msg]
    stateRef <- refMaker[State](initialState)
  } yield new ActorInternal[Msg, State] {
    override val queue: Queue[IO, Msg] = newQueue
    override val state: Ref[IO, State] = stateRef
  }
end ActorFactory

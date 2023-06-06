package com.ivmoreau.actoresperros.factory

import cats.implicits.*
import cats.effect.{IO, Ref}
import cats.effect.std.Queue
import com.ivmoreau.actoresperros.internal.ActorInternal

trait ActorFactory:
  def queueMaker[A]: IO[Queue[IO, A]]
  def refMaker[A]: A => IO[Ref[IO, A]]

  def createActor[Msg, State, Result](
      initialState: State
  ): IO[ActorInternal[Msg, State, Result]] = for {
    newQueue <- queueMaker[Msg]
    newOutQueue <- queueMaker[Result]
    stateRef <- refMaker[State](initialState)
  } yield new ActorInternal[Msg, State, Result] {
    override val queue: Queue[IO, Msg] = newQueue
    override val outQueue: Queue[IO, Result] = newOutQueue
    override val state: Ref[IO, State] = stateRef
  }
end ActorFactory

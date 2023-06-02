package com.ivmoreau.actoresperros.internal

import cats.effect
import cats.implicits.*
import cats.effect.{FiberIO, IO, IOApp, Ref}
import cats.effect.std.Queue
import com.ivmoreau.actoresperros.api.ActorRef

private[actoresperros] trait ActorInternal[Msg, State]:
  protected[actoresperros] val queue: Queue[IO, Msg]
  protected val state: Ref[IO, State]

  def actorRef(f: State => IO[Unit]): IO[ActorRef[Msg]] =
    state.get.flatMap[Unit](f).start.map { fiber =>
      new ActorRef[Msg]:
        override def !(msg: Msg): IO[Unit] = queue.offer(msg)

        override def cancel(): IO[Unit] = fiber.cancel

        override def join(): IO[Unit] = fiber.join.void
    }
end ActorInternal

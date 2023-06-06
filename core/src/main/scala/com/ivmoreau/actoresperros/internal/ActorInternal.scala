package com.ivmoreau.actoresperros.internal

import cats.effect
import cats.implicits.*
import cats.effect.{FiberIO, IO, IOApp, Ref}
import cats.effect.std.Queue
import com.ivmoreau.actoresperros.api.ActorRef
import cats.effect.kernel.Outcome.Succeeded
import cats.effect.kernel.Outcome.Errored
import cats.effect.kernel.Outcome.Canceled

private[actoresperros] trait ActorInternal[Msg, State, Result]:
  protected[actoresperros] val queue: Queue[IO, Msg]
  protected[actoresperros] val outQueue: Queue[IO, Result]
  protected val state: Ref[IO, State]

  def actorRef(f: State => IO[Result]): IO[ActorRef[Msg, Result]] =
    state.get.flatMap[Result](f).start.map { fiber =>
      new ActorRef[Msg, Result]:

        override def queueSize: IO[Int] = queue.size

        override def ? : IO[Result] = outQueue.take

        override def !(msg: Msg): IO[Unit] = queue.offer(msg)

        override def cancel(): IO[Unit] = fiber.cancel

        override def join(): IO[Result] = fiber.join.flatMap {
          case Succeeded(fa) => fa
          case Errored(e) => IO.raiseError(e)
          case Canceled() => IO.raiseError(new Exception("Actor was canceled"))
        }
    }
end ActorInternal

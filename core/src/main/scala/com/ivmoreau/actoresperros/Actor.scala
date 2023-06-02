package com.ivmoreau.actoresperros

import cats.implicits.*
import cats.effect.{FiberIO, IO, Ref}
import cats.effect.kernel.Fiber
import cats.effect.std.Queue
import fs2.Stream


object Calculator extends Actor[MyEnum, MyState]:
  def function(st: MyState): Unit = for
    msg <- get
  yield ()
end Calculator

// Calculator(initState) -->> ActorRef

trait Actor[Msg, State]:
  def function(st: State): IO[Unit]
  def get: IO[Msg] = ???
  def apply(init: State): ActorRef[Msg] = DefaultActorFactory.createActor(new ActorInternalBehaviour[Msg, State] {
    override get
  })
end Actor

/*
new ActorInternalBehaviour[Msg, State]:
    override def get: IO[Msg] = queue.take
    override def function: State => IO[Unit] = externalFunction
*/

private class ActorInternal[Msg, State]:
  protected val queue: Queue[IO, Msg]
  protected val state: Ref[IO, State]

  def actorRef(withBehaviour: ActorInternalBehaviour[Msg, State]): ActorRef[Msg] =
    val fiber: IO[FiberIO[Unit]] = state.get.flatMap[Unit](withBehaviour.function).start
    new ActorRef[Msg]:
      override def !(msg: Msg): IO[Unit] = queue.offer(msg)

      override def cancel(): IO[Unit] = fiber.flatMap(_.cancel)

      override def join(): IO[Unit] = fiber.flatMap(_.join)
end ActorInternal

trait ActorRef[Msg]:
  def !(msg: Msg): IO[Unit]
  def stop(): IO[Unit]
  def join(): IO[Unit]
end ActorRef

class ActorFactory:
  def queueMaker[A]: IO[Queue[IO, A]]
  def refMaker[A]: IO[Ref[IO, A]]

  def createActor[ActorInternal[Msg, State]](): IO[ActorInternal[Msg, State]] = for {
    queue <- queueMaker
    state <- initialState
    stateRef <- refMaker
    () <- stateRef.set(state)
  } yield (new ActorInternal[Msg, State] {
    override val queue: Queue[IO, Msg] = queue
    override val state: Ref[IO, State] = stateRef
  })
end ActorFactory

object DefaultActorFactory extends ActorFactory:
  override def queueMaker[A]: IO[Queue[IO, A]] = Queue.bounded(5)
  override def refMaker[A]: IO[Ref[IO, A]] = Ref.of(_)
end DefaultActorFactory


/*
// defiterateEval[F[_], A](start: A)(f: (A) => F[A]): Stream[F, A]
val run: IO[Unit] = msgQueue.take { a =>
  Stream.iterateEval[IO, Msg](a)(_ => msgQueue.take).evalMap(function).compile.drain
}.flatten
*/

// SAM

class ActorInternalBehaviour[Msg, State]:
  def get: IO[Msg]
  def function: State => IO[Unit]
end ActorInternalBehaviour

@main def main() =
  IO.unit *> {
    for
      _ <- DefaultActorFactory.createActor(IO.unit, _ => IO.unit)
      // actor: ActorId <- Actor.new()
      // _ <- actor ! "Hola"
    yield ()
  }
package com.ivmoreau.actoresperros

import cats.effect
import cats.implicits.*
import cats.effect.{FiberIO, IO, IOApp, Ref}
import cats.effect.kernel.Fiber
import cats.effect.std.Queue
import fs2.Stream

trait Actor[Msg, State]:

  def get(using aM: ActorMethods[Msg, State]): IO[Msg] = aM.get

  def function(st: State)(using ActorMethods[Msg, State]): IO[Unit]

  def apply(init: State): IO[ActorRef[Msg]] = DefaultActorFactory.createActor(init).map { (ai: ActorInternal[Msg,
    State]) =>
    given ActorMethods[Msg, State] with
      override def get: IO[Msg] = ai.queue.take
    ai.actorRef(state => function(state))
  }
end Actor

trait ActorMethods[Msg, State]:
  def get: IO[Msg]
end ActorMethods

private trait ActorInternal[Msg, State]:
  protected[actoresperros] val queue: Queue[IO, Msg]
  protected val state: Ref[IO, State]

  def actorRef(f: State => IO[Unit]): ActorRef[Msg] =
    val fiber: IO[FiberIO[Unit]] = state.get.flatMap[Unit](f).start
    new ActorRef[Msg]:
      override def !(msg: Msg): IO[Unit] = queue.offer(msg)

      override def cancel(): IO[Unit] = fiber.flatMap(_.cancel)

      override def join(): IO[Unit] = fiber.flatMap(_.join).void
end ActorInternal

trait ActorRef[Msg]:
  def !(msg: Msg): IO[Unit]
  def cancel(): IO[Unit]
  def join(): IO[Unit]
end ActorRef

trait ActorFactory:
  def queueMaker[A]: IO[Queue[IO, A]]
  def refMaker[A]: A => IO[Ref[IO, A]]

  def createActor[Msg, State](initialState: State): IO[ActorInternal[Msg, State]] = for {
    newQueue <- queueMaker[Msg]
    stateRef <- refMaker[State](initialState)
  } yield new ActorInternal[Msg, State] {
    override val queue: Queue[IO, Msg] = newQueue
    override val state: Ref[IO, State] = stateRef
  }
end ActorFactory

object DefaultActorFactory extends ActorFactory:
  override def queueMaker[A]: IO[Queue[IO, A]] = Queue.bounded[IO, A](5)
  override def refMaker[A]: A => IO[Ref[IO, A]] = Ref.of[IO, A](_)
end DefaultActorFactory

object Calculator extends Actor[Int, Unit]:
  def function(st: Unit)(using ActorMethods[Int, Unit]): IO[Unit] = for
    msg <- get
    _ <- cats.effect.std.Console[IO].println(s"${msg + 420}")
  yield ()
end Calculator

object Main extends IOApp.Simple:
  override def run = IO.unit *> {
    for
      calc <- Calculator(())
      _ <- calc ! 420
      _ <-  cats.effect.std.Console[IO].println("GO{ODPFD")
      _ <- calc.join()
    yield ()
  }
package com.ivmoreau.actoresperros

import cats.implicits.*
import cats.effect.{IO, IOApp}
import cats.effect.std.Console
import scala.concurrent.duration.Duration
import com.ivmoreau.actoresperros.api.ActorMethods

object Calculator extends Actor:

  enum Msg:
    case Add(value: Int)
    case Stop
  end Msg

  type State = Unit

  type Result = Int

  def function(st: State)(using ActorMethods[Msg, Result]): IO[Int] = for
    msg <- get
    _ <- msg match
      case Msg.Add(value) =>
        Console[IO].println(s"Add: ${value + 420}") *> send(273) *> function(st)
      case Msg.Stop => Console[IO].println("Stop")
  yield 132948298
end Calculator

/*

OPTION 1

askTimed(askTimeout: FiniteDuration): IO[Result]

OPTION 2

askSecure(implicit secure: SecureAsk[Result]): IO[Result]

secure { implicit s =>

  _ <- sendSecure(273)

  _ <- askSecure
  _ <- askSecure // error

  _ <- sendSecure(273)  

}

*/


object Main extends IOApp.Simple:
  override def run = IO.unit *> {
    for
      calc <- Calculator(())
      h <- (IO.sleep(Duration("5s")) *> (calc ! Calculator.Msg.Add(420))).start
      calc2 <- Calculator(())
      _ <- calc2 ! Calculator.Msg.Add(69)
      asked <- calc2.?
      _ <- Console[IO].println(s"Asked: $asked")
      _ <- Console[IO].println("GO{ODPFD")
      _ <- calc ! Calculator.Msg.Add(10)
      h2 <- (IO.sleep(Duration("5s")) *> (calc ! Calculator.Msg.Stop)).start
      _ <- calc2 ! Calculator.Msg.Stop
      _ <- h.join
      _ <- h2.join
      value <- calc.join()
      _ <- Console[IO].println(s"Value: $value")
    yield ()
  }

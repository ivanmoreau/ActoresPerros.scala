# The ActoresPerros library - a Functional actor library for Scala 3 with Cats Effect

Inspired by the Erlang actor model, but now with 300% more cats and 420% more types!

## Usage

```scala
import com.ivmoreau.actoresperros.all.*

object YourActor extends Actor:

  type Msg = YourMessageType

  type State = YourStateType

  def function(st: State)(using ActorMethods[Msg]): IO[Unit] = IO.pure("Your function implementation").void
end YourActor
```

Then just execute the actor with `YourActor(initialState)` that returns an `IO[ActorRef[Msg]]` and you can send messages to it with `!`.

## Example

```scala
object Calculator extends Actor:

  enum Msg:
    case Add(value: Int)
    case Stop
  end Msg

  type State = Unit

  def function(st: State)(using ActorMethods[Msg]): IO[Unit] = for
    msg <- get
    _ <- msg match
      case Msg.Add(value) =>
        Console[IO].println(s"Add: ${value + 420}") *> function(st)
      case Msg.Stop => Console[IO].println("Stop")
  yield ()
end Calculator

object Main extends IOApp.Simple:
  override def run = IO.unit *> {
    for
      calc <- Calculator(())
      h <- (IO.sleep(Duration("5s")) *> (calc ! Calculator.Msg.Add(420))).start
      calc2 <- Calculator(())
      _ <- calc2 ! Calculator.Msg.Add(69)
      _ <- Console[IO].println("GO{ODPFD")
      _ <- calc ! Calculator.Msg.Add(10)
      h2 <- (IO.sleep(Duration("5s")) *> (calc ! Calculator.Msg.Stop)).start
      _ <- calc2 ! Calculator.Msg.Stop
      _ <- h.join
      _ <- h2.join
    yield ()
  }
```
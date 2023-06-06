package com.ivmoreau.actoresperros.api

import cats.implicits.*
import cats.effect.IO

trait ActorRef[Msg, Result]:
  /**
   * Send a message to the actor
   */
  def !(msg: Msg): IO[Unit]
  /**
    * Cancel the actor
    */
  def cancel(): IO[Unit]
  /**
    * Join the actor
    */
  def join(): IO[Result]
  /**
   * Await/Promise a message from the actor
   */
  def ? : IO[Result]
  /**
   * Size of the queue
   */
  def queueSize: IO[Int]
end ActorRef
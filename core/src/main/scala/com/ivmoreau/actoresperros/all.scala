package com.ivmoreau.actoresperros

object all {
  trait ActorMethods[Msg, Result] extends api.ActorMethods[Msg, Result]
  trait ActorRef[Msg, Result] extends api.ActorRef[Msg, Result]
  trait Actor extends com.ivmoreau.actoresperros.Actor
}

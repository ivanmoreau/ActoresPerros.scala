package com.ivmoreau.actoresperros

object all {
  trait ActorMethods[Msg] extends api.ActorMethods[Msg]
  trait ActorRef[Msg] extends api.ActorRef[Msg]
  trait Actor extends com.ivmoreau.actoresperros.Actor
}

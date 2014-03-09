package pl.lechglowiak.retry

import akka.actor.ActorRef

/**
 * Created by Lech Głowiak on 07.03.14.
 */


abstract trait Command

abstract trait CommandResult

case class CommandWithSender(command: Command, id: Long, sender: ActorRef)



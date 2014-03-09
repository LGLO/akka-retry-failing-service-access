package pl.lechglowiak.example

import akka.actor.{ActorRef, Props, Actor, ActorLogging}
import pl.lechglowiak.retry.{CommandFailure, CommandWithSender, CommandResult, Command}
import scala.util.Try

/**
 * Created by Lech Głowiak on 07.03.14.
 */

sealed abstract trait SpecificCommand extends Command
case class GetByUUID(uuid: String) extends SpecificCommand
case class SetIndicator(uuid: String, indicator: String, onOff: Boolean) extends SpecificCommand
case class SetSomeProperty(uuid: String, somePropertyValue: String) extends SpecificCommand

sealed abstract trait SpecificCommandResult extends CommandResult
case class GetByUUIDResult(thing: Try[String]) extends SpecificCommandResult
case class SetIndicatorResult(result: Try[Nothing]) extends SpecificCommandResult
case class SetSomePropertyResult(result: Try[Nothing]) extends SpecificCommandResult

object SpecificCommandExecutingActor {
  def props() = {
    Props(new SpecificCommandExecutingActor())
  }
}

class SpecificCommandExecutingActor() extends Actor with ActorLogging {

  override def receive: Actor.Receive = {
    case cws@CommandWithSender(storageCommand: SpecificCommand, id, originalSender) => {
      tryExecute(storageCommand, originalSender, cws)
    }
  }

  def tryExecute(command: SpecificCommand, originalSender: ActorRef, cws: CommandWithSender) {
    try {
      doExecute(command, originalSender, cws)
    } catch {
      case e: Exception => {
        sender ! CommandFailure(cws, e)
      }
    }
  }

  def doExecute(command: SpecificCommand, originalSender: ActorRef, cws: CommandWithSender) {
    command match {
      case GetByUUID(uuid) => {
        originalSender ! "placeholder_for_real_value"
      }
      case SetIndicator(uuid, indicator, onOff) => {
        originalSender ! "ok"
      }
      case SetSomeProperty(uuid, params) => {
        originalSender ! "ok"
      }
    }
  }
}

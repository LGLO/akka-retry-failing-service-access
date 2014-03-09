package pl.lechglowiak.retry

import akka.actor.{ActorRef, Props, ActorLogging, Actor}
import scala.util.Failure
import akka.event.LoggingReceive
import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * Created by Lech Głowiak on 09.03.14.
 */

case class CommandFailure(command: CommandWithSender, reason: Throwable)

object RetryActor{
  def props(workActor: ActorRef, interval: FiniteDuration, retriesNumber: Int) = {
    Props(new RetryActor(workActor, interval, retriesNumber))
  }
}

class RetryActor(workActor: ActorRef, interval: FiniteDuration, retriesNumber: Int) extends Actor with ActorLogging {

  val retries = scala.collection.mutable.Map.empty[Long, Int]

  override def receive = LoggingReceive{
    case c@CommandWithSender(command, id, originalSender) =>
      retries.put(id, 0)
      workActor ! c
    case CommandFailure(c@CommandWithSender(command, id, originalSender), reason) =>
      val retryNo = retries.get(id).orElse(Some(0)).get + 1
      log.debug(s"Retry no $retryNo for command with id: $id")
      if (retryNo < retriesNumber) {
        retries.update(id, retryNo)
        context.system.scheduler.scheduleOnce(interval, workActor, c)(context.dispatcher)
      } else {
        retries.remove(id)
        originalSender ! Failure(reason)
      }
  }
}


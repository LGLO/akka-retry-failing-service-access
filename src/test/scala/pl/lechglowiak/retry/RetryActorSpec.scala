package pl.lechglowiak.retry

import akka.testkit.{TestProbe, ImplicitSender, TestKit}
import akka.actor.ActorSystem
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
import org.scalatest.matchers.ShouldMatchers
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Failure
import pl.lechglowiak.example.GetByUUID

/**
 * Created by kot on 09.03.14.
 */
class RetryActorSpec extends TestKit(ActorSystem("retrying-actor-tests-as")) with ImplicitSender
with WordSpecLike with ShouldMatchers with BeforeAndAfterAll {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  val interval = 100 millis
  val triesNumber = 2

  "A RetryActor " should {
    val workActorProbe = TestProbe()
    val delegatingActor = TestProbe()
    val command = CommandWithSender(GetByUUID("DEVICE_UUID"), 42l, delegatingActor.ref)

    "forward message to work actor" in {
      val sut = system.actorOf(RetryActor.props(workActorProbe.ref, interval, triesNumber), "retryActor-1")
      within(100 millis) {
        delegatingActor.send(sut, command)
        workActorProbe.expectMsg(command)
      }
    }
    "resend command after 'interval' if received failure notification" in {
      within(interval, 2 * interval) {
        val sut = system.actorOf(RetryActor.props(workActorProbe.ref, interval, triesNumber), "retryActor-2")
        val reason = new IllegalArgumentException()
        workActorProbe.send(sut, CommandFailure(command, reason))
        workActorProbe.expectMsg(command)
      }
    }

    "send Failure to message original sender after 'triesNumber' CommandFailure" in {
      within((triesNumber - 1) * interval, (triesNumber + 1) * interval) {
        val sut = system.actorOf(RetryActor.props(workActorProbe.ref, interval, triesNumber), "retryActor-3")
        val reason = new IllegalArgumentException()
        workActorProbe.send(sut, CommandFailure(command, reason))
        workActorProbe.expectMsg(command)
        workActorProbe.send(sut, CommandFailure(command, reason))
        delegatingActor.expectMsg(Failure(reason))
      }
    }
  }
}

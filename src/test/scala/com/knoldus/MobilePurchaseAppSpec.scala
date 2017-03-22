package com.knoldus


import akka.actor.{ActorSystem, Props}
import akka.testkit.TestKit
import org.scalatest.{BeforeAndAfterAll, MustMatchers, WordSpecLike}

class MobilePurchaseAppSpec extends TestKit(ActorSystem("test-system")) with WordSpecLike
  with BeforeAndAfterAll with MustMatchers {


  override protected def afterAll(): Unit = {
    system.terminate()
  }


  "A PurchaseRequestHandler " must {


    "respond with Error message in case ItemsRequested More than 2" in {
      val ref = system.actorOf(Props(classOf[PurchaseRequestHandler], testActor))
      ref tell((2, PersonDetails("", "", 1, 1)), testActor)

      expectMsgPF() {
        case errorMessage: String =>
          errorMessage must be("\nCannot Order More Than One Item At a Time")
      }
    }

    "respond with value as response" in {
      val ref = system.actorOf(Props(classOf[PurchaseRequestHandler], testActor))
      ref tell((1, PersonDetails("Anmol", "Faridabad", 1, 1)), testActor)

      expectMsgPF() {
        case (itemsRequested: Int, customer: PersonDetails) =>
          (itemsRequested, customer) must be(1, PersonDetails("Anmol", "Faridabad", 1, 1))
      }


    }

  }

  "A ValidationActor " must {

    "respond with Error message in case ItemsRequested are Out of Stock" in {
      val ref = system.actorOf(Props(classOf[ValidationActor], testActor))
      ref tell((1001, PersonDetails("", "", 1, 1)), testActor)

      expectMsgPF() {
        case errorMessage: String =>
          errorMessage must be("\nSorry We Are Out Of Stock ..!! We Will Let You Know Once Available..!! ")
      }
    }

    "respond with value as response" in {
      //      val ref = system.actorOf(Props[PurchaseActor])
      val ref = system.actorOf(Props(classOf[ValidationActor], testActor))
      ref tell((1, PersonDetails("Anmol", "Faridabad", 1, 1)), testActor)

      expectMsgPF() {
        case customer: PersonDetails =>
          customer must be(PersonDetails("Anmol", "Faridabad", 1, 1))
      }


    }

  }

  "A PurchaseActor " must {

    "respond with Delivery message in case of Success" in {
      val ref = system.actorOf(Props[PurchaseActor])
      ref tell(PersonDetails("", "", 1, 1), testActor)

      expectMsgPF() {
        case message: String =>
          message must be("\nYour Order Has been Confirmed.!! Your Galaxy S8 is On it's Way.!! Thank you for the Purchase !!")
      }
    }
  }
}




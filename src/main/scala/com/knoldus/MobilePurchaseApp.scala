package com.knoldus

import akka.actor._
import akka.pattern.ask
import akka.routing.BalancingPool
import akka.util.Timeout
import org.apache.log4j.Logger

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

case class PersonDetails(name: String, address: String, cardNumber: Long, phoneNumber: Long)

class PurchaseRequestHandler(ref: ActorRef) extends Actor with ActorLogging {

  override def receive: Receive = {
    case (itemsRequested: Int, customer: PersonDetails) => {
      log.info("\n:: Inside PurchaseRequestHandler ::")
      if (itemsRequested == 1) {
        ref forward(itemsRequested, customer)
      }
      else {
        sender() ! "\nCannot Order More Than One Item At a Time"
      }
    }
  }
}

class ValidationActor(ref: ActorRef) extends Actor with ActorLogging {

  var availableItems = 1000

  override def receive: Receive = {
    case (itemsRequested: Int, customer: PersonDetails) => {
      log.info("\n:: Inside ValidationActor ::")

      if (itemsRequested <= availableItems) {
        log.info("\nYour Galaxy S8 Costs Rs 62000 ..!!! Please Pay the Amount.!! \n ____Forwarding To PaymentGateway_____ ")
        availableItems -= itemsRequested
        log.info(s"\nRemaining Items $availableItems")
        ref forward (customer)
      }
      else {
        sender() ! "\nSorry We Are Out Of Stock ..!! We Will Let You Know Once Available..!! "
      }
    }
  }
}

class PurchaseActor extends Actor with ActorLogging {
  override def receive: Receive = {

    case (customer: PersonDetails) => {
      log.info("\n:: Inside PurchaseActor ::")

      log.info("\nPayment Recieved")
      log.info("\nPayment Summary : ")
      log.info(s"\nName of the Customer : ${customer.name}")
      log.info(s"\nAddress of the Customer : ${customer.address}")
      log.info(s"\nCredit Card Number of the Customer : ${customer.cardNumber}")
      log.info(s"\nContact Number of the Customer : ${customer.phoneNumber}")

      sender() ! ("\nYour Order Has been Confirmed.!! Your Galaxy S8 is On it's Way.!! Thank you for the Purchase !!")
    }
  }
}

object MobilePurchaseApp extends App {
  val log = Logger.getLogger(this.getClass)
  implicit val timeout = Timeout(1000 seconds)

  log.info("Welcome To Samsung World .!!!!!! Let Us Help You Buy Your Dream Mobile!!! ")
  val system = ActorSystem("GalaxyS8Booking")

  val purchaseGateway = system.actorOf(BalancingPool(5).props(Props[PurchaseActor]))

  val validationSystem = system.actorOf(Props(classOf[ValidationActor], purchaseGateway), "Validation")


  val purchaseCounter1 = system.actorOf(Props(classOf[PurchaseRequestHandler], validationSystem), "PurchaseCounter1")
  val purchaseCounter2 = system.actorOf(Props(classOf[PurchaseRequestHandler], validationSystem), "PurchaseCounter2")


  val customer1 = PersonDetails("Anmol", "Faridabad", 1234567890, 9876543210l)
  val customer2 = PersonDetails("Nitin", "GuruGram", 1234567890, 9876543211l)
  val customer3 = PersonDetails("Pankhurie", "Delhi", 1234567890, 9876543212l)
  val customer4 = PersonDetails("Shivangi", "Ghaziabad", 1234567890, 9876543213l)
  val customer5 = PersonDetails("Neha", "Ghaziabad", 1234567890, 9876543214l)
  val customer6 = PersonDetails("Kunal", "Noida", 1234567890, 9876543215l)


  val result1 = purchaseCounter1 ?(1, customer1)
  val result2 = purchaseCounter2 ?(1, customer2)
  //  val result3=purchaseCounter1 ?(1,customer3)
  //  val result4= purchaseCounter1 ? (2,customer4)
  //  val result5=purchaseCounter2 ?(2,customer5)
  //  val result6=purchaseCounter2 ? (1,customer6)


  result1.map(log.info(_))
  result2.map(log.info(_))
  //   result3.map(log.info(_))
  //result4.map(log.info(_))
  //result5.map(log.info(_))
  //result6.map(log.info(_))


}

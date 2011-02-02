package com.notnoop.gym.fitincentives
package model

import org.specs._
import org.specs.runner.JUnit4
import org.specs.runner.ConsoleRunner
import net.liftweb._
import http._
import net.liftweb.util._
import net.liftweb.common._
import org.specs.matcher._
import org.specs.specification._
import Helpers._
import lib._


class TransactionCalcTestSpecsAsTest extends JUnit4(TransactionCalcTestSpecs)
object TransactionCalcTestSpecsRunner extends ConsoleRunner(TransactionCalcTestSpecs)

object TransactionCalcTestSpecs extends Specification {


  "Transaction calculator" should {
    val displayName = "SampleOne"
    val eventId = "PT"
    val trigger = Trigger.createRecord.displayName(displayName).
        reward(100).triggerDescription(eventId).countOffset(2).
        resetFrequency(Recurring.annually)

    "doesn't credit no events" in {
      trigger.creditTransactions(List(), List()) must beEmpty
    }

    "doesn't credit less than offset events" in {
      trigger.creditTransactions(
        List(Event(1L, new java.util.Date, eventId)),
        List()) must beEmpty
    }

    "credit once offset passed" in {
      trigger.creditTransactions(
        List(Event(0L, new java.util.Date, eventId),
          Event(1L, new java.util.Date, eventId),
          Event(2L, new java.util.Date, eventId)),
        List()) must have size(1)
    }

    "credit multiple times if member is being great" in {
      trigger.creditTransactions(
        List(
          Event(0L, new java.util.Date, eventId),
          Event(1L, new java.util.Date, eventId),
          Event(2L, new java.util.Date, eventId),
          Event(3L, new java.util.Date, eventId)),
        List()) must have size(2)
    }

    "credit multiple times if member is being great with respect to order" in {
      val transactions = trigger.creditTransactions(
        List(
          Event(0L, new java.util.Date, eventId),
          Event(1L, new java.util.Date, eventId),
          Event(2L, new java.util.Date, eventId),
          Event(3L, new java.util.Date, eventId)),
        List())

      transactions must have size(2)
      transactions(0) must beLike {
        case Transaction(1L, _, _, _, 1L, 100) => true
      }
      transactions(1) must beLike {
        case Transaction(2L, _, _, _, 3L, 100) => true
      }
    }

    "doesn't credit the same event multiple times" in {
      trigger.creditTransactions(
        List(
          Event(0L, new java.util.Date, eventId),
          Event(1L, new java.util.Date, eventId),
          Event(2L, new java.util.Date, eventId)),
        List(Transaction(0L, new java.util.Date, displayName, displayName, 1L, 100))
      ) must beEmpty
    }

    "credits again if user is awesome but was credit before as well" in {
      trigger.creditTransactions(
        List(Event(0L, new java.util.Date, eventId),
          Event(1L, new java.util.Date, eventId),
          Event(2L, new java.util.Date, eventId),
          Event(3L, new java.util.Date, eventId)),
        List(Transaction(0L, new java.util.Date, displayName, displayName, 1L, 100))
      ) must have size(1)
    }

    "ignores other events not of interest" in {
      trigger.creditTransactions(
        List(
          Event(0L, new java.util.Date, eventId + "A"),
          Event(1L, new java.util.Date, eventId + "A"),
          Event(2L, new java.util.Date, eventId + "A"),
          Event(3L, new java.util.Date, eventId + "A")),
        List()) must beEmpty
    }

    "ignores other events not of interest while considering interesting events" in {
      trigger.creditTransactions(
        List(
          Event(0L, new java.util.Date, eventId),
          Event(1L, new java.util.Date, eventId + "A"),
          Event(2L, new java.util.Date, eventId + "A"),
          Event(3L, new java.util.Date, eventId)),
        List()) must have size(1)
    }
  }
}

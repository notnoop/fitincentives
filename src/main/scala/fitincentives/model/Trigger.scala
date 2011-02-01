package com.notnoop.gym.fitincentives
package model

import java.util.Date

import net.liftweb.util.Helpers

import net.liftweb.record.LifecycleCallbacks
import net.liftweb.record.field._

import net.liftweb.mongodb.record._
import net.liftweb.mongodb.record.field._

class Trigger extends MongoRecord[Trigger] with MongoId[Trigger]
  with CreatedUpdated[Trigger] {
  def meta = Trigger

  object displayName extends StringField(this, 255)
  object reward extends IntField(this)

  // Rules
  object triggerDescription extends StringField(this, 255)
  object countOffset extends IntField(this)
  object resetFrequency extends EnumField(this, Recurring)

  def creditTransactions(
    events: Iterable[Event],
    transactions: Iterable[Transaction]): List[Transaction] = {


    val lastEventId: Long = transactions.
            filter(_.description == displayName.is).
            map(_.lastEvent).
            lastOption.
            getOrElse(-1L)

    val eventsToConsider = events.
            filter(_.id > lastEventId).
            filter(_.description == triggerDescription.is)

    val resetGrouped = resetFrequency.is match {
      case Recurring.monthly => eventsToConsider.groupBy(_.date.getMonth).values
      case Recurring.annually => eventsToConsider.groupBy(_.date.getYear).values
      case Recurring.lifeTime => List(eventsToConsider)
    }

    val counted = resetGrouped.
        map(_.grouped(countOffset.is))

    var lastTransId = transactions.lastOption.map(_.id) getOrElse 0L
    val newTransactions = counted.flatMap(group => group.
        filter(_.size >= countOffset.is).
        map(ts => {
          lastTransId += 1
          Transaction(lastTransId, ts.toList.last.date, displayName.is, displayName.is,
            ts.toList.last.id, reward.is)
        }).toList
    )

    newTransactions.toList
  }
}

object Trigger extends Trigger with MongoMetaRecord[Trigger]

object Recurring extends Enumeration {
  val lifeTime = Value("Life Time")
  val monthly = Value("Monthly")
  val annually = Value("Annually")
}

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
}

object Trigger extends Trigger with MongoMetaRecord[Trigger]

object Recurring extends Enumeration {
  val liftTime = Value("Life Time")
  val monthly = Value("Monthly")
  val annually = Value("Annually")
}

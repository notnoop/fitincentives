package com.notnoop.gym.fitincentives
package model

import java.util.Date

import net.liftweb.util.Helpers

import net.liftweb.record.LifecycleCallbacks
import net.liftweb.record.field._

import net.liftweb.mongodb.record._
import net.liftweb.mongodb.record.field._

class Member extends MongoRecord[Member] with MongoId[Member]
  with CreatedUpdated[Member] {

  def meta = Member

  object firstName extends StringField(this, 255)
  object lastName extends StringField(this, 255)

  object fullName extends StringField(this, 255)

  object email extends EmailField(this, 255)
  object phoneNumber extends StringField(this, 255)

  object balance extends IntField(this)

  object transactions extends MongoCaseClassListField[Member, Transcation](this)
  object events extends MongoCaseClassListField[Member, Event](this)
}

object Member extends Member with MongoMetaRecord[Member]

case class Transcation(
  id: Long,
  date: Date,
  entryType: String,
  description: String,
  reward: Int
)

case class Event(
  id: String,
  date: Date,
  description: String
)

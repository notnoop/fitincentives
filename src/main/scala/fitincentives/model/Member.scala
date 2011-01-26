package com.notnoop.gym.fitincentives
package model

import java.util.Calendar

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

  object entries extends MongoCaseClassListField[Member, Entry](this)
}

object Member extends Member with MongoMetaRecord[Member]

case class Entry(
  date: Calendar,
  entryType: String,
  description: String,
  reward: Int
)

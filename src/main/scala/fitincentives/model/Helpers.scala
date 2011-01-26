package com.notnoop.gym.fitincentives
package model

import java.util.Calendar

import net.liftweb.record._
import field._

import net.liftweb.util.FieldContainer

trait CreatedTrait[Owner <: Record[Owner]] {
  self: Record[Owner] =>

  import net.liftweb.util._

  /**
   * Override this method to index the createdAt field
   */
  protected def createdAtIndexed_? = false

  lazy val createdAt: DateTimeField[Owner] = new MyCreatedAt(this)

  protected class MyCreatedAt(obj: self.type) extends
  DateTimeField[Owner](obj.asInstanceOf[Owner]) {
    override def defaultValue = Calendar.getInstance
//    override def dbIndexed_? = createdAtIndexed_?
  }
}

trait UpdatedTrait[Owner <: Record[Owner]] {
  self: Record[Owner] =>

  import net.liftweb.util._

  /**
   * Override this method to index the updatedAt field
   */
  protected def updatedAtIndexed_? = false

  /**
   * The updatedAt field.  You can change the behavior of this
   * field:
   * <pre name="code" class="scala">
   * override lazy val updatedAt = new MyUpdatedAt(this) {
   *   override def dbColumnName = "i_eat_time_for_breakfast"
   * }
   * </pre>
   */
  lazy val updatedAt: MyUpdatedAt = new MyUpdatedAt(this)

  protected class MyUpdatedAt(obj: self.type) extends
  DateTimeField(obj.asInstanceOf[Owner]) with LifecycleCallbacks {
    override def beforeSave() {super.beforeSave; this.set(Calendar.getInstance)}
    override def defaultValue = Calendar.getInstance
//       override def dbIndexed_? = updatedAtIndexed_?
  }
}

trait CreatedUpdated[Owner <: Record[Owner]] extends CreatedTrait[Owner] with UpdatedTrait[Owner] {
  self: Record[Owner] =>
}

package com.notnoop.gym.fitincentives
package lib
package fetchers

import com.gargoylesoftware.htmlunit._
import html._

class FoursquareMayorFetcher {
  val namePtr = """>([^<]+)<""".r
  def fetchMayor(pageId: String) = {
    val pageId = "1162541"
    import dispatch._

    val http = new Http
    val req = :/("foursquare.com") / "venue" / pageId
    val res = http(req as_str)
    res.split("\n").find(_.contains("""<div class="name">""")).
        map(namePtr.findFirstMatchIn(_).get.group(1))
  }
}

package com.notnoop.gym.fitincentives
package lib
package fetchers

import com.gargoylesoftware.htmlunit._
import html._

class FacebookFetcher(username: String, password: String) {

  def login(): WebClient = {
    val webClient = new WebClient()
    webClient.setJavaScriptEnabled(false)
    val page1: HtmlPage = webClient.getPage("http://www.facebook.com")
    val form = page1.getForms().get(0)
    val button = form.getInputsByValue("Login").get(0).asInstanceOf[HtmlSubmitInput]
    val textField: HtmlTextInput = form.getInputByName("email")
    textField.setValueAttribute(username)
    val textField2: HtmlPasswordInput = form.getInputByName("pass")
    textField2.setValueAttribute(password)
    button.click()
    webClient
  }

  def fetchFansInternal(pageId: String): String = {
    val client = login()
    val page: Page = client.getPage(
      "http://www.facebook.com/ajax/social_graph/dialog/popup.php?id="
      + pageId + "&__a=1&__d=1")
    val text = page.getWebResponse.getContentAsString
    text
  }

  val titles = """\\"title\\":\\"([^"]+)\\"""".r
  def fansOf(pageId: String): List[String] = {
    val fansInternal = fetchFansInternal(pageId)
    val allTitles = titles.findAllIn(fansInternal)

    allTitles.map(s => s.substring(12, s.length - 2)).toList
  }
}

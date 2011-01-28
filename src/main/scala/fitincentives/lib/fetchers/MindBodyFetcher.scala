package com.notnoop.gym.fitincentives
package lib
package fetchers

import org.apache.http.impl.client._
import org.apache.http.client.methods._
import org.apache.http.client._

import scala.io.Source
import org.apache.http.message._

import com.gargoylesoftware.htmlunit._
import html._

import collection.JavaConversions._

class MindBodyFetcher(username: String, password: String, siteId: String) {
  val client = new DefaultHttpClient

  def login() = {
    val getR = new HttpGet("https://clients.mindbodyonline.com/ASP/home.asp?studioid="+siteId)
    client.execute(getR).getEntity().consumeContent()

    val formparam = List(
      new BasicNameValuePair("requiredtxtUserName", username),
      new BasicNameValuePair("requiredtxtPassword", password),
      new BasicNameValuePair("optRememberMe", "false"),
      new BasicNameValuePair("tabID", "7"),
      new BasicNameValuePair("optTG", ""),
      new BasicNameValuePair("pageNum", "1")
    )

    val post = new HttpPost(
      "https://clients.mindbodyonline.com/ASP/login_p.asp")
    post.setEntity(
      new entity.UrlEncodedFormEntity(formparam, "UTF-8"))

    val response = client.execute(post)
    val source = Source.fromInputStream(response.getEntity.getContent)
    source.mkString
  }

  def generateReport(from: String, to: String) = {
    val formparam = List(
        new BasicNameValuePair("PostAction", "Generate"),
        new BasicNameValuePair("sr-range-opt", ""),
        new BasicNameValuePair("sr-name", ""),
        new BasicNameValuePair("txtFilterState1", "filter-open"),
        new BasicNameValuePair("requiredtxtDateStart", from),
        new BasicNameValuePair("requiredtxtDateEnd", to),
        new BasicNameValuePair("txtFilterState2", "filter-open"),
        new BasicNameValuePair("optFilterTagged", "false"),
        new BasicNameValuePair("optSaleLoc", "0"),
        new BasicNameValuePair("optHomeStudio", "0"),
        new BasicNameValuePair("optPayMethod", "0"),
        new BasicNameValuePair("optCategory", "0"),
        new BasicNameValuePair("optEmployee", ""),
        new BasicNameValuePair("optRep", ""),
        new BasicNameValuePair("optIncludeAutoRenews", "0"),
        new BasicNameValuePair("txtFilterState3", ""),
        new BasicNameValuePair("optDisMode", "1"),
        new BasicNameValuePair("optBasis", "0"),
        new BasicNameValuePair("optShowSupplier", "false")
    )
    val post = new HttpPost("https://clients.mindbodyonline.com/web/mvc.aspx/Report/Sales/Sales/Generate")
    post.setEntity(
        new entity.UrlEncodedFormEntity(formparam, "UTF-8"))

    val response = client.execute(post)
    val source = Source.fromInputStream(response.getEntity.getContent)
    source.mkString
  }

  def getSales(from: String, to: String) = {
    login()
    val r = generateReport(from, to)
    val client = new WebClient()
    client.setJavaScriptEnabled(false)
    client.setCssEnabled(false)


    val response = new StringWebResponse(r, new java.net.URL("http://example.com"))
    val html = HTMLParser.parseHtml(response, client.getCurrentWindow())

    def clean(s: String) = s.replaceAll("[^\\p{ASCII}]", "").trim
    def isInt(s: String) = try { s.trim.toInt; true } catch { case _ => false }
    def isSale(row: HtmlTableRow) = row.getCells.size == 17 && isInt(clean(row.getCells.get(0).getTextContent))

    val table = html.getHtmlElementById("result-table").asInstanceOf[HtmlTable]

    for (row <- table.getRows() if isSale(row))
      yield MindBodySale(
        clean(row.getCells().get(0).getTextContent()),
        clean(row.getCells().get(1).getTextContent()),
        clean(row.getCells().get(2).getTextContent()),
        clean(row.getCells().get(3).getTextContent())
      )
  }

}

case class MindBodySale(
  saleId: String,
  dateOfPurchase: String,
  client: String,
  description: String
)

package com.notnoop.gym.fitincentives
package lib
package fetchers

import org.apache.http.impl.client._
import org.apache.http.client.methods._
import org.apache.http.client._

import scala.io.Source
import org.apache.http.message._

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

  def generateReport() = {
    val formparam = List(
        new BasicNameValuePair("PostAction", "Generate"),
        new BasicNameValuePair("sr-range-opt", ""),
        new BasicNameValuePair("sr-name", ""),
        new BasicNameValuePair("txtFilterState1", "filter-open"),
        new BasicNameValuePair("requiredtxtDateStart", "1/27/2011"),
        new BasicNameValuePair("requiredtxtDateEnd", "1/27/2011"),
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
}

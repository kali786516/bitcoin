package com.tookitaki.assignment.bitcoin.utils

import java.net.URL

import org.json.JSONObject

import scala.collection.JavaConversions._
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.sql.Timestamp

import com.tookitaki.assignment.bitcoin.constants.JsonParsingConstants._
import com.tookitaki.assignment.bitcoin.constants.StringConstants.{InputDateFormat, ThresholdDate}
import com.tookitaki.assignment.bitcoin.execution.BitCoinPriceRanges.getPriceRangesForTimeFrame
import HelperUtils._
import com.tookitaki.assignment.bitcoin.enums.TimeFrame
import com.tookitaki.assignment.bitcoin.SpringBootRestApplication.historicPrices

/**
  * Contains all util methods for the application
  */
object CommonUtils {

  /**
    *
    * @param url URL
    * @return JsonObject given by the url
    */
  def getJsonFromUrl(url: String): JSONObject = {

    val is = new URL(url).openStream
    try {
      val rd = new BufferedReader(new InputStreamReader(is, Charset.forName(CharSet)))
      val jsonText = rd.lines().toArray.mkString
      val json = new JSONObject(jsonText)
      json
    } finally is.close()
  }

  /**
    *
    * @param jSONObject Json object of proces
    * @return Map of timeStamp and price
    */
  def getDatePriceMap(jSONObject: JSONObject): List[(Timestamp, Double)] =
    jSONObject.getJSONObject(Data).getJSONArray(Prices).toList.
      map(_.asInstanceOf[java.util.HashMap[String, String]]).
      map(mapOfTimePrice => (toTimeStamp(mapOfTimePrice(Time)), mapOfTimePrice(Price).toDouble)).
      sortBy(tuple => tuple._1)(ordered).toList


  /**
    *
    * @return Average rate of price range from [[ThresholdDate]]
    */
  def averageRateOfPriceChange: Double = {

    val requiredPrices = getPriceRangesForTimeFrame(TimeFrame.CustomDate, ThresholdDate).reverse
    var prevPrice = requiredPrices.head._2

    val rateOfChanges = requiredPrices.tail.map(tuple => {
      val rateOfChange = (Math.abs(prevPrice - tuple._2) / prevPrice) * 100
      prevPrice = tuple._2
      rateOfChange
    })
    val meanRate = rateOfChanges.sum / rateOfChanges.size

    meanRate
  }

  /**
    *
    * @return Current Days price
    */
  def todayPrice: Double = historicPrices.head._2

  /**
    *
    * @return maximum number of days can be considered for prediction
    */
  def maxNumOfDaysForPrediction:Int = getDateDiff(toTimeStamp(ThresholdDate, InputDateFormat),
    new Timestamp(System.currentTimeMillis()))
}

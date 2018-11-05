package com.tookitaki.assignment.bitcoin

import java.sql.Timestamp

import org.json.JSONObject
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.SpringApplication
import com.tookitaki.assignment.bitcoin.utils.CommonUtils._
import com.tookitaki.assignment.bitcoin.constants.JsonParsingConstants

/**
  * Main class for rest application
  *
  * Initialize historic prices map when application is started
  */

@SpringBootApplication
class SpringBootRestApplication

object SpringBootRestApplication {

  var historicPrices: List[(Timestamp, Double)] = List.empty

  def main(args: Array[String]): Unit = {

    val json: JSONObject = getJsonFromUrl(JsonParsingConstants.HistoricPricesUrl)
    historicPrices = getDatePriceMap(json)
    SpringApplication.run(classOf[SpringBootRestApplication])
  }
}
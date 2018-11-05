package com.tookitaki.assignment.bitcoin.constants

/**
  * Constants of historic price json
  */
object JsonParsingConstants {

  final val HistoricPricesUrl = "https://www.coinbase.com/api/v2/prices/BTC-USD/historic?period=all"
  final val CharSet = "UTF-8"
  final val Data = "data"
  final val Prices = "prices"
  final val Time = "time"
  final val Price = "price"
  final val JsonTimeStampFormat = "yyyy-MM-dd'T'hh:mm:ss'Z'"
}
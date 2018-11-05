package com.tookitaki.assignment.bitcoin.enums

/**
  * All trading decisions available
  */
object TradingDecision extends Enumeration {

  type TradingDecision = Value
  val Sell, Buy, Hold = Value
}

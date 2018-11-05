package com.tookitaki.assignment.bitcoin.enums

/**
  *All possible time frames value
  */

object TimeFrame extends Enumeration {

  type TimeFrame = Value
  val Weekly, Monthly, Yearly, CustomDate = Value
}
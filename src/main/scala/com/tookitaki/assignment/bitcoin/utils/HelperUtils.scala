package com.tookitaki.assignment.bitcoin.utils

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

import com.tookitaki.assignment.bitcoin.constants.JsonParsingConstants

/**
  * Object contains helper utils for the application
  */
object HelperUtils {

  /**
    *
    * @return implicit ordering for time stamp
    */
  implicit def ordered: Ordering[Timestamp] = new Ordering[Timestamp] {
    def compare(x: Timestamp, y: Timestamp): Int = y compareTo x
  }

  /**
    *
    * @param s          String
    * @param dateFormat DateFormat
    * @return Converts string into timestamp
    */
  def toTimeStamp(s: String, dateFormat: String = JsonParsingConstants.JsonTimeStampFormat): Timestamp = {
    val simpleDateFormat = new SimpleDateFormat(dateFormat)
    val parsedDate = simpleDateFormat.parse(s)
    new Timestamp(parsedDate.getTime)
  }

  /**
    *
    * @param start start date
    * @param end end date
    * @return difference between the start date and end date
    */
  def getDateDiff(start: Timestamp, end: Timestamp): Int =
    Math.abs(TimeUnit.DAYS.convert(end.getTime - start.getTime, TimeUnit.MILLISECONDS).toInt)
}

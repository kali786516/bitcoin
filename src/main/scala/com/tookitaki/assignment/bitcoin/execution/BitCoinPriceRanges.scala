package com.tookitaki.assignment.bitcoin.execution

import java.sql.Timestamp
import com.tookitaki.assignment.bitcoin.SpringBootRestApplication.historicPrices
import com.tookitaki.assignment.bitcoin.constants.NumericConstants.{MonthCount, WeekCount, YearCount}
import com.tookitaki.assignment.bitcoin.constants.StringConstants.{EmptyString, InputDateFormat}
import com.tookitaki.assignment.bitcoin.enums.TimeFrame
import com.tookitaki.assignment.bitcoin.utils.HelperUtils.{getDateDiff, toTimeStamp}

/**
  * It is the object which solves following operations
  * 1) return tuples of (TimeStamp, Price) for a given TimeFrame
  * 2) return rolling averages of all values between given start_date , end_date and rolling size
  */
object BitCoinPriceRanges {

  /**
    *
    * @param timeFrame : TimeFrame Value
    * @param customDate : Custom Date entered by user
    * @return : list of tuples of (TimeStamp, Price) for a given TimeFrame
    */
  def getPriceRangesForTimeFrame(timeFrame: TimeFrame.Value,
                                 customDate: String = EmptyString): List[(Timestamp, Double)] = {

    timeFrame match {
      case TimeFrame.Weekly => historicPrices.take(WeekCount)
      case TimeFrame.Monthly => historicPrices.take(MonthCount)
      case TimeFrame.Yearly => historicPrices.take(YearCount)
      case TimeFrame.CustomDate => historicPrices.take(
        getDateDiff(toTimeStamp(customDate, InputDateFormat), new Timestamp(System.currentTimeMillis())))
    }
  }

  /**
    *
    * @param start : Start Date
    * @param end : End Date
    * @param rollingSize : Rolling Size
    * @return : Rolling averages of all values between given start_date , end_date and rolling size
    */
  def getRollingAveragePriceRanges(start: String,
                                   end: String,
                                   rollingSize: Int): List[(Timestamp, Double)] = {

    val startDate = toTimeStamp(start, InputDateFormat)
    val endDate = toTimeStamp(end, InputDateFormat)

    val reqHistoricPrices = historicPrices.
      filter(tuple => tuple._1.after(startDate) && tuple._1.before(endDate)).
      reverse.map(tuple => (tuple._1, tuple._2.toDouble))
    val itr = reqHistoricPrices.dropRight(rollingSize - 1).toIterator
    var prevSum = reqHistoricPrices.take(rollingSize).map(_._2).sum
    val sumItr = reqHistoricPrices.drop(rollingSize)
    var prevElem = itr.next()._2
    ((reqHistoricPrices.take(rollingSize).last._1, prevSum) :: sumItr.map(elem => {
      val newSum = prevSum - prevElem + elem._2
      prevElem = itr.next()._2
      prevSum = newSum
      (elem._1, newSum)
    })).map(tuple => (tuple._1, Math.round((tuple._2 * 100.0) / rollingSize) / 100.0))
  }

}

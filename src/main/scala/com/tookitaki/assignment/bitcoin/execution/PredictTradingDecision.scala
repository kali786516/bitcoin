package com.tookitaki.assignment.bitcoin.execution

import com.cloudera.sparkts.models.ARIMA
import org.apache.spark.mllib.linalg.Vectors
import com.tookitaki.assignment.bitcoin.enums.{TimeFrame, TradingDecision}
import com.tookitaki.assignment.bitcoin.utils.CommonUtils._
import com.tookitaki.assignment.bitcoin.constants.NumericConstants._
import com.tookitaki.assignment.bitcoin.constants.StringConstants.ThresholdDate
import com.tookitaki.assignment.bitcoin.execution.BitCoinPriceRanges.getPriceRangesForTimeFrame

/**
  * This object is used to predict BitCoin prices for next 'X' days using ARIMA and
  * suggest trading decision to the user
  */
object PredictTradingDecision {

  /**
    *
    * @param numOfDays : Number of days used to train the model
    * @return Trading decision
    */
  def predictDecision(numOfDays: Int = maxNumOfDaysForPrediction): TradingDecision.Value = {

    val nextPriceRanges = predictNextXDaysPrices(numOfDays)
    val maxPrice = nextPriceRanges.max
    val rateOfChange = Math.abs(((maxPrice - todayPrice) / todayPrice) * 100)
    if (rateOfChange < averageRateOfPriceChange)
      TradingDecision.Hold
    else if (maxPrice > todayPrice)
      TradingDecision.Buy
    else
      TradingDecision.Sell
  }

  /**
    * ARIMA is used to predict the next time series values
    *
    * @param numOfDays : Number of days used to train the model
    * @return Array of prices for the next [[NumOfPredictionUnits]]
    */
  def predictNextXDaysPrices(numOfDays: Int): Array[Double] = {

    val prices = getPriceRangesForTimeFrame(TimeFrame.CustomDate, ThresholdDate).
      reverse.map(_._2).takeRight(numOfDays)
    val ts = Vectors.dense(prices.toArray)
    val arimaModel = ARIMA.fitModel(AutoRegressiveOrder, DifferencingOrder, MovingAverageOrder, ts)
    val forecast = arimaModel.forecast(ts, NumOfPredictionUnits)
    forecast.toArray.takeRight(NumOfPredictionUnits)
  }
}

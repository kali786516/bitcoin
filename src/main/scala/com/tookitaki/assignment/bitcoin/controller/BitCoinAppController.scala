package com.tookitaki.assignment.bitcoin.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation._
import com.tookitaki.assignment.bitcoin.constants.HtmlFileConstants._
import com.tookitaki.assignment.bitcoin.enums.TimeFrame
import com.tookitaki.assignment.bitcoin.execution.BitCoinPriceRanges._
import com.tookitaki.assignment.bitcoin.constants.StringConstants.{EmptyString, DefaultTimeFrame}
import com.tookitaki.assignment.bitcoin.utils.CommonUtils.maxNumOfDaysForPrediction
import com.tookitaki.assignment.bitcoin.execution.PredictTradingDecision

/**
  * Controller class for rest API
  */

@Controller
class BitCoinAppController {

  /**
    *
    * @return html file with all operations supported displayed in a page
    */
  @RequestMapping(Array("/home"))
  def showHomePage(): String = HomePageFile

  /**
    *
    * @return return html file with time frame options to select
    */
  @RequestMapping(Array("/getTimeFrameOptions"))
  def showTimeFrameOptions(): String = TimeFrameOptionsFile

  /**
    *
    * @return a form to fill start date, end date and rolling size
    */
  @RequestMapping(Array("/getRollingAverageForm"))
  def showRollingAverageForm(): String = RollingAverageFile

  /**
    *
    * @return a form to enter number of days
    */
  @RequestMapping(Array("/getTradingDecisionForm"))
  def showTradingDecisionForm(): String = TradingDecisionFile

  /**
    *
    * @param n number of days to be considered for training of ML model
    * @return trading decision
    */
  @GetMapping(Array("/getTradingDecision"))
  @ResponseBody
  def getTradingDecision(@RequestParam(name = "numDays", required = false, defaultValue = EmptyString)
                         n: Int = maxNumOfDaysForPrediction): String = {
    val numDays = if (n > 0) n else maxNumOfDaysForPrediction
    PredictTradingDecision.predictDecision(numDays).toString
  }

  /**
    *
    * @param timeFrame : Time Frame option selected by user
    * @param date      : date is entered when custom date option is selected
    * @return json string with x and y values to be plotted
    */
  @GetMapping(Array("/getGraphTimeFrame"))
  @ResponseBody
  def getTimeFrameHistoricPrices(@RequestParam(name = "timeFrames", required = true, defaultValue = DefaultTimeFrame)
                                 timeFrame: String,
                                 @RequestParam(name = "date", required = false, defaultValue = EmptyString)
                                 date: String): String = {

    val priceRanges = getPriceRangesForTimeFrame(TimeFrame.withName(timeFrame), date).
      map(tuple => ( s""""${tuple._1.toLocalDateTime.toLocalDate.toString}"""", tuple._2.toDouble))

    s"""{"x" : ["date",${priceRanges.map(_._1).mkString(",")}],
       |"y" : ["prices",${priceRanges.map(_._2).mkString(",")}]}""".stripMargin
  }

  /**
    *
    * @param startDate   : start date
    * @param endDate     end date
    * @param rollingSize rolling size
    * @return json string with x and y values to be plotted
    */
  @GetMapping(Array("/getRollingAverageGraph"))
  @ResponseBody
  def getRollingAveragePrices(@RequestParam(name = "startDate", required = true)
                              startDate: String,
                              @RequestParam(name = "endDate", required = true)
                              endDate: String,
                              @RequestParam(name = "rollingSize", required = true)
                              rollingSize: Int): String = {

    val priceRanges = getRollingAveragePriceRanges(startDate, endDate, rollingSize).
      map(tuple => ( s""""${tuple._1.toLocalDateTime.toLocalDate.toString}"""", tuple._2.toDouble))

    s"""{"x" : ["date",${priceRanges.map(_._1).mkString(",")}],
       |"y" : ["prices",${priceRanges.map(_._2).mkString(",")}]}""".stripMargin
  }
}
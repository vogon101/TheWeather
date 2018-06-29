package services


import java.awt.image.BufferedImage
import java.io.{File, PrintWriter}
import java.nio.file.{Files, Paths}
import java.time.temporal.TemporalAmount
import java.time.{ZoneId, ZonedDateTime}

import com.vogonjeltz.weather.utils.{DWDUtils, WeatherUtils}
import com.vogonjeltz.weather.gfx.{ColourScale, WeatherMapGenerator}
import com.vogonjeltz.weather.lib.UcarVariableGridWrapper
import com.vogonjeltz.weather.map.MapData
import javax.inject.Inject
import lib.{Forecast, WeatherStatus}
import play.api.Configuration
import play.api.inject.ApplicationLifecycle
import play.api.libs.json.{JsNumber, JsString, JsValue, Json}
import ucar.nc2.dataset.NetcdfDataset

import scala.collection.JavaConverters._

class WeatherService  @Inject() (appLifecycle: ApplicationLifecycle, config: Configuration) {

  val DATA_PATH = config.underlying.getString("folders.data")

  def readStatusText: JsValue = Json.parse(
    Files.readAllLines(
      Paths.get(
        config.underlying.getString("files.status")
      )
    ).asScala.toList.mkString("")
  )

  def readStatusFile(): WeatherStatus = {
    val txt = readStatusText
    WeatherStatus(
      (txt \ "status" \ "lastRun" \ "hour").get.as[JsNumber].as[Int],
      (txt \ "status" \ "lastRun" \ "lastRunTime").get.as[JsString].as[String],
      (txt \ "status" \ "available").get.as[List[Forecast]].map {
        case (o) => o.name -> o
      }.toMap
    )
  }

  private var _weatherStatusVar: WeatherStatus = readStatusFile()
  def forceReadStatusFile(): Unit = _weatherStatusVar = readStatusFile()

  def updateWeatherStatus(ws: WeatherStatus): Unit = {

    _weatherStatusVar = ws

    println(ws)

    //FIXME: This is v.v.v unsafe
    new PrintWriter(config.underlying.getString("files.status")) { write(weatherStatus.toJSONString); close() }

  }

  def weatherStatus: WeatherStatus = _weatherStatusVar




  //Call me every so often please
  def checkForNewUpdates(): WeatherStatus ={

    val lastTime = weatherStatus.lastRunTime
    val time = ZonedDateTime.now(
      // Specify time zone.
      ZoneId.of("UTC")
    )

    if (time.isAfter(lastTime.plusHours(1))) {

      val dwdList = downloadLatestDWD()

      if (dwdList.length < 10) weatherStatus
      else {

        val ws = weatherStatus.addForecast( Forecast("dwd", dwdList, List("t_2m")))

        val hh = time.getHour

        val hour = 0
          /*
          if (hh >= 18) 18
          else if (hh >= 12) 12
          else if (hh >= 6) 6
          else 0
          */

        println(hh)
        println(hour)

        WeatherStatus(hour, time.toString, ws.availableForecasts)
      }

    } else {
      weatherStatus
    }

  }

  updateWeatherStatus(checkForNewUpdates())


  /*
      val lastTime = weatherStatus.lastRunTime
    val time = ZonedDateTime.now(
      // Specify time zone.
      ZoneId.of( "Europe/Paris" )
    )
   */

  def downloadLatestDWD(): List[String] =  {

    //First lets download the new run
    DWDUtils.downloadRun(
      "t_2m",
      weatherStatus.lastRunHourString,
      config.underlying.getString("folders.data") + "/dwd/t_2m/" + weatherStatus.lastRunHourString,
      (hour) => hour + ".grib2"
    )

  }

  def getDWDMap(hour: String, cs: ColourScale = ColourScale.CS_STANDARD):BufferedImage = {

    val file = new File(config.underlying.getString("files.map"))
    val mapData = MapData.readFromShapefile(file)

    val mapMaker = new WeatherMapGenerator(
      WeatherUtils.DWD_ICON_EU_FILTER,
      cs,
      Some(mapData)
    )

    val dataset = new UcarVariableGridWrapper(
      NetcdfDataset
        .openDataset(config.underlying.getString("folders.data") + "/dwd/t_2m/" + weatherStatus.lastRunHourString + "/" + hour + ".grib2")
        .findVariable("Temperature_height_above_ground"),
      WeatherUtils.DWD_ICON_EU_FILTER
    )

    mapMaker.generateImage(
      dataset
    )

  }

}

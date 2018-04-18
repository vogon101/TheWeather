package services


import java.time.{ZoneId, ZonedDateTime}

import com.vogonjeltz.weather.utils.DWDUtils
import javax.inject.Inject
import play.api.Configuration
import play.api.inject.ApplicationLifecycle

import scala.collection.mutable

class WeatherService  @Inject() (appLifecycle: ApplicationLifecycle, config: Configuration) {

  private lazy val _availableForecasts: mutable.Map[String, List[String]] = mutable.Map()

  val DATA_PATH = config.underlying.getString("folders.data")

  def availableForecasts: Map[String, List[String]] = _availableForecasts.toMap

  //Download the DWD forecast
  _availableForecasts("dwd/t_2m/" + lastRun) = downloadLatestDWD()

  def lastRun: String = {
    val time = ZonedDateTime.now(
      // Specify time zone.
      ZoneId.of( "Europe/Paris" )
    )

    val hh = time.getHour

    if (hh >= 18) "18"
    else if (hh >= 12) "12"
    else if (hh >= 6) "06"
    else "00"
  }

  def downloadLatestDWD(): List[String] =  {

    //First lets download the new run
    DWDUtils.downloadRun(
      "t_2m",
      lastRun,
      config.underlying.getString("folders.data") + "/dwd/t_2m/" + lastRun,
      (hour) => hour + ".grib2"
    )

  }

}

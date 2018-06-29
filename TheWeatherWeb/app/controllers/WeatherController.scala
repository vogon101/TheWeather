package controllers

import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import com.google.inject.{Inject, Singleton}
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream
import javax.imageio.ImageIO
import play.api.mvc._
import services.WeatherService

@Singleton
class WeatherController @Inject()(cc : ControllerComponents, val weatherService: WeatherService) extends AbstractController(cc) {

  def hello = Action { Ok ("Hello " + weatherService.weatherStatus.lastRunHourString)}

  def availableForecasts = Action {
    Ok(weatherService.weatherStatus.availableForecasts.keys.toList.mkString("\n"))
  }

  def image(hour: String) = Action {
    val img = weatherService.getDWDMap(hour)
    val baos = new ByteArrayOutputStream()
    ImageIO.write(img, "png", baos)
    //val bais = new ByteArrayInputStream(baos.toByteArray)

    val validAt = weatherService.weatherStatus.lastRunTime
      .withHour(weatherService.weatherStatus.lastRunHour)
      .plusHours(hour.toInt)
      .withMinute(0)
      .withSecond(0)
      .withNano(0)

    Ok(baos.toByteArray).as("image/png").withHeaders(
      "WEATHER_RUN" -> weatherService.weatherStatus.lastRunHourString,
      "WEATHER_VALID_AT" -> validAt.toString
    )
  }

  def status = Action {
    Ok (weatherService.weatherStatus.toJSONString)
  }

  def check = Action{
    weatherService.updateWeatherStatus(weatherService.checkForNewUpdates())
    Ok("Ok")
  }

  def readStatus = Action {
    weatherService.forceReadStatusFile()
    Ok("Ok")
  }

}

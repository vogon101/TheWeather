package controllers

import com.google.inject.{Inject, Singleton}
import play.api.mvc._
import services.WeatherService

@Singleton
class WeatherController @Inject()(cc : ControllerComponents, val weatherService: WeatherService) extends AbstractController(cc) {

  def hello = Action { Ok ("Hello " + weatherService.lastRun)}

  def availableForecasts = Action {
    Ok(weatherService.availableForecasts.keys.toList.mkString("\n"))
  }

}

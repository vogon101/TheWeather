package lib

import java.time.ZonedDateTime
import play.api.libs.json._
import play.api.libs.functional.syntax._
case class WeatherStatus(lastRunHour: Int, lastRunTimeString: String, availableForecasts: Map[String, Forecast]) {

  val lastRunTime: ZonedDateTime = ZonedDateTime.parse(lastRunTimeString)

  def toJSONString: String = Json.prettyPrint(
    Json.obj(
      "status" -> Json.obj(
        "lastRun" -> Json.obj(
          "hour" -> lastRunHour,
          "lastRunTime" -> lastRunTimeString
        ),
        "available" -> Forecast.forecastMapWrites.writes(availableForecasts)
      )
    )
  )

  def addForecast(forecast: Forecast):WeatherStatus =
    WeatherStatus(lastRunHour, lastRunTimeString, availableForecasts + (forecast.name -> forecast))

  def lastRunHourString: String = { return "00"
    if (lastRunHour >= 18) "18"
    else if (lastRunHour >= 12) "12"
    else if (lastRunHour >= 6) "06"
    else "00"
  }

}

case class Forecast (name: String, hours: List[String], variables: List[String])

object Forecast {

  implicit val forecastWrites = new Writes[Forecast] {
    override def writes(o: Forecast): JsValue = Json.obj(
      "name" -> o.name,
      "vars" -> Json.toJson(o.variables),
      "hours" -> o.hours
    )
  }

  implicit val forecastMapWrites = new Writes[Map[String, Forecast]] {
    override def writes(o: Map[String, Forecast]): JsValue = Json.toJson(o.values.map(Json.toJson(_)))
  }

  implicit val forecastReads :Reads[Forecast] = (
      (JsPath \ "name" ).read[String] and
      (JsPath \ "vars").read[List[String]] and
      (JsPath \ "hours").read[List[String]]
  ) (Forecast.apply _)

}
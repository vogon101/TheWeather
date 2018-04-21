package com.vogonjeltz.weather.utils

import java.io.{File, FileOutputStream}
import java.net.URI
import java.time.LocalDateTime

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.apache.commons.io.{FileUtils, IOUtils}

object DWDUtils {

  val T_2M_PATTERN = "ICON_EU_single_level_elements_T_2M_%s%s%s%s_%s.grib2.bz2"
  val T_2M_PATH = "http://opendata.dwd.de/weather/icon/eu_nest/grib/%s/t_2m/"

  def downloadRun(variable:String, run: String, folder: String, target: (String) => String): List[String] = {

    if (variable != "t_2m") ???

    val DATA_FOLDER = new File(folder)
    DATA_FOLDER.mkdirs()
    FileUtils.deleteDirectory(DATA_FOLDER)
    DATA_FOLDER.mkdirs()

    val date = LocalDateTime.now()

    Range(1, 121).toList.flatMap (i => {
      val hour = downloadTimeStep(
        date.getYear.toString,
        f"${date.getMonthValue}%02d",
        f"${date.getDayOfMonth}%02d",
        run,
        f"$i%03d",
        folder + "/" + target(f"$i%03d"))

      println(date.getYear.toString, f"${date.getMonthValue}%02d", f"${date.getDayOfMonth}%02d", run, f"$i%03d")
      println(f"$i%03d")

      hour.toList

    })

  }

  def downloadTimeStep(year: String, month: String, day: String, run: String, hour: String, target: String): Option[String] = {

    val inputStream = try{new URI(T_2M_PATH.format(run) + T_2M_PATTERN.format(year, month, day, run, hour)).toURL.openStream()} catch {
      case e: Exception =>
        println("Download Exception " + e.getMessage)
        return None
    }

    val outputStream = new FileOutputStream(target)

    val comp = new BZip2CompressorInputStream(inputStream)

    IOUtils.copyLarge(comp, outputStream)

    Some(hour)

  }


}

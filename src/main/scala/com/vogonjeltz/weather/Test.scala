package com.vogonjeltz.weather

import java.io.File

import org.geotools.data.FileDataStoreFinder
/*
import java.io.{File, FileInputStream, FileOutputStream}
import java.net.URI
import java.util.Date

import com.vogonjeltz.weather.dwd.DWD_Utils.ICONEU_Utils
import com.vogonjeltz.weather.lib.{GridData, VariableGridData}
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.apache.commons.io.IOUtils
import ucar.nc2.NetcdfFile
import ucar.nc2.dataset.NetcdfDataset
*/


object Test extends App {

  val file = new File("data/map.shp")
  val map: Map[String, String] = Map ("url" -> file.toURI.toString)


  /*
  val TEMP_PATH = "data/temp/t_2m"

  val T_2M_PATH = "http://opendata.dwd.de/weather/icon/eu_nest/grib/12/t_2m/"
  val T_2M_PATTERN = "ICON_EU_single_level_elements_T_2M_%s%s%s$s_%s.grib2.bz2"

  def downloadTimeStep(year: String, month: String, day: String, run: String, hour: String): Unit = {

    val TEMP_FOLDER = new File(TEMP_PATH)
    TEMP_FOLDER.mkdirs()

    val inputStream = try{new URI(T_2M_PATH + T_2M_PATTERN.format(year, month, day, run, hour)).toURL.openStream()} catch {
      case e: Exception =>
        println("Download Exception " + e.getMessage)
        return
    }

    val outputStream = new FileOutputStream(TEMP_PATH + "/" + hour + ".grib2")

    val comp = new BZip2CompressorInputStream(inputStream)

    IOUtils.copyLarge(comp, outputStream)
  }

  def downloadRun(run: String): Unit = {
    val date = new Date()
    for (i <- Range(1, 121)) {
      downloadTimeStep(date.getYear.toString, f"${date.getMonth}%02d", f"${date.getDay}%02d", run, f"$i%03d")
      println(f"$i%03d")
    }
  }

  def forecast_temperature(lat: Double, long: Double, hour: String): Double = {

    val path = s"$TEMP_PATH/$hour.grib2"
    val variable = NetcdfDataset.openDataset(path).findVariable("Temperature_height_above_ground")

    val gridData = new VariableGridData(variable, ICONEU_Utils.iconeu_gridSpec)

    gridData.getLocation(lat, long)

    /*val y = ((lat - 29.5) / 0.0625).toInt % 657
    val x = ((long - 336.5) / 0.0625).toInt % 1097


    val array = NetcdfDataset.openDataset(path).findVariable("Temperature_height_above_ground").read()
    val idx = array.getIndex

    array.getDouble(idx.set(0,0,y,x)) - 273*/

  }



  val lat = readFloat()
  val long = (readFloat() % 360) + 360


  for (i <- Range(0, 120)) {
    try {
      val temp = forecast_temperature(lat, long, f"$i%03d")
      println(f"$i%3d -> ${temp}%3f")
    } catch {
      case e: Exception =>
    }

  }

  */


}

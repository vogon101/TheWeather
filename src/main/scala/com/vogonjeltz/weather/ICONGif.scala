package com.vogonjeltz.weather

import java.awt.image.RenderedImage
import java.io.{File, FileOutputStream}
import java.net.URI
import java.time.LocalDateTime
import java.util.{Calendar, Date}

import com.vogonjeltz.weather.gfx.{ColourScale, WeatherMapGenerator}
import com.vogonjeltz.weather.lib.UcarVariableGridWrapper
import com.vogonjeltz.weather.map.MapData
import com.vogonjeltz.weather.utils.WeatherUtils
import javax.imageio.ImageIO
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.apache.commons.io.{FileUtils, IOUtils}
import ucar.nc2.dataset.NetcdfDataset

object ICONGif extends App {

  val DATA_PATH = "data/temp/t_2m"
  val OUTPUT_PATH = "output/dwd/t_2m"

  val IMAGE_PATTERN = "ICON_T_2M_%s.png"

  val T_2M_PATH = "http://opendata.dwd.de/weather/icon/eu_nest/grib/%s/t_2m/"
  val T_2M_PATTERN = "ICON_EU_single_level_elements_T_2M_%s%s%s%s_%s.grib2.bz2"

  def downloadTimeStep(year: String, month: String, day: String, run: String, hour: String): Option[String] = {

    val inputStream = try{new URI(T_2M_PATH.format(run) + T_2M_PATTERN.format(year, month, day, run, hour)).toURL.openStream()} catch {
      case e: Exception =>
        println("Download Exception " + e.getMessage)
        return None
    }

    val outputStream = new FileOutputStream(DATA_PATH + "/" + hour + ".grib2")

    val comp = new BZip2CompressorInputStream(inputStream)

    IOUtils.copyLarge(comp, outputStream)

    Some(hour)

  }

  def downloadRun(run: String): List[String] = {
    
    val DATA_FOLDER = new File(DATA_PATH)
    DATA_FOLDER.mkdirs()
    FileUtils.deleteDirectory(DATA_FOLDER)
    DATA_FOLDER.mkdirs()

    val date = LocalDateTime.now()

    Range(1, 121).toList.flatMap (i => {
      val hour = downloadTimeStep(date.getYear.toString, f"${date.getMonthValue}%02d", f"${date.getDayOfMonth}%02d", run, f"$i%03d")

      println(date.getYear.toString, f"${date.getMonthValue}%02d", f"${date.getDayOfMonth}%02d", run, f"$i%03d")
      println(f"$i%03d")

      hour.toList

    })

  }
  //STEP 1: Download forecast

  //TODO: Allow skipping downloads
  val valid_hours = downloadRun("12")

  val OUTPUT_FOLDER = new File(OUTPUT_PATH)
  OUTPUT_FOLDER.mkdirs()

  //STEP 2: Generate pngs

  for (hour <- valid_hours) {

    println(s"Generating $hour")

    val GRIB_FILE_PATH = DATA_PATH + "/" + hour + ".grib2"

    val dataset = new UcarVariableGridWrapper(NetcdfDataset.openDataset(GRIB_FILE_PATH).findVariable("Temperature_height_above_ground"), WeatherUtils.DWD_ICON_EU_FILTER)//new VariableGridData(NetcdfDataset.openDataset(path).findVariable("Temperature_height_above_ground"), canada_gridSpec)
    val colourScale: ColourScale = ColourScale.CS_STANDARD.reverse

    val file = new File("data/map.shp")
    val mapData = MapData.readFromShapefile(file)
    val img = new WeatherMapGenerator(WeatherUtils.DWD_ICON_EU_FILTER, colourScale, Some(mapData)).generateImage(dataset, scaleRange = Some((273 - 20, 273 + 50)))

    ImageIO.write(img.asInstanceOf[RenderedImage], "png", new File(OUTPUT_PATH + "/" + IMAGE_PATTERN.format(hour)))

  }


  

}

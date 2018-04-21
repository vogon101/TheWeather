package com.vogonjeltz.weather.app

import java.awt.image.RenderedImage
import java.io.{File, FileOutputStream}
import java.net.URI
import java.time.LocalDateTime

import com.vogonjeltz.weather.gfx.{ColourScale, WeatherMapGenerator}
import com.vogonjeltz.weather.lib.UcarVariableGridWrapper
import com.vogonjeltz.weather.map.MapData
import com.vogonjeltz.weather.utils.{DWDUtils, WeatherUtils}
import javax.imageio.ImageIO
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream
import org.apache.commons.io.{FileUtils, IOUtils}
import ucar.nc2.dataset.NetcdfDataset

object ICONGif extends App {

  val DATA_PATH = "data/dwd/t_2m/12"
  val OUTPUT_PATH = "output/dwd/t_2m"

  val IMAGE_PATTERN = "ICON_T_2M_%s.png"

  //TODO: Allow skipping downloads
  val valid_hours = DWDUtils.downloadRun("t_2m", "12", "data/dwd/t_2m/12", (hour) => hour + ".grib2")

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

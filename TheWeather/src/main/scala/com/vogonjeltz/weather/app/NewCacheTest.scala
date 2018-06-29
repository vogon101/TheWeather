package com.vogonjeltz.weather.app

import java.awt.image.RenderedImage
import java.io.File
import java.time.LocalDateTime

import com.vogonjeltz.weather.gfx.{ColourScale, WeatherMapGenerator}
import com.vogonjeltz.weather.lib.UcarVariableGridWrapper
import com.vogonjeltz.weather.map.MapData
import com.vogonjeltz.weather.utils.{DWDUtils, WeatherUtils}
import com.vogonjeltz.weather.utils.DWDUtils.{ModelRun, ModelVariable}
import javax.imageio.ImageIO
import ucar.nc2.dataset.NetcdfDataset

object NewCacheTest extends App {

  val T_2M_PATTERN = "ICON_EU_single_level_elements_T_2M_%s%s%s%s_%s.grib2.bz2"
  val T_2M_PATH = "http://opendata.dwd.de/weather/icon/eu_nest/grib/%s/t_2m/"

  val FOLDER = "data/"

  val DWD_T2M = new ModelVariable(
    "t_2m",
    120,
    (run: ModelRun, hour: String) =>
      T_2M_PATH.format(run.hourString)
        + T_2M_PATTERN.format(
        run.run.getYear.toString,
        f"${run.run.getMonthValue}%02d",
        f"${run.run.getDayOfMonth}%02d",
        run.hourString,
        hour
      ),
    (run: ModelRun, hour: String) =>
      (FOLDER + f"/dwd/${run.run.getYear}${run.run.getMonthValue}%02d${run.run.getDayOfMonth}%02d/${run.hourString}/t_2m/", s"$hour.grib2")

  )

  val RUN = new ModelRun(
    LocalDateTime.now(),
    0
  )

  val index = DWDUtils.indexRun(RUN, DWD_T2M)

  println(index.hours)

  val path_to_hour_one = DWDUtils.getHourPath(index, "001")

  val dataset = new UcarVariableGridWrapper(NetcdfDataset.openDataset(path_to_hour_one).findVariable("Temperature_height_above_ground"), WeatherUtils.DWD_ICON_EU_FILTER)//new VariableGridData(NetcdfDataset.openDataset(path).findVariable("Temperature_height_above_ground"), canada_gridSpec)
  val colourScale: ColourScale = ColourScale.CS_STANDARD.reverse

  val file = new File("data/map.shp")
  val mapData = MapData.readFromShapefile(file)
  val img = new WeatherMapGenerator(WeatherUtils.DWD_ICON_EU_FILTER, colourScale, Some(mapData)).generateImage(dataset)

  ImageIO.write(img.asInstanceOf[RenderedImage], "png", new File("out.png"))




}

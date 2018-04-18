package com.vogonjeltz.weather

import java.awt.image.RenderedImage
import java.io.File

import com.vividsolutions.jts.geom.MultiPolygon
import com.vogonjeltz.weather.Plotting.img
import com.vogonjeltz.weather.utils.WeatherUtils
import com.vogonjeltz.weather.gfx.{ColourScale, WeatherMapGenerator}
import com.vogonjeltz.weather.lib.UcarVariableGridWrapper
import javax.imageio.ImageIO
import org.geotools.data.{DataStoreFinder, FileDataStoreFinder}
import ucar.nc2.dataset.NetcdfDataset

import scala.collection.JavaConverters._
import scala.collection.mutable.ArrayBuffer



object Test extends App {

  val file = new File("data/map.shp")
  val map: Map[String, String] = Map ("url" -> file.toURI.toString)
  val dataStore = DataStoreFinder.getDataStore(map.asJava)

  val typeName = dataStore.getTypeNames.head
  println(s"Reading $typeName")

  val iterator = dataStore.getFeatureSource(typeName).getFeatures().features()

  val path = s"data/canada_test.grib2"
  val dataset = new UcarVariableGridWrapper(NetcdfDataset.openDataset(path).findVariable("Temperature_height_above_ground"), WeatherUtils.CANADA_GDPS_FILTER)//new VariableGridData(NetcdfDataset.openDataset(path).findVariable("Temperature_height_above_ground"), canada_gridSpec)
  val colourScale: ColourScale = ColourScale.CS_BLUES.reverse
  val img = new WeatherMapGenerator(WeatherUtils.CANADA_GDPS_FILTER, colourScale).generateImage(dataset)

  val polygons = ArrayBuffer[List[(Int, Int)]]()

  println(s"Longsize = ${WeatherUtils.CANADA_GDPS_FILTER.gridSpec.longSize}")
  println(s"Latsize = ${WeatherUtils.CANADA_GDPS_FILTER.gridSpec.latSize}")


  println(img.getWidth)
  println(img.getHeight)


  while (iterator.hasNext) {
    val feature = iterator.next()
    val geom = feature.getDefaultGeometryProperty()
    val poly = geom.getValue.asInstanceOf[MultiPolygon]
    for (i <- Range(0, poly.getNumGeometries)) {
      val geom = poly.getGeometryN(i)
      val coords = geom.getCoordinates
        .map(T => WeatherUtils.CANADA_GDPS_FILTER.convertToIDXOption(T.y, T.x))
        .filterNot(_.isEmpty).map(_.get).toList
      polygons.append(coords)
    }

  }

  for (poly <- polygons) {

    var lastPoint: (Int, Int) = null

    for (i <- poly) {

      val point = WeatherUtils.CANADA_GDPS_FILTER.filterIDX(i)

      if (lastPoint != null) {

        val (startX, startY) = (lastPoint._1.toDouble, lastPoint._2.toDouble)
        val (endX, endY) = (point._1.toDouble, point._2.toDouble)

        val STEP = if (startX > endX) -0.05 else 0.05
        var xSTEP = STEP
        val GRAD =
          if (Math.abs(endX - startX)< 0.001) {
            xSTEP = 0
            if (endY > startY) 0.4 else -0.4
          } else (endY - startY) / (endX - startX)

        var posX = startX
        var posY = startY

        println(s"From $startX,$startY to $endX,$endY with grad $GRAD and xSTEP $xSTEP")

        while (!(posX.round == endX.round && posY.round == endY.round)) {

          posX += xSTEP
          posY += GRAD * STEP

          img.setRGB(posY.round.toInt, posX.round.toInt, 0)

        }


      }

      img.setRGB(point._2, point._1, 0)

      lastPoint = point

    }



  }

  /*
  for (idx <- polygons) {



    println(idx)
    val i = WeatherUtils.DWD_ICON_EU_FILTER.filterIDX(idx)
    //println(i)
    img.setRGB(i._2, i._1, 0)
  }

*/
  ImageIO.write(img.asInstanceOf[RenderedImage], "png", new File("out.png"))


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

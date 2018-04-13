package com.vogonjeltz.weather


import java.awt.image.RenderedImage
import java.io.File

import com.vogonjeltz.weather.dwd.WeatherUtils
import javax.imageio.ImageIO
import com.vogonjeltz.weather.gfx.{ColourScale, ImageGenerator}
import com.vogonjeltz.weather.lib._
import ucar.ma2.Index
import ucar.nc2.dataset.NetcdfDataset

/**
  * Plotting
  *
  * Created by fredd
  */
object Plotting extends App {

  val TEMP_PATH = "data/temp/t_2m"

  val hour = "002"
  val path = s"data/clouds.grib2"
  val dataset = new UcarVariableGridWrapper(NetcdfDataset.openDataset(path).findVariable("Cloud_cover_isobaric_layer"), WeatherUtils.DWD_ICON_EU_FILTER)//new VariableGridData(NetcdfDataset.openDataset(path).findVariable("Temperature_height_above_ground"), canada_gridSpec)
  val colourScale: ColourScale = ColourScale.CS_STANDARD.reverse
  val img = new ImageGenerator(colourScale).generateImage(dataset)

  ImageIO.write(img.asInstanceOf[RenderedImage], "png", new File("out.png"))


  /*


      /*
      getImageFromArray(
        finalArray.map(_.map(
          (T: Double) => {
            colourScale.mapValue((T - minValue) * step, interpolate = true)
          }
        )),
        size(3),
        size(2)
      )
      */

  val size = array.getShape
  val idx = array.getIndex

  println(size.toList)


  val finalArray: Array[Array[Double]] = Array.fill(size(3), size(2))(0)

  for (i <- Range(0,size(2))) {
    for (j <- Range(0, size(3))) {
      finalArray(j)(size(2) - i - 1) = array.getDouble(idx.set(0,0,i,j))
    }
  }

  val maxValue = finalArray.map(_.max).max
  val minValue = finalArray.map(_.min).min

  val step = 1/(maxValue - minValue)

  def interpolateColour(value: Double, colours: List[(Double, Double, Double)]): (Int, Int, Int) = {


    val fi = value * (colours.length- 1)
    val index = fi.toInt
    val step = fi - index

    if (step < 0.0001) {
      val (r,g,b) = colours(index)
      return (r.toInt, g.toInt, b.toInt)
    }

    val (r1, g1, b1) = colours(index)
    val (r2, g2, b2) = colours(index + 1)

    (
      (r1 + step * (r2 - r1)).toInt,
      (g1 + step * (g2 - g1)).toInt,
      (b1 + step * (b2 - b1)).toInt
    )

  }

  def getImageFromArray(arr: Array[Array[(Int, Int, Int)]], width: Int, height: Int): Image = {
    val image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val raster = image.getRaster


    for (x <- Range(0, width)) {
      for (y <- Range(0, height)) {
        val d = arr(x)(y)
        val (r,g,b) = d
        var p = (0 << 24) | (r << 16) | (g << 8) | b
        image.setRGB(x,y, p)
      }
    }

    //raster.setPixels(0,0, width, height, arr.flatten)
    println("done")
    image
  }

  */

}

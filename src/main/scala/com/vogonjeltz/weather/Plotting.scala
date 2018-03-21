package com.vogonjeltz.weather

import java.awt.Image
import java.awt.image.{BufferedImage, RenderedImage, WritableRaster}
import java.io.File

import javax.swing.{ImageIcon, JFrame, JLabel}
import com.vogonjeltz.weather.Test.TEMP_PATH
import javax.imageio.ImageIO
import ucar.nc2.dataset.NetcdfDataset

/**
  * Plotting
  *
  * Created by fredd
  */
object Plotting extends App {

  val TEMP_PATH = "data/temp/t_2m"

  val hour = "002"
  val path = s"$TEMP_PATH/$hour.grib2"
  val dataset = NetcdfDataset.openDataset(path)
  val array = dataset.findVariable("Temperature_height_above_ground").read()
  array.reduce()

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

  val colourScale: List[(Double, Double, Double)] = List(
    (0x00, 0x02, 0x0C),
    (0x00, 0x07, 0x25),
    (0x00, 0x2D, 0x58),
    (0x00, 0x73, 0xA3),
    (0x2B, 0xAE, 0xC0),
    (0x81, 0xDD, 0xB0),
    (0xC0, 0xF7, 0xBE)
  )


  val img =
      getImageFromArray(
        finalArray.map(_.map(
          (T: Double) => {
            interpolateColour( (T - minValue) * step, colourScale)
          }
        )),
        size(3),
        size(2)
      )

  ImageIO.write(img.asInstanceOf[RenderedImage], "png", new File("out.png"))

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



}

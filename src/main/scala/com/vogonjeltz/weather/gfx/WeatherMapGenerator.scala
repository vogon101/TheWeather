package com.vogonjeltz.weather.gfx

import java.awt.image.BufferedImage

import com.vogonjeltz.weather.lib.{GridFilter, UcarVariableGridWrapper}
import com.vogonjeltz.weather.map.MapData

/**
  * ImageGenerator
  *
  *
  *
  * Created by fredd
  */
class WeatherMapGenerator(
                   val gridFilter: GridFilter,
                   val colourScale: ColourScale,
                   val mapData: Option[MapData] = None
                 )
{

  /**
    * Generates an image, preference of resolution: resX, resY, default
    * @param data - Data to be rendered
    * @param resolutionX - X resolution
    * @param resolutionY - Y resolution
    * @param interpolate - interpolate colours
    * @param scaleRange - Absolute range of the scale
    * @return
    */
  def generateImage(data: UcarVariableGridWrapper, resolutionX: Option[Int] = None, resolutionY: Option[Int] = None, interpolate: Boolean = true, scaleRange: Option[(Double, Double)] = None): BufferedImage = {

    import data.gridFilter.gridSpec._

    val (width, height) =
      if (resolutionX.isDefined) {
        val w = resolutionX.get
        val h = (w.toDouble / longSize) * latSize
        (w.toInt,h.toInt)
      }
      else if (resolutionY.isDefined) {
        val h = resolutionY.get
        val w = (h.toDouble / latSize) * longSize
        (w.toInt,h.toInt)
      }
      else (longSize, latSize)

    val (minValue, maxValue): (Double, Double) = scaleRange.getOrElse((data.javaArray.map(_.min).min,data.javaArray.map(_.max).max))

    val xScale = width.toDouble / longSize
    val yScale = height.toDouble / latSize

    val colourMapper = (colourScale.mapValue _)(minValue, maxValue)

    println(s"xScale = $xScale")
    println(s"yScale = $yScale")

    val image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)

    for (x <- Range(0, width)) {
      for (y <- Range(0, height)) {

        val I_X = (x / xScale).floor.toInt
        val I_Y = (y / yScale).floor.toInt

        val d = colourMapper(
          data.getIndex(I_Y,I_X),
          interpolate
        )

        val (r,g,b) = d
        var p = (0 << 24) | (r << 16) | (g << 8) | b
        image.setRGB(x,y,p)

      }
    }

    if (mapData.isEmpty) return image

    for (poly <- mapData.get.interpret(gridFilter)) {

      var lastPoint: (Int, Int) = null

      for (i <- poly) {

        val point = gridFilter.filterIDX(i)

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

          //println(s"From $startX,$startY to $endX,$endY with grad $GRAD and xSTEP $xSTEP")

          while (!(posX.round == endX.round && posY.round == endY.round)) {

            posX += xSTEP
            posY += GRAD * STEP

            image.setRGB((posY * xScale).round.toInt, (posX * yScale).round.toInt, 0)

          }

        }

        image.setRGB((point._2 * xScale).round.toInt, (point._1 * yScale).round.toInt, 0)

        lastPoint = point

      }

    }

    image

  }

}

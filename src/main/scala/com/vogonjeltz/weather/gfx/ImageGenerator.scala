package com.vogonjeltz.weather.gfx

import java.awt.image.BufferedImage

import com.vogonjeltz.weather.lib.UcarVariableGridWrapper

/**
  * ImageGenerator
  *
  * Created by fredd
  */
class ImageGenerator (colourScale: ColourScale) {

  def generateImage(data: UcarVariableGridWrapper, interpolate: Boolean = true): BufferedImage = {

    import data.gridFilter.gridSpec._

    val maxValue = data.javaArray.map(_.max).max
    val minValue = data.javaArray.map(_.min).min

    val image = new BufferedImage(longSize, latSize, BufferedImage.TYPE_INT_RGB)
    val raster = image.getRaster

    for (x <- Range(0, longSize)) {
      for (y <- Range(0, latSize)) {
        val d = colourScale.mapValue(
          data.getIndex(y,x),
          minValue,
          maxValue,
          interpolate
        )
        val (r,g,b) = d
        var p = (0 << 24) | (r << 16) | (g << 8) | b
        image.setRGB(x,y,p)
      }
    }

    image

  }

}

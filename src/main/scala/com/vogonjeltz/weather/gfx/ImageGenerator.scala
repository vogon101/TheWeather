package com.vogonjeltz.weather.gfx

import java.awt.image.BufferedImage

import com.vogonjeltz.weather.lib.{GridData, VariableGridData}

/**
  * ImageGenerator
  *
  * Created by fredd
  */
class ImageGenerator (colourScale: ColourScale) {

  def generateImage(data: VariableGridData, interpolate: Boolean = true): BufferedImage = {

    val dataArray = data.toArrayGrid.flipLatLong.toArray

    val maxValue = dataArray.map(_.max).max
    val minValue = dataArray.map(_.min).min

    val colourArray = dataArray.map(_.map(colourScale.mapValue(_, minValue, maxValue, interpolate)))

    val width = dataArray.length
    val height = dataArray(0).length

    val image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
    val raster = image.getRaster

    for (x <- Range(0, width)) {
      for (y <- Range(0, height)) {
        val d = colourArray(x)(y)
        val (r,g,b) = d
        var p = (0 << 24) | (r << 16) | (g << 8) | b
        image.setRGB(x,y, p)
      }
    }

    image

  }

}

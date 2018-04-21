package com.vogonjeltz.weather.gfx

/**
  * ColourScale
  *
  * Created by fredd
  */
case class ColourScale (colours: (Double, Double, Double)*) {

  def mapValue(minVal:Double = 1, maxVal:Double = 1)(value: Double,interpolate: Boolean = true): (Int, Int, Int) = {

    val fi = ((value - minVal) / (maxVal - minVal)) * (colours.length - 1)//(if (interpolate) colours.length- 1 else colours.length)
    val index = fi.toInt
    val step = fi - index

    if (!interpolate || step < 0.0001) {
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

  def reverse: ColourScale = ColourScale(colours.reverse: _*)

}

object ColourScale {

  val CS_BLUES = ColourScale(
      (0x00, 0x02, 0x0C),
      (0x00, 0x07, 0x25),
      (0x00, 0x2D, 0x58),
      (0x00, 0x73, 0xA3),
      (0x2B, 0xAE, 0xC0),
      (0x81, 0xDD, 0xB0),
      (0xC0, 0xF7, 0xBE)
  )

  val CS_RED_BLUE = ColourScale(
    (0xFF, 0x00, 0x00),
    (0x00, 0x00, 0xFF)
  )

  val CS_BLACK_WHITE = ColourScale(
    (0,0,0),
    (0xFF, 0xFF, 0xFF)
  )

  val CS_STANDARD = ColourScale(
    (0xad, 0x05, 0x05),
    (0xFF, 0x00, 0x00),
    (0xf7, 0x85, 0x38),
    (0xed, 0xf7, 0x37),
    (0x42, 0xe2, 0xf4),
    (0x00, 0x00, 0xFF)
  )

}
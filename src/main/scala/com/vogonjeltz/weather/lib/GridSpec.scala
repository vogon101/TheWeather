package com.vogonjeltz.weather.lib

/*
CONVENTION: x-direction is longitude, y is latitude
 */

class GridSpec (
                 val minLat: Double,
                 val maxLat: Double,
                 val latRes: Double,

                 val minLong: Double,
                 val maxLong: Double,
                 val longRes: Double,

                 val longSize: Int,
                 val latSize: Int
  ) {

  assert(math.abs(longSize - (maxLong-minLong)/longRes) < 0.0001)
  assert(math.abs(latSize - (maxLat-minLat)/latRes) < 0.0001)

  def this(minLat: Double, maxLat: Double, latRes: Double, minLong: Double, maxLong: Double, longRes: Double) {
    this(minLat, maxLat, latRes, minLong, maxLong, longRes, ((maxLong-minLong)/longRes).toInt , ((maxLat-minLat)/longRes).toInt)
  }

}

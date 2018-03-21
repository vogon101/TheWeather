package com.vogonjeltz.weather.lib

import ucar.ma2.Index
import ucar.nc2.Variable

class GridData (val variable: Variable, val gridSpec: GridSpec){

  private val minLat: Double = gridSpec.minLat
  private val minLong: Double = gridSpec.minLong
  private val resolutionLat: Double = gridSpec.resolutionLat
  private val resolutionLong: Double = gridSpec.resolutionLong

  lazy val array = variable.read()

  def getLocation(lat: Double, long: Double): Double = {
    val lat_idx = ((lat - minLat) / resolutionLat).toInt
    val long_idx = ((long - minLong) / resolutionLong). toInt

    val idx = array.getIndex

    array.getDouble(gridSpec.latLongToIndex(lat_idx, long_idx, idx))

  }
}

case class GridSpec (
                      minLat: Double,
                      minLong: Double,
                      resolutionLat: Double,
                      resolutionLong: Double,
                      latLongToIndex: (Int, Int, Index) => Index
                    )

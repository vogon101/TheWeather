package com.vogonjeltz.weather.lib

import ucar.ma2.Index
import ucar.nc2.Variable



//TODO: Add access filters that allow modifications like flip-lat/long and flip x, flip y
abstract class GridData (val gridSpec: GridSpec) {

  protected val minLat: Double = gridSpec.minLat
  protected val minLong: Double = gridSpec.minLong
  protected val resolutionLat: Double = gridSpec.resolutionLat
  protected val resolutionLong: Double = gridSpec.resolutionLong

  def getLocation(lat: Double, long: Double): Double

  def getIndex(latIdx: Int, longIdx: Int): Double

  def toArray: Array[Array[Double]]

}

class VariableGridData (val variable: Variable, val varGridSpec: VariableGridSpec) extends GridData (varGridSpec){

  lazy val array: ucar.ma2.Array = variable.read()
  lazy val idx: Index = array.getIndex
  lazy val coordConverter = varGridSpec.latLongIndexToIndex(idx)

  lazy val arrayRepresentation: Array[Array[Double]] = {
    val arr = array.reduce()
    val size = arr.getShape
    val idx = arr.getIndex
    val finalArray: Array[Array[Double]] = Array.fill(size(0), size(1))(0)

    for (i <- Range(0,size(0))) {
      for (j <- Range(0, size(1))) {
        finalArray(i)(j) = arr.getDouble(idx.set(i,j))
      }
    }

    finalArray
  }

  def getLocation(lat: Double, long: Double): Double = {
    val lat_idx = ((lat - minLat) / resolutionLat).toInt
    val long_idx = ((long - minLong) / resolutionLong). toInt

    array.getDouble(coordConverter(lat_idx, long_idx))

  }

  def getIndex(latIdx: Int, longIdx: Int): Double =
    array.getDouble(coordConverter(latIdx, longIdx))

  def toArray: Array[Array[Double]] = arrayRepresentation

  def toArrayGrid: ArrayGridData = new ArrayGridData(arrayRepresentation, gridSpec)

}

class ArrayGridData(val data: Array[Array[Double]], gridSpec: GridSpec) extends GridData (gridSpec){

  lazy val x_size: Int = data.length
  lazy val y_size: Int = data(0).length

  def getLocation(lat:Double, long:Double): Double = {
    val lat_idx = ((lat - minLat) / resolutionLat).toInt
    val long_idx = ((long - minLong) / resolutionLong). toInt

    data(lat_idx)(long_idx)
  }

  override def getIndex(latIdx: Int, longIdx: Int): Double = data(latIdx)(longIdx)

  def toArray: Array[Array[Double]] = data

  def flipLatLong: ArrayGridData = {

    val newArray = Array.fill(y_size, x_size)(0d)

    for (x <- Range(0, x_size)) {
      for (y <- Range(0, y_size)) {
        newArray(y)(x) = data(x)(y)
      }
    }

    new ArrayGridData(newArray, GridSpec(minLong, minLat, resolutionLong, resolutionLat))

  }

}

case class GridSpec (
                      minLat: Double,
                      minLong: Double,
                      resolutionLat: Double,
                      resolutionLong: Double
                    )

class VariableGridSpec(
                        minLat: Double,
                        minLong: Double,
                        resolutionLat: Double,
                        resolutionLong: Double,
                        val latLongIndexToIndex: (Index) => (Int, Int) => Index
                      ) extends GridSpec(minLat, minLong, resolutionLat, resolutionLong)

package com.vogonjeltz.weather.lib

import ucar.ma2.Index
import ucar.nc2.Variable

abstract class GridDataWrapper (val gridFilter: GridFilter) {

  import gridFilter.gridSpec._

  def getLocation(lat: Double, long: Double): Double = {
    val idx = gridFilter.convertToIDX(lat, long)
    getIndex(idx._1, idx._2)
  }

  def getIndex(idx: (Int, Int)): Double = {
    val filtered = gridFilter.filterIDX(idx)
    applyGetIndex(filtered)
  }

  protected def applyGetIndex(idx: (Int,Int)): Double

}

class UcarVariableGridWrapper (val variable: Variable, val ucarFilter: UcarGridFilter) extends GridDataWrapper (ucarFilter) {

  private lazy val array : ucar.ma2.Array = variable.read()
  private lazy val indexer: Index = array.getIndex
  private lazy val indexMapper = ucarFilter.idxMapper(indexer)

  lazy val javaArray: Array[Array[Float]] = array.reduce().copyToNDJavaArray().asInstanceOf[Array[Array[Float]]]

  override def applyGetIndex(idx: (Int, Int)): Double = {

    array.getDouble(indexMapper(idx._1, idx._2))

  }

}

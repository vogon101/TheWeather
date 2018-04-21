package com.vogonjeltz.weather.map

import java.io.File

import com.vividsolutions.jts.geom.MultiPolygon
import com.vogonjeltz.weather.lib.GridFilter
import org.geotools.data.DataStoreFinder

import scala.collection.mutable.ArrayBuffer

class MapData(val data: List[List[(Double, Double)]]) {

  def interpret(filter: GridFilter): List[List[(Int, Int)]] = {

    data.map(
      _.map(
        P => filter.convertToIDXOption(P._1, P._2)
      ).filter(_.isDefined).map(_.get)
    )

  }

}

object MapData {

  def readFromShapefile(file: File): MapData = {

    import scala.collection.JavaConverters._

    val map: Map[String, String] = Map ("url" -> file.toURI.toString)
    val dataStore = DataStoreFinder.getDataStore(map.asJava)

    val typeName = dataStore.getTypeNames.head
    println(s"Reading $typeName")

    val iterator = dataStore.getFeatureSource(typeName).getFeatures().features()

    val polygons = ArrayBuffer[List[(Double,Double)]]()
    while (iterator.hasNext) {

      val feature = iterator.next()
      val geom = feature.getDefaultGeometryProperty
      val poly = geom.getValue.asInstanceOf[MultiPolygon]

      for (i <- Range(0, poly.getNumGeometries)) {
        val geom = poly.getGeometryN(i)
        val coords = geom.getCoordinates.map(T => (T.y, T.x)).toList
        polygons.append(coords)
      }

    }

    iterator.close()

    new MapData(polygons.toList)
  }

}
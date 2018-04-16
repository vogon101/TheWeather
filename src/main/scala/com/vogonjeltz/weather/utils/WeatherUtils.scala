package com.vogonjeltz.weather.utils

import com.vogonjeltz.weather.lib.{FlipLatFilter, GridFilter, GridSpec, UcarGridFilter}
import ucar.ma2.Index

object WeatherUtils {

  lazy val DWD_ICON_EU_GRIDSEC = new GridSpec(
    29.5,70.5,0.0625,
    -23.5,45,0.0625
  )

  lazy val DWD_ICON_EU_FILTER: UcarGridFilter = {
    val x = new UcarGridFilter(DWD_ICON_EU_GRIDSEC, (idx: Index) => (lat: Int, long: Int) => idx.set(0,0,lat, long))
    x.registerFilter(new FlipLatFilter(DWD_ICON_EU_GRIDSEC))
    x
  }


  lazy val CANADA_GDPS_GRIDSPEC =  new GridSpec(
    -90,90,0.24,
    -180,180,0.24
  )

  lazy val CANADA_GDPS_FILTER: UcarGridFilter = {
    val x = new UcarGridFilter(CANADA_GDPS_GRIDSPEC, (idx: Index) => (lat: Int, long: Int) => idx.set(0,0,lat, long))
    x.registerFilter(new FlipLatFilter(CANADA_GDPS_GRIDSPEC))
    x
  }

}

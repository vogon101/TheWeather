package com.vogonjeltz.weather.dwd

import com.vogonjeltz.weather.lib.GridSpec
import ucar.ma2.Index

object DWD_Utils {

  object ICONEU_Utils {

    lazy val iconeu_gridSpec = GridSpec(
      29.5,
      336.5,
      0.0625,
      0.0625,
      (lat: Int, long: Int, idx: Index) => idx.set(0,0,lat, long)
    )

  }

}

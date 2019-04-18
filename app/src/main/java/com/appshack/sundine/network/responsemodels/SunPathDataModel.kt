package com.appshack.sundine.network.responsemodels

import com.appshack.sundine.dataclasses.CanvasArcData
import com.appshack.sundine.dataclasses.CelestialPoint
import com.google.android.gms.maps.model.CameraPosition
import java.util.*

data class SunPathDataModel(val name: String, val gregorianCalendar: GregorianCalendar, val cameraPosition: CameraPosition) {

    lateinit var sunrisePosition: CelestialPoint
    lateinit var solarNoonPosition: CelestialPoint
    lateinit var sunsetPosition: CelestialPoint
    lateinit var currentSolarPosition: CelestialPoint
    lateinit var canvasArcData: CanvasArcData

}
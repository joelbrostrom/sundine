package com.appshack.sundine.network.responsemodels

import com.appshack.sundine.dataclasses.CanvasArcData
import com.appshack.sundine.dataclasses.CelestialPoint
import java.util.*

data class SunPathDataModel(val name: String, val Date: Date) {

    lateinit var sunrisePosition: CelestialPoint
    lateinit var solarNoonPosition: CelestialPoint
    lateinit var sunsetPosition: CelestialPoint
    lateinit var currentSolarPosition: CelestialPoint
    lateinit var canvasArcData: CanvasArcData

//    override var sunriseTime: String = "No Time"
//    override var sunsetTime: String = "No Time"
//    override var noonTime: String = "No Time"
//
//    override var sunriseAngle: Float = 0f
//    override var sunsetAngle: Float = 0f
//    override var noonAngle: Float = 0f
//
//    override var sunriseZenith: Float = 0f
//    override var sunsetZenith: Float = 0f
//    override var noonzenit: Float = 0f
//
//
//    override var widthFraction: Double = (Math.cos(sunriseAngle.toDouble())
//            + Math.cos(sunsetAngle.toDouble())) / 2


}
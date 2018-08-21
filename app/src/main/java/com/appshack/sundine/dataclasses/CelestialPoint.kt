package com.appshack.sundine.dataclasses

import java.util.*


/**
 * Created by joelbrostrom on 2018-05-24
 * Developed by App Shack
 */

data class CelestialPoint(val date: Date, val azimuth: Float, private val zenith: Float) {

    fun getWidthMultiplier(azimuthAngle: Float = azimuth) = Math.abs(Math.cos(Math.toRadians(azimuthAngle.toDouble())).toFloat())

    fun getZenithMultiplier(zenithAngle: Float = zenith) = Math.sin(Math.toRadians(zenithAngle.toDouble())).toFloat()
}
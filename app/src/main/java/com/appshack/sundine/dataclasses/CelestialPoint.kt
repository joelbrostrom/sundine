package com.appshack.sundine.dataclasses


/**
 * Created by joelbrostrom on 2018-05-24
 * Developed by App Shack
 */

data class CelestialPoint(val azimuth: Float, val zenith: Float) {

    fun getWidthMultiplier(azimuthAngle: Float = azimuth) = Math.abs(Math.cos(Math.toRadians(azimuthAngle.toDouble())).toFloat())
    fun getZenithMultiplier(zenithAngle: Float= zenith) = Math.abs(Math.sin(Math.toRadians(zenithAngle.toDouble())).toFloat())
}
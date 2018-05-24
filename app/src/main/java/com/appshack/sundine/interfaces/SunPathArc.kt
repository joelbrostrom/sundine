package com.appshack.sundine.interfaces


/**
 * Created by joelbrostrom on 2018-05-23
 * Developed by App Shack
 */

interface SunPathArc {
    var sunriseTime: String
    var sunriseAngle: Float
    var sunsetTime: String

    var sunsetAngle: Float
    var noonTime: String
    var noonAngle: Float

    var sunriseZenith: Float
    var sunsetZenith: Float
    var noonzenit: Float

    var widthFraction: Double
    var heightFraction: Double
}
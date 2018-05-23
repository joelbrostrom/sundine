package com.appshack.sundine.network.responsemodels

import com.appshack.sundine.interfaces.SunPathArc
import com.fasterxml.jackson.annotation.JsonProperty

data class SunPathDataModel(

	@field:JsonProperty("sunrise")
	val sunrise: String,

	@field:JsonProperty("solar_noon")
	val solarNoon: String,

	@field:JsonProperty("day_length")
	val dayLength: Int? = null,

	@field:JsonProperty("astronomical_twilight_end")
	val astronomicalTwilightEnd: String? = null,

	@field:JsonProperty("astronomical_twilight_begin")
	val astronomicalTwilightBegin: String? = null,

	@field:JsonProperty("sunset")
	val sunset: String,

	@field:JsonProperty("civil_twilight_end")
	val civilTwilightEnd: String? = null,

	@field:JsonProperty("nautical_twilight_end")
	val nauticalTwilightEnd: String? = null,

	@field:JsonProperty("civil_twilight_begin")
	val civilTwilightBegin: String? = null,

	@field:JsonProperty("nautical_twilight_begin")
	val nauticalTwilightBegin: String? = null

): SunPathArc {
	override var sunriseTime: String= "No Time"
	override var sunsetTime: String= "No Time"
	override var noonTime: String = "No Time"

	override var sunriseAngle: Float = 0f
	override var sunsetAngle: Float = 0f
	override var noonAngle: Float = 0f

	override var sunDistanceRadius: Float = 0f

}
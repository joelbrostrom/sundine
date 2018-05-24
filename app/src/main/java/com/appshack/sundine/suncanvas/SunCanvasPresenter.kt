package com.appshack.sundine.suncanvas

import android.util.Log
import com.appshack.sundine.network.RequestDispatcher
import com.appshack.sundine.network.requests.SunPathRequest
import com.appshack.sundine.network.responsemodels.ApiResponseDataModel
import com.appshack.sundine.network.responsemodels.SunPathDataModel
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.gson.JsonParser
import net.e175.klaus.solarpositioning.Grena3
import net.e175.klaus.solarpositioning.SPA
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.util.*


/**
 * Created by joelbrostrom on 2018-05-21
 * Developed by App Shack
 */
class SunCanvasPresenter(val viewController: SunCanvasMVP.View) : SunCanvasMVP.Presenter {

    private val ESTIMATED_DELTA_T = 68.0
    private val minutesPerDay = 24f * 60f

    fun getPos() {

        val calendar = GregorianCalendar()

        val azimuthZenithAngle = Grena3.calculateSolarPosition(calendar,
                59.8599454,
                17.6403475,
                ESTIMATED_DELTA_T)

        val sunRiseSetTransit = SPA.calculateSunriseTransitSet(calendar,
                59.8599454,
                17.6403475,
                ESTIMATED_DELTA_T)

        val azimuthZenithAngleRise = Grena3.calculateSolarPosition(sunRiseSetTransit[0],
                59.8599454,
                17.6403475,
                ESTIMATED_DELTA_T)

        val azimuthZenithAngleNoon = Grena3.calculateSolarPosition(sunRiseSetTransit[1],
                59.8599454,
                17.6403475,
                ESTIMATED_DELTA_T)

        val azimuthZenithAngleSet = Grena3.calculateSolarPosition(sunRiseSetTransit[2],
                59.8599454,
                17.6403475,
                ESTIMATED_DELTA_T)

        Log.d("@dev", azimuthZenithAngle.toString())
        Log.d("@dev", "sunriseTime: ${sunRiseSetTransit[0].time}")
        Log.d("@dev", "solarNoon: ${sunRiseSetTransit[1].time}")
        Log.d("@dev", "sunsetTime: ${sunRiseSetTransit[2].time}")
        Log.d("@dev solarrise:", azimuthZenithAngleRise.toString())
        Log.d("@dev solarNoon:", azimuthZenithAngleNoon.toString())
        Log.d("@dev solarset:", azimuthZenithAngleSet.toString())
       // Log.d("@dev", sunRiseSetTransit.contentToString())
    }

    override fun getSunHeightPosition() {
        getPos()
        val dispatcher = RequestDispatcher()
        val request = SunPathRequest(59.8599454f, 17.6403475f)
        val summerSolsticeRequest = SunPathRequest(59.8599454f, 17.6403475f, "2018-06-21")
        val winterSolsticeRequest = SunPathRequest(59.8599454f, 17.6403475f, "2018-12-21")

        dispatcher.dispatch(request, object : Callback {

            override fun onFailure(call: Call?, e: IOException?) {
                viewController.toastMessage(e.toString())
                Log.d("@dev error response", e.toString())
            }

            override fun onResponse(call: Call?, response: Response?) {
                response?.body()?.string()?.let {
                    val mapper = jacksonObjectMapper()
                    val jsonObject = JsonParser().parse(it).asJsonObject.toString()
                    val apiResponseModel: ApiResponseDataModel = mapper.readValue(jsonObject)

                    calculateCanvasAngles(apiResponseModel.sunPathDataModel)
                }
            }
        })
    }

    fun calculateCanvasAngles(sunPathDataModel: SunPathDataModel?) {

        sunPathDataModel?.let {

            convertISOtoShortTimeString(it)
            getAngleForPath(it)

            Log.d("@dev", "sunriseT: ${it.sunriseTime}   sunsetT: ${it.sunsetTime}   " +
                    "noonT: ${it.noonTime}")

            Log.d("@dev", "sunriseA: ${it.sunriseAngle}   sunsetA: ${it.sunsetAngle}   " +
                    "noonA: ${it.noonAngle}")

            viewController.updateCanvas(sunPathDataModel)
        }
    }

    private fun convertISOtoShortTimeString(sunPathDataModel: SunPathDataModel) {

        sunPathDataModel.sunriseTime = sunPathDataModel.sunrise.substringAfter('T').substring(0, 5)
        sunPathDataModel.noonTime = sunPathDataModel.solarNoon.substringAfter('T').substring(0, 5)
        sunPathDataModel.sunsetTime = sunPathDataModel.sunset.substringAfter('T').substring(0, 5)

    }

    private fun convertTimeStringToInt(sunPathTimeString: String): Int {
        val timeInMinutes = sunPathTimeString.substring(0, 2).toInt() * 60 +
                sunPathTimeString.substring(3, 5).toInt()

        return adjustTimeForGMT(timeInMinutes, 120)
    }

    private fun adjustTimeForGMT(minutes: Int, difference: Int): Int {
        return minutes + difference
    }

    private fun getPercentageOfDay(minutes: Int): Float {
        return minutes / minutesPerDay
    }

    private fun getAngleForPath(sunPathDataModel: SunPathDataModel) {

        val sunriseInMinutes = convertTimeStringToInt(sunPathDataModel.sunriseTime)
        val noonInMinutes = convertTimeStringToInt(sunPathDataModel.noonTime)
        val sunsetInMinutes = convertTimeStringToInt(sunPathDataModel.sunsetTime)

        sunPathDataModel.sunriseAngle = getPercentageOfDay(sunriseInMinutes) * 360
        sunPathDataModel.noonAngle = getPercentageOfDay(noonInMinutes) * 360
        sunPathDataModel.sunsetAngle = getPercentageOfDay(sunsetInMinutes) * 360
    }

}


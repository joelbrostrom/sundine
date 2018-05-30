package com.appshack.sundine.suncanvas

import android.util.Log
import com.appshack.sundine.dataclasses.CelestialPoint
import com.appshack.sundine.interfaces.SunCanvasMVP
import com.appshack.sundine.network.responsemodels.SunPathDataModel
import net.e175.klaus.solarpositioning.Grena3
import net.e175.klaus.solarpositioning.SPA
import java.util.*
import kotlin.collections.LinkedHashMap


/**
 * Created by joelbrostrom on 2018-05-21
 * Developed by App Shack
 */
class SunCanvasPresenter(val viewController: SunCanvasMVP.View) : SunCanvasMVP.Presenter {

    private val ESTIMATED_DELTA_T = 68.0
//    private val MINUTES_IN_A_DAY = 24f * 60f

    override fun getSunPaths(): LinkedHashMap<String, SunPathDataModel> {

        val summerPath = SunPathDataModel("summerPath", Date(2018, 5, 21))
        val currentPath = SunPathDataModel("currentPath", Date())
        val winterPath = SunPathDataModel("winterPath", Date(2018, 11, 21))

        val sunPathDataModels: LinkedHashMap<String, SunPathDataModel> = linkedMapOf(
                Pair(summerPath.name, summerPath),
                Pair(currentPath.name, currentPath),
                Pair(winterPath.name, winterPath))

        return setupSunPathModel(sunPathDataModels)

    }

    private fun setupSunPathModel(sunPathDataModels: LinkedHashMap<String, SunPathDataModel>): LinkedHashMap<String, SunPathDataModel> {

        sunPathDataModels.forEach { mapEntry ->
            val calendar = GregorianCalendar()

            calendar.time = mapEntry.value.Date

            val sunTransit = SPA.calculateSunriseTransitSet(
                    calendar,
                    59.8599454,
                    17.6403475,
                    ESTIMATED_DELTA_T).toList()

            val azimuthZenithAngleRise = Grena3.calculateSolarPosition(
                    sunTransit[0],
                    59.8599454,
                    17.6403475,
                    ESTIMATED_DELTA_T)

            val azimuthZenithAngleNoon = Grena3.calculateSolarPosition(
                    sunTransit[1],
                    59.8599454,
                    17.6403475,
                    ESTIMATED_DELTA_T)

            val azimuthZenithAngleSet = Grena3.calculateSolarPosition(
                    sunTransit[2],
                    59.8599454,
                    17.6403475,
                    ESTIMATED_DELTA_T)

            val azimuthZenithAngleCurrent = SPA.calculateSolarPosition(
                    calendar,59.8599454,
                    17.6403475,
                    50.0,
                    ESTIMATED_DELTA_T)


            mapEntry.value.sunrisePosition = CelestialPoint(
                    azimuthZenithAngleRise.azimuth.toFloat(),
                    azimuthZenithAngleRise.zenithAngle.toFloat())

            mapEntry.value.solarNoonPosition = CelestialPoint(
                    azimuthZenithAngleNoon.azimuth.toFloat(),
                    azimuthZenithAngleNoon.zenithAngle.toFloat())

            mapEntry.value.sunsetPosition = CelestialPoint(
                    azimuthZenithAngleSet.azimuth.toFloat(),
                    azimuthZenithAngleSet.zenithAngle.toFloat())

            mapEntry.value.currentSolarPosition = CelestialPoint(
                    azimuthZenithAngleCurrent.azimuth.toFloat(),
                    azimuthZenithAngleCurrent.zenithAngle.toFloat())

            Log.d("@dev solarRise", "${sunTransit[0].time}$azimuthZenithAngleRise")
            Log.d("@dev solarNoon", "${sunTransit[1].time}$azimuthZenithAngleNoon")
            Log.d("@dev solarSet", " ${sunTransit[2].time}$azimuthZenithAngleSet")
            Log.d("@dev currentPosition", " ${sunTransit[1].time}$azimuthZenithAngleCurrent")
            Log.d("@dev", " ------------------------------------------------------------")

        }
        return sunPathDataModels
    }

}


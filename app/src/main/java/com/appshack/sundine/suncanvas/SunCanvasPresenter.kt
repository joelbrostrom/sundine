package com.appshack.sundine.suncanvas

import android.util.Log
import com.appshack.sundine.dataclasses.CelestialPoint
import com.appshack.sundine.interfaces.SunCanvasMVP
import com.appshack.sundine.network.responsemodels.SunPathDataModel
import com.google.android.gms.maps.model.CameraPosition
import net.e175.klaus.solarpositioning.Grena3
import net.e175.klaus.solarpositioning.SPA
import java.time.Instant
import java.util.*

/**
 * Created by joelbrostrom on 2018-05-21
 * Developed by App Shack
 */
class SunCanvasPresenter(val view: SunCanvasMVP.View) : SunCanvasMVP.Presenter {

    private val ESTIMATED_DELTA_T = 68.0

    override fun getSunPaths(cameraPosition: CameraPosition): LinkedHashMap<String, SunPathDataModel> {

        val now = Date()
        val nowCalendar = GregorianCalendar().apply {
            time = now
        }
        val summerSolsticeCalendar = GregorianCalendar().apply {
            time = now
            set(nowCalendar.get(Calendar.YEAR), 5, 21)
        }
        val winterSolsticeCalendar = GregorianCalendar().apply {
            time = now
            set(nowCalendar.get(Calendar.YEAR), 5, 21)
        }

        val summerPath = SunPathDataModel("summerPath", summerSolsticeCalendar, cameraPosition)
        val currentPath = SunPathDataModel("currentPath", nowCalendar, cameraPosition)
        val winterPath = SunPathDataModel("winterPath", winterSolsticeCalendar, cameraPosition)

        val sunPathDataModels: LinkedHashMap<String, SunPathDataModel> = linkedMapOf(
                Pair(summerPath.name, summerPath),
                Pair(currentPath.name, currentPath),
                Pair(winterPath.name, winterPath))
        Log.d("@dev getSunPath", "local time: ${nowCalendar.time}")
        return setupSunPathModel(sunPathDataModels)

    }

    private fun setupSunPathModel(sunPathDataModels: LinkedHashMap<String, SunPathDataModel>): LinkedHashMap<String, SunPathDataModel> {

        sunPathDataModels.forEach { mapEntry ->
            val cameraPosition = mapEntry.value.cameraPosition

            val sunTransit = SPA.calculateSunriseTransitSet(
                    mapEntry.value.gregorianCalendar,
                    cameraPosition.target.latitude,
                    cameraPosition.target.longitude,
                    ESTIMATED_DELTA_T)

            mapEntry.value.currentSolarPosition = setUpCelestialPoint(mapEntry.value.gregorianCalendar, cameraPosition)

            mapEntry.value.sunrisePosition = setUpCelestialPoint(sunTransit[0], cameraPosition)

            mapEntry.value.solarNoonPosition = setUpCelestialPoint(sunTransit[1], cameraPosition)

            mapEntry.value.sunsetPosition = setUpCelestialPoint(sunTransit[2], cameraPosition)

//            Log.d("@dev solarRise", "${sunTransit[0].time}$azimuthZenithAngleRise")
//            Log.d("@dev solarNoon", "${sunTransit[1].time}$azimuthZenithAngleNoon")
//            Log.d("@dev solarSet", " ${sunTransit[2].time}$azimuthZenithAngleSet")
//            Log.d("@dev currentPosition", " ${sunTransit[1].time}$azimuthZenithAngleCurrent")
//            Log.d("@dev", " ------------------------------------------------------------")

        }
        return sunPathDataModels
    }

    private fun setUpCelestialPoint(gregorianCalendar: GregorianCalendar?, cameraPosition: CameraPosition): CelestialPoint {

        return gregorianCalendar?.let {
            val azimuthZenithAngle = Grena3.calculateSolarPosition(
                    gregorianCalendar,
                    cameraPosition.target.latitude,
                    cameraPosition.target.longitude,
                    ESTIMATED_DELTA_T)

            CelestialPoint(
                    it.time,
                    azimuthZenithAngle.azimuth.toFloat(),
                    azimuthZenithAngle.zenithAngle.toFloat())

        } ?: CelestialPoint(Date(0), 360f, 90f)
    }

}


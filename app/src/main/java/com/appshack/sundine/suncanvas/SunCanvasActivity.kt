package com.appshack.sundine.suncanvas

import android.app.Activity
import android.os.Bundle
import com.appshack.sundine.R
import com.appshack.sundine.extensions.toast
import com.appshack.sundine.interfaces.SunCanvasMVP
import com.appshack.sundine.network.responsemodels.SunPathDataModel
import kotlinx.android.synthetic.main.activity_sun_canvas.*


/**
 * Created by joelbrostrom on 2018-05-03
 * Developed by App Shack
 */
class SunCanvasActivity : Activity(), SunCanvasMVP.View {

    private val sunCanvasPresenter = SunCanvasPresenter(this)
    lateinit var sunPath: LinkedHashMap<String, SunPathDataModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sun_canvas)

        sunPath = sunCanvasPresenter.getSunPaths()

        reloadButton.setOnClickListener { updateCanvas(sunPath) }

        updateCanvas(sunPath)
    }

    override fun toastMessage(message: String) {
        runOnUiThread {
            toast(message)
        }
    }

    private fun updateCanvas(sunPathDataModels: LinkedHashMap<String, SunPathDataModel>) {
        runOnUiThread {
            sun_canvas_view.updateCanvas(sunPathDataModels)
            sun_canvas_view.invalidate()
        }
    }

}


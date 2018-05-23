package com.appshack.sundine.suncanvas

import android.app.Activity
import android.os.Bundle
import com.appshack.sundine.R
import com.appshack.sundine.extensions.toast
import com.appshack.sundine.network.responsemodels.SunPathDataModel
import kotlinx.android.synthetic.main.sun_canvas.*


/**
 * Created by joelbrostrom on 2018-05-03
 * Developed by App Shack
 */
class SunCanvasActivity : Activity(), SunCanvasMVP.View {

    private val sunCanvasPresenter = SunCanvasPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.sun_canvas)
        sunCanvasPresenter.getSunHeightPosition()
    }

    override fun toastMessage(message: String) {
        runOnUiThread {
            toast(message)
        }
    }

    override fun updateCanvas(sunPathDataModel: SunPathDataModel) {
        runOnUiThread {
            sun_canvas_view.updateCanvas(sunPathDataModel)
            sun_canvas_view.invalidate()
        }
    }

}


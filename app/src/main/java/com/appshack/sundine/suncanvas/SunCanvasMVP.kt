package com.appshack.sundine.suncanvas

import com.appshack.sundine.network.responsemodels.SunPathDataModel


/**
 * Created by joelbrostrom on 2018-05-21
 * Developed by App Shack
 */

interface SunCanvasMVP {

    interface View {
        fun updateCanvas(sunPathDataModel: SunPathDataModel)
        fun toastMessage(message: String)
    }

    interface Presenter {
        fun getSunHeightPosition()
    }
}
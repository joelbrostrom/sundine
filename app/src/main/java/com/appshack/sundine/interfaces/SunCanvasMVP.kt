package com.appshack.sundine.interfaces

import com.appshack.sundine.network.responsemodels.SunPathDataModel
import com.google.android.gms.maps.model.CameraPosition


/**
 * Created by joelbrostrom on 2018-05-21
 * Developed by App Shack
 */

interface SunCanvasMVP {

    interface View {
        fun toastMessage(message: String)
    }

    interface Presenter {
        fun getSunPaths(cameraPosition: CameraPosition): Map<String, SunPathDataModel>
    }
}
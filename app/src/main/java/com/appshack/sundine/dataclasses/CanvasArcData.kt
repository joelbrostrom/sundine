package com.appshack.sundine.dataclasses

import android.graphics.Paint
import android.graphics.RectF


/**
 * Created by joelbrostrom on 2018-05-24
 * Developed by App Shack
 */

data class CanvasArcData(var rectF: RectF,
                         val startAngle: Float,
                         val sweepingAngle: Float,
                         val paint: Paint,
                         val fillCenter: Boolean = false)
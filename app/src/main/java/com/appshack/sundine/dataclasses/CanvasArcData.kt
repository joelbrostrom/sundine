package com.appshack.sundine.dataclasses

import android.graphics.Paint
import android.graphics.RectF
import com.appshack.sundine.interfaces.ShapeInterface


/**
 * Created by joelbrostrom on 2018-05-24
 * Developed by App Shack
 */

data class CanvasArcData(override var rectF: RectF,
                         val startAngle: Float,
                         val sweepingAngle: Float,
                         override val paint: Paint,
                         val fillCenter: Boolean = false)
    : ShapeInterface
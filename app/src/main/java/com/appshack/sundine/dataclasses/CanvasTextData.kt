package com.appshack.sundine.dataclasses

import android.graphics.Paint
import android.graphics.RectF
import com.appshack.sundine.interfaces.ShapeInterface


/**
 * Created by joelbrostrom on 2018-06-04
 * Developed by App Shack
 */
data class CanvasTextData(
        val string: String,
        val xCord: Float,
        val yCord: Float,
        override val paint: Paint,
        override var rectF: RectF = RectF(0f, 0f, 0f, 0f))
    : ShapeInterface

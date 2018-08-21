package com.appshack.sundine.dataclasses

import android.graphics.Paint
import android.graphics.RectF
import com.appshack.sundine.interfaces.ShapeInterface


/**
 * Created by joelbrostrom on 2018-06-04
 * Developed by App Shack
 */

data class CanvasOval(override var rectF: RectF, override val paint: Paint) : ShapeInterface
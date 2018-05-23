package com.appshack.sundine

import android.content.Context
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.support.v4.content.ContextCompat


/**
 * Created by joelbrostrom on 2018-05-23
 * Developed by App Shack
 */

class PaintFactory(private val context: Context) {

    fun getPaint(type: PaintType): Paint {
        return when (type) {
            PaintType.BLACK_STROKE -> blackStroke
            PaintType.BLACK_STROKE_THICK -> blackStrokeThick
            PaintType.YELLOW_FILL -> yellowFill
            PaintType.GREEN_STROKE -> greenStroke
            PaintType.PINK_FILL -> pinkFill
            PaintType.CUTTING -> cuttingPaint
        }
    }

    private val blackStroke: Paint
        get() {
            val p = Paint()
            p.isAntiAlias = true
            p.color = ContextCompat.getColor(context, R.color.black)
            p.style = Paint.Style.STROKE
            p.strokeWidth = 5f
            p.alpha = 0xFF
            p.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)


            return p
        }


    private val blackStrokeThick: Paint
        get() {
            val p = Paint()
            p.isAntiAlias = true
            p.color = ContextCompat.getColor(context, R.color.black)
            p.style = Paint.Style.STROKE
            p.strokeWidth = 10f
            p.alpha = 0xFF
            p.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)


            return p
        }

    private val yellowFill: Paint
        get() {
            val p = Paint()
            p.isAntiAlias = true
            p.color = ContextCompat.getColor(context, R.color.yellow)
            p.style = Paint.Style.FILL
            p.strokeWidth = 5f
            p.alpha = 0xFF

            return p
        }

    private val greenStroke: Paint
        get() {
            val p = Paint()
            p.isAntiAlias = true
            p.color = ContextCompat.getColor(context, R.color.green)
            p.style = Paint.Style.STROKE
            p.strokeWidth = 15f
            p.alpha = 0xFF
            p.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)


            return p
        }

    private val pinkFill: Paint
        get() {
            val p = Paint()
            p.isAntiAlias = true
            p.color = ContextCompat.getColor(context, R.color.pink)
            p.style = Paint.Style.FILL
            p.strokeWidth = 5f
            p.alpha = 0xFF
            p.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)

            return p
        }


    private val cuttingPaint: Paint
        get() {
            val p = Paint()
            p.isAntiAlias = true
            p.color = ContextCompat.getColor(context, R.color.pink)
            p.style = Paint.Style.FILL_AND_STROKE
            p.strokeWidth = 5f
            p.alpha = 0xFF
            p.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)

            return p
        }

    enum class PaintType {
        BLACK_STROKE(),
        BLACK_STROKE_THICK(),
        YELLOW_FILL(),
        GREEN_STROKE(),
        PINK_FILL(),
        CUTTING()
    }

}

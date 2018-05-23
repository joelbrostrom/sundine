package com.appshack.sundine.suncanvas

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.drawable.shapes.ArcShape
import android.util.AttributeSet
import android.view.View
import com.appshack.sundine.DrawableArc
import com.appshack.sundine.PaintFactory
import com.appshack.sundine.network.responsemodels.SunPathDataModel


class SunCanvasView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {
    val drawableArcs: MutableList<DrawableArc> = mutableListOf()
    private val lowestSunPathRectF = RectF(0f, 100f, 1000f, 900f)
    private val currentSunPathRectF = RectF(0f, 200f, 1000f, 800f)
    private val highestSunPathRectF = RectF(0f, 300f, 1000f, 700f)
    private val outerCircleRectF = RectF(0f, 0f, 1000f, 1000f)

    private var currentSunriseAngle = 270f
    private var currentSunsetSweepingAngle = 90f
    val paintFactory = PaintFactory(context)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        setLayerType(LAYER_TYPE_HARDWARE, null)
        setCanvasSize()

        canvas?.drawArc(lowestSunPathRectF, 0f, 180f, false, paintFactory.getPaint(PaintFactory.PaintType.YELLOW_FILL))
        canvas?.drawArc((lowestSunPathRectF), 0f, 180f, false, paintFactory.getPaint(PaintFactory.PaintType.BLACK_STROKE))
        canvas?.drawArc((highestSunPathRectF), 0f, 180f, false, paintFactory.getPaint(PaintFactory.PaintType.CUTTING))
        canvas?.drawArc((currentSunPathRectF), currentSunriseAngle, currentSunsetSweepingAngle, false, paintFactory.getPaint(PaintFactory.PaintType.GREEN_STROKE))
        canvas?.drawArc((highestSunPathRectF), 0f, 180f, false, paintFactory.getPaint(PaintFactory.PaintType.BLACK_STROKE))
        canvas?.drawOval((outerCircleRectF), paintFactory.getPaint(PaintFactory.PaintType.BLACK_STROKE_THICK))

        for (step in 0..35) {

            val startX = Math.cos(
                    Math.toRadians(currentSunriseAngle.toDouble() + 10 * step)) *
                    (currentSunPathRectF.bottom - currentSunPathRectF.top) /
                    2 + currentSunPathRectF.centerX()

            val startY = Math.sin(
                    Math.toRadians(currentSunriseAngle.toDouble() + 10 * step)) *
                    (currentSunPathRectF.bottom - currentSunPathRectF.top) /
                    2 + currentSunPathRectF.centerY()

            canvas?.drawCircle(startX.toFloat(), startY.toFloat(), 10f, paintFactory.getPaint(PaintFactory.PaintType.PINK_FILL))

        }
    }

    fun updateCanvas(sunPathDataModel: SunPathDataModel) {

        currentSunriseAngle = adjustOrigoAngleToTop(sunPathDataModel.sunriseAngle)

        currentSunsetSweepingAngle = sunPathDataModel.sunsetAngle -
                sunPathDataModel.sunriseAngle

        currentSunPathRectF.set(0f, 0f, 1000f, 1000f)
    }

    private fun adjustOrigoAngleToTop(angle: Float): Float {
        return angle - 90f
    }

    private fun setCanvasSize() {
        val lp = layoutParams
        lp.width = outerCircleRectF.right.toInt() + paddingEnd
        lp.height = outerCircleRectF.bottom.toInt() + paddingBottom
        layoutParams = lp
    }

//    private fun addPadding(rectF: RectF): RectF {
//        return RectF(rectF.left + paddingStart, rectF.top + paddingTop, rectF.right - paddingEnd,
//                rectF.bottom - paddingBottom)
//    }
}
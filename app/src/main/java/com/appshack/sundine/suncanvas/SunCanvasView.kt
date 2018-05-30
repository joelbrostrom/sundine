package com.appshack.sundine.suncanvas

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import com.appshack.sundine.PaintFactory
import com.appshack.sundine.dataclasses.CanvasArcData
import com.appshack.sundine.network.responsemodels.SunPathDataModel


class SunCanvasView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {


    val RADIUS: Float
        get() {
            val metrics = DisplayMetrics()
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(metrics)
            return metrics.widthPixels / 2f - paddingTop
        }

    val DIAMETER = RADIUS * 2f


    private var canvasSunPathDataModels: LinkedHashMap<String, SunPathDataModel> = linkedMapOf()

    private val outerCircleRectF = RectF(0f, 0f, DIAMETER, DIAMETER).addPadding(this)

    private val paintFactory = PaintFactory(context)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        setLayerType(LAYER_TYPE_HARDWARE, null)
        setCanvasSize()

        canvas?.drawOval((outerCircleRectF), paintFactory.getPaint(PaintFactory.PaintType.YELLOW_FILL_TRANS))

        drawCanvasArc(canvas, canvasSunPathDataModels["cuttingPath"]?.canvasArcData)

        drawCanvasArc(canvas, canvasSunPathDataModels["summerPath"]?.canvasArcData)

        drawCanvasArc(canvas, canvasSunPathDataModels["currentPath"]?.canvasArcData)

        drawCanvasArc(canvas, canvasSunPathDataModels["winterPath"]?.canvasArcData)

        canvas?.drawOval((outerCircleRectF), paintFactory.getPaint(PaintFactory.PaintType.BLACK_STROKE_THICK))

        canvas?.drawArc(outerCircleRectF,
                adjustAngleToTop(canvasSunPathDataModels["currentPath"]?.currentSolarPosition?.azimuth!!),
                3f,
                true,
                paintFactory.getPaint(PaintFactory.PaintType.PINK_FILL))

        //    drawGrid(canvas)


//        for (step in 0..35) {
//
//            val startX = Math.cos(
//                    Math.toRadians(currentSunriseAngle.toDouble() + 10 * step)) *
//                    (currentSunPathRectF.bottom - currentSunPathRectF.top) /
//                    2 + currentSunPathRectF.centerX()
//
//            val startY = Math.sin(
//                    Math.toRadians(currentSunriseAngle.toDouble() + 10 * step)) *
//                    (currentSunPathRectF.bottom - currentSunPathRectF.top) /
//                    2 + currentSunPathRectF.centerY()
//
//            canvas?.drawCircle(startX.toFloat(), startY.toFloat(), 10f, paintFactory.getPaint(PaintFactory.PaintType.PINK_FILL))
//
//        }

    }

    fun updateCanvas(sunPathDataModels: LinkedHashMap<String, SunPathDataModel>) {

        sunPathDataModels.forEach { sunPathDataModel ->

            val startAngle = adjustAngleToTop(sunPathDataModel.value.sunrisePosition.azimuth)
            val sweepingAngle = sunPathDataModel.value.sunsetPosition.azimuth - sunPathDataModel.value.sunrisePosition.azimuth

            val top = RADIUS * (1f - sunPathDataModel.value.sunsetPosition.getZenithMultiplier())
            val bottom = RADIUS + RADIUS * sunPathDataModel.value.solarNoonPosition.getZenithMultiplier()

            val widthPadding = (DIAMETER - (bottom - top)) / 2

            val left = 0f + widthPadding
            val right = DIAMETER - widthPadding

            val paint = when (sunPathDataModel.key) {
                "summerPath" -> paintFactory.getPaint(PaintFactory.PaintType.BLACK_STROKE)
                "currentPath" -> paintFactory.getPaint(PaintFactory.PaintType.BLACK_STROKE)
                "winterPath" -> paintFactory.getPaint(PaintFactory.PaintType.BLACK_STROKE)
                else -> paintFactory.getPaint(PaintFactory.PaintType.BLACK_STROKE)
            }

            sunPathDataModel.value.canvasArcData = CanvasArcData(
                    RectF(left, top, right, bottom).addPadding(this),
                    startAngle,
                    sweepingAngle,
                    paint)

        }

        val summerPathData = sunPathDataModels["summerPath"]
        summerPathData?.let {
            val cuttingPath = SunPathDataModel("cuttingPath", it.Date)
            cuttingPath.canvasArcData = CanvasArcData(it.canvasArcData.rectF, 0f, 360f, paintFactory.getPaint(PaintFactory.PaintType.CUTTING))
            sunPathDataModels.put("cuttingPath", cuttingPath)
        }


        canvasSunPathDataModels = sunPathDataModels

    }

    private fun adjustAngleToTop(angle: Float): Float {
        return angle - 90f
    }

//    private fun addPadding(rectF: RectF) : RectF {
//        with(rectF) {
//            return RectF(left+paddingStart,top+paddingTop,right+paddingStart,bottom+paddingTop)
//        }
//    }

    private fun setCanvasSize() {
        val lp = layoutParams
        lp.width = outerCircleRectF.right.toInt() + paddingStart
        lp.height = outerCircleRectF.bottom.toInt() + paddingTop
        layoutParams = lp
    }


    private fun drawGrid(canvas: Canvas?) {
        for (i in 0..10) {

            val startX = RADIUS * Math.abs(Math.cos(Math.toRadians(i * 18.0)))
            val endX = DIAMETER - startX //RADIUS * Math.abs(Math.cos( Math.toRadians(i * 18.0))) + RADIUS
            val startY = i * DIAMETER / 10 //DIAMETER * Math.abs(Math.sin( i * 18.0)) +RADIUS
            val endY = i * DIAMETER / 10 //DIAMETER - startY
            canvas?.drawLine(startX.toFloat(), startY, endX.toFloat(), endY, paintFactory.getPaint(PaintFactory.PaintType.BLACK_STROKE))
        }
    }

    private fun drawCanvasArc(canvas: Canvas?, canvasArcData: CanvasArcData?) =
            canvasArcData?.let {
                canvas?.drawArc(it.rectF, it.startAngle, it.sweepingAngle, it.fillCenter, it.paint)
            }
}

fun RectF.addPadding(view: View): RectF = RectF(
        left + view.paddingStart,
        top + view.paddingTop,
        right + view.paddingStart,
        bottom + view.paddingTop)

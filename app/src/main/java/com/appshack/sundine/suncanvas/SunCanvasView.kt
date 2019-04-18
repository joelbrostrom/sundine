package com.appshack.sundine.suncanvas

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import com.appshack.sundine.PaintFactory
import com.appshack.sundine.dataclasses.CanvasArcData
import com.appshack.sundine.dataclasses.CanvasOval
import com.appshack.sundine.dataclasses.CanvasTextData
import com.appshack.sundine.dataclasses.CelestialPoint
import com.appshack.sundine.interfaces.ShapeInterface
import com.appshack.sundine.network.responsemodels.SunPathDataModel
import net.e175.klaus.solarpositioning.Grena3
import java.text.SimpleDateFormat
import java.util.*

class SunCanvasView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private val PADDING = 128
    private val SCREEN_WIDTH: Int
        get() {
            val metrics = DisplayMetrics()
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(metrics)
            return metrics.widthPixels
        }

    private val RADIUS = SCREEN_WIDTH / 2f
    private val outerCircleRectF = RectF(0f, 0f, 2 * RADIUS, 2 * RADIUS).addPadding(PADDING)

    private var canvasSunPathDataModels: LinkedHashMap<String, SunPathDataModel> = linkedMapOf()
    private val paintFactory = PaintFactory(context)

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        setLayerType(LAYER_TYPE_HARDWARE, null)
        setCanvasSize()

        val layerList = stackLayers()

        layerList.forEach {

            when (it) {
                is CanvasArcData -> {
                    canvas?.drawArc(it.rectF, it.startAngle, it.sweepingAngle, it.fillCenter, it.paint)
                }

                is CanvasOval -> {
                    canvas?.drawOval(it.rectF, it.paint)
                }

                is CanvasTextData -> {
                    canvas?.drawText(it.string, it.xCord, it.yCord, it.paint)
                }
            }
        }
    }

    private fun stackLayers(): MutableList<ShapeInterface> {
        val canvasLayerList = mutableListOf<ShapeInterface>()

        canvasLayerList.add(CanvasOval(outerCircleRectF, paintFactory.getPaint(PaintFactory.PaintType.YELLOW_FILL_TRANS)))

        canvasSunPathDataModels["cuttingPath"]?.canvasArcData?.let { canvasLayerList.add(it) }

        canvasSunPathDataModels["summerPath"]?.canvasArcData?.let { canvasLayerList.add(it) }

        canvasSunPathDataModels["currentPath"]?.canvasArcData?.let { canvasLayerList.add(it) }

        canvasSunPathDataModels["winterPath"]?.canvasArcData?.let { canvasLayerList.add(it) }

        canvasLayerList.add(CanvasOval(outerCircleRectF, paintFactory.getPaint(PaintFactory.PaintType.BLACK_STROKE_THICK)))

        canvasSunPathDataModels["currentPositionMarker"]?.canvasArcData?.let { canvasLayerList.add(it) }

        val digits = canvasSunPathDataModels["currentPositionMarker"]?.let { getFaceDigits(it) }
                ?: mutableListOf()

        canvasLayerList.addAll(digits)

        return canvasLayerList
    }

    fun updateCanvas(sunPathDataModels: LinkedHashMap<String, SunPathDataModel>) {

        sunPathDataModels.forEach { setupCanvasArcData(it) }

        sunPathDataModels["summerPath"]?.let {

            val cuttingPath = SunPathDataModel("cuttingPath", it.gregorianCalendar, it.cameraPosition)

            cuttingPath.canvasArcData = CanvasArcData(
                    it.canvasArcData.rectF,
                    it.canvasArcData.startAngle.adjustAngleToTop() - it.cameraPosition.bearing,
                    360f,
                    paintFactory.getPaint(PaintFactory.PaintType.CUTTING))

            sunPathDataModels.put(cuttingPath.name, cuttingPath)
        }

        sunPathDataModels["currentPath"]?.let {
            val currentPositionMarker = SunPathDataModel("currentPositionMarker", it.gregorianCalendar, it.cameraPosition)
            currentPositionMarker.canvasArcData = CanvasArcData(
                    outerCircleRectF,
                    it.currentSolarPosition.azimuth.adjustAngleToTop() - it.cameraPosition.bearing - 2f,
                    2f,
                    paintFactory.getPaint(PaintFactory.PaintType.PINK_FILL),
                    true)

            sunPathDataModels.put(currentPositionMarker.name, currentPositionMarker)
        }
        canvasSunPathDataModels = sunPathDataModels
    }

    private fun setupCanvasArcData(sunPathDataModel: Map.Entry<String, SunPathDataModel>) {
        val sunPathModel = sunPathDataModel.value

        val sunriseAngle = sunPathModel.sunrisePosition.azimuth //angle of sunrise horizontal position
        val sunsetAngle = sunPathModel.sunsetPosition.azimuth   //angle of sunsets horizontal position
        val sweepingAngle = sunsetAngle - sunriseAngle          //degrees between sunrise and sunset

        val innerDiameter = RADIUS + RADIUS * sunPathModel.solarNoonPosition.getZenithMultiplier()
        val verticalPadding = (RADIUS * 2 - innerDiameter) * Math.sin(Math.toRadians(sunPathModel.cameraPosition.bearing.toDouble() / 2.0)).toFloat()
        val bottom = innerDiameter + verticalPadding
        val top = verticalPadding

        val heightRadius = innerDiameter / 2

        val leftWidthPadding = getArcWithDifference(sunPathModel.sunsetPosition, heightRadius)
        val rightWidthPadding = getArcWithDifference(sunPathModel.sunrisePosition, heightRadius)

        val left = 0f + leftWidthPadding
        val right = 2 * RADIUS + rightWidthPadding

        val paint = when (sunPathDataModel.key) {
            "summerPath" -> paintFactory.getPaint(PaintFactory.PaintType.BLACK_STROKE)
            "currentPath" -> paintFactory.getPaint(PaintFactory.PaintType.BLUE_STROKE)
            "winterPath" -> paintFactory.getPaint(PaintFactory.PaintType.BLACK_STROKE)
            else -> paintFactory.getPaint(PaintFactory.PaintType.BLACK_STROKE)
        }

        sunPathDataModel.value.canvasArcData = CanvasArcData(
                RectF(left, top, right, bottom).addPadding(PADDING),
                sunriseAngle.adjustAngleToTop() - sunPathModel.cameraPosition.bearing,
                sweepingAngle,
                paint)
    }

    private fun getFaceDigits(currentPositionModel: SunPathDataModel): MutableList<CanvasTextData> {
        val canvasTimeStamps = mutableListOf<CanvasTextData>()
        val year = currentPositionModel.gregorianCalendar.get(Calendar.YEAR)
        val month = currentPositionModel.gregorianCalendar.get(Calendar.MONTH)
        val day = currentPositionModel.gregorianCalendar.get(Calendar.DAY_OF_MONTH)

        for (hour in 0..23) {
            val dateWithTime = GregorianCalendar()
            dateWithTime.set(year, month, day, hour,0)
            val azimuthZenithAngle = Grena3.calculateSolarPosition(
                    dateWithTime,
                    currentPositionModel.cameraPosition.target.latitude,
                    currentPositionModel.cameraPosition.target.longitude,
                    68.0)
            val timeString = hour.toString()
            val innerRadius = (outerCircleRectF.bottom - outerCircleRectF.top) / 2
            val angle = azimuthZenithAngle.azimuth.adjustAngleToTop() - currentPositionModel.cameraPosition.bearing
            val xCord = RADIUS + innerRadius * 1.15f * Math.cos(Math.toRadians(angle)).toFloat()
            val yCord = RADIUS + innerRadius * 1.15f * Math.sin(Math.toRadians(angle)).toFloat()
            val paint = paintFactory.getPaint(PaintFactory.PaintType.BLACK_STROKE)
            paint.textAlign = Paint.Align.CENTER
            paint.textSize = 40f


            val canvasTextData = CanvasTextData(timeString, xCord, yCord, paint)
            canvasTimeStamps.add(canvasTextData)
        }
        return canvasTimeStamps
    }

    private fun getArcWithDifference(celestialPoint: CelestialPoint, innerRadius: Float): Float {
        val radiusDifference = RADIUS - innerRadius
        val widthDifference = radiusDifference * Math.cos(Math.toRadians(celestialPoint.azimuth.toDouble() + 90))

        return widthDifference.toFloat()
    }

    private fun getRiseCurrentSetTime(sunPathDataModel: SunPathDataModel?): List<CanvasTextData> {
        val canvasTimeStamps = mutableListOf<CanvasTextData>()

        sunPathDataModel?.let {
            val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            val celestialPoints = listOf(it.sunrisePosition, it.currentSolarPosition, it.sunsetPosition)

            val innerRadius = (outerCircleRectF.bottom - outerCircleRectF.top) / 2
            celestialPoints.forEach { celestialPoint ->
                val time = simpleDateFormat.format(celestialPoint.date)
                val angle = celestialPoint.azimuth.adjustAngleToTop().toDouble() - sunPathDataModel.cameraPosition.bearing
                val xCord = RADIUS + innerRadius * 1.15f * Math.cos(Math.toRadians(angle)).toFloat()
                val yCord = RADIUS + innerRadius * 1.15f * Math.sin(Math.toRadians(angle)).toFloat()
                val paint = paintFactory.getPaint(PaintFactory.PaintType.BLACK_STROKE)
                paint.textSize = 40f
                paint.textAlign = Paint.Align.CENTER

                canvasTimeStamps.add(CanvasTextData(time, xCord, yCord, paint))

            }
//            Log.d("@dev drawTime", "Time: $timeToDraw   ${String.format("cos(%.0f): %.3f", angle, Math.cos(Math.toRadians(it.currentSolarPosition.azimuth.toDouble())))}   ${String.format("sin: %.3f", Math.sin(Math.toRadians(it.currentSolarPosition.azimuth.toDouble())).toFloat())}   X: ${xCord.roundToInt()}   y: ${yCord.roundToInt()}")
//            canvas?.drawText(timeToDraw, xCord, yCord, paint)
        }
        return canvasTimeStamps
    }

    private fun setCanvasSize() {
        val lp = layoutParams
        lp.width = SCREEN_WIDTH
        lp.height = SCREEN_WIDTH
        layoutParams = lp
    }

}

fun Float.adjustAngleToTop(): Float = this - 90

fun Double.adjustAngleToTop(): Double = this - 90

fun RectF.addPadding(padding: Int): RectF = RectF(
        this.left + padding,
        this.top + padding,
        this.right - padding,
        this.bottom - padding)


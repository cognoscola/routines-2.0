package com.gorillamoa.routines.views

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

private const val START_ANGLE = -90.0f
private const val ANGLE_PER_MINUTE = 6.0f

class TimerView(
        private val cx:Int,
        private val cy:Int,
        private val radius:Int) {

    @Suppress("unused")
    private val tag:String = TimerView::class.java.name

    private val paint = Paint()

    private var endTime = 0L
    private var startTime = 0L
    var minute:Int = 0
        set(value)  {
            field =value
            startTime = System.currentTimeMillis()
            endTime = startTime + value*60L * 1000L
        }

    init {
        paint.apply {
            strokeWidth = 20f
            isAntiAlias = true
            style = Paint.Style.STROKE
            color = Color.GREEN
        }
    }

    /**
     * Draw the Arc
     * @param canvas is the object on which to draw on
     * @param upTime is the time in milliseconds
     */
    fun onDraw(canvas: Canvas, upTime:Long) {
        if ((startTime + endTime + minute) != 0L) {

            if (upTime <= endTime) {

                canvas.drawArc(
                        (cx - radius).toFloat(),
                        (cy - radius).toFloat(),
                        (cx + radius).toFloat(),
                        (cy + radius).toFloat(),
                        START_ANGLE,
                        recalculateAngle(upTime,endTime,minute),
                        false,
                        paint)
            }
        }

    }

    fun reset(){
        minute = 0
        startTime = 0
        endTime = 0
    }

    /**
     * Recalculates the angle that should be drawn
     * We update once per second, but receive millisecond updates
     * @param upTime is the current up time
     * @param endTime is the time at which the alarm should go off
     * @param minute is the user chosen time to elapse
     */
    private fun recalculateAngle(upTime:Long, endTime:Long,minute:Int):Float {

        //lets pretend 1second is one minute for the sake of debugging

        val dtPercent = ((upTime - startTime).toFloat() / (endTime - startTime).toFloat())
        val remainingPerfect = 1.0 - dtPercent
        return (minute * ANGLE_PER_MINUTE * remainingPerfect).toFloat()

    }
}
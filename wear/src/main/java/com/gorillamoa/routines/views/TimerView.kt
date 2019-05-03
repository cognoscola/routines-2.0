package com.gorillamoa.routines.views

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import java.util.*

private const val ZERO_ANGLE = 90.0f
private const val ANGLE_PER_MINUTE = 6.0f
private const val ANGLE_PER_SECOND = 1/60.0f
private const val ANGLE_PER_MILLISECOND = 1/60000.0f

class TimerView(
        private val cx:Int,
        private val cy:Int,
        private val radius:Int) {

    @Suppress("unused")
    private val tag:String = TimerView::class.java.name

    private val paint = Paint()

    private var endTime = 0L
    private var startTime = 0L

    /**
     * The minute at which the alarm must go off
     */
    private var minutes:Int = 0


    fun setSelectedMinute(selectedMinute:Int,calendar: Calendar){

        startTime = System.currentTimeMillis()

        val currentMinute = calendar.get(Calendar.MINUTE)
        minutes =  if (selectedMinute >= currentMinute) {
            selectedMinute - currentMinute
        }else{
            (60 - currentMinute) + selectedMinute
        }
        //calculate the end time. We subtract any seconds so that our line is perfectly on minute
        endTime = startTime + (minutes)*60L * 1000L - (calendar.get(Calendar.SECOND) * 1000L)

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
    fun onDraw(canvas: Canvas, mCalendar: Calendar) {
        if ((startTime + endTime + minutes) != 0L) {

            val upTime = mCalendar.timeInMillis

            if (upTime <= endTime) {

                //rotate the canvas such that the end angle is always the same

                canvas.drawArc(
                        (cx - radius).toFloat(),
                        (cy - radius).toFloat(),
                        (cx + radius).toFloat(),
                        (cy + radius).toFloat(),
                        (mCalendar.get(Calendar.MINUTE) * ANGLE_PER_MINUTE)
                                + ((ANGLE_PER_SECOND) * mCalendar.get(Calendar.SECOND)) - ZERO_ANGLE,
                        recalculateAngle(upTime,endTime,minutes),
                        false,
                        paint)
            }
        }
    }

    fun reset(){
        minutes = 0
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

        //lets pretend 1second is one minutes for the sake of debugging

        val dtPercent = ((upTime - startTime).toFloat() / (endTime - startTime).toFloat())
        val remainingPerfect = 1.0 - dtPercent
        return (minute * ANGLE_PER_MINUTE * remainingPerfect).toFloat()

    }
}
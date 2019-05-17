package com.gorillamoa.routines.views

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.gorillamoa.routines.utils.CircularTimer


class TimerView(
        private val cx:Int,
        private val cy:Int,
        private val radius:Int) {

    @Suppress("unused")
    private val tag:String = TimerView::class.java.name

    private val paint = Paint()


    init {
        paint.apply {
            strokeWidth = 1f
            isAntiAlias = true
            style = Paint.Style.STROKE
            color = Color.argb(100,207,201,213) //207 201 213
            setShadowLayer(3f, 0f, 0f, Color.WHITE)

        }
    }

    /**
     * Draw the Arc
     * @param canvas is the object on which to draw on
     * @param upTime is the time in milliseconds
     */
    fun onDraw(canvas: Canvas, timingObject: CircularTimer) {
        if (timingObject.isRunning()) {

            //rotate the canvas such that the end angle is always the same
            canvas.drawArc(
                    (cx - radius).toFloat(),
                    (cy - radius).toFloat(),
                    (cx + radius).toFloat(),
                    (cy + radius).toFloat(),
                    timingObject.startAngle,
                    timingObject.sweepAngle,
                    false,
                    paint)
        }
    }
}
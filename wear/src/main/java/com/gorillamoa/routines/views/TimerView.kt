package com.gorillamoa.routines.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class TimerView : View {

    var circleColor = DEFAULT_CIRCLE_COLOR
        set(circleColor) {
            field = circleColor
            invalidate()
        }

    private var paint: Paint? = null

    var elapsedTimeSecond = 40
        set(value){

        }

    val startAngle = -90.0f
    var sweepAngle = 0f

    public fun setTime(value:Float){
        sweepAngle = (value / 60) * 360.0f
        invalidate()
    }


    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        paint = Paint()
        paint!!.isAntiAlias = true
        paint!!.style = Paint.Style.STROKE
        paint!!.strokeWidth = 3f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val w = width
        val h = height

        val pl = paddingLeft
        val pr = paddingRight
        val pt = paddingTop
        val pb = paddingBottom

        val usableWidth = w - (pl + pr)
        val usableHeight = h - (pt + pb)

        val radius = (Math.min(usableWidth, usableHeight) / 2)-5
        val cx = pl + usableWidth / 2
        val cy = pt + usableHeight / 2

        paint!!.color = this.circleColor
//        canvas.drawCircle(cx.toFloat(), cy.toFloat(), radius.toFloat(), paint!!)
        canvas.drawArc(
                (cx - radius).toFloat(),
                (cy - radius).toFloat(),
                (cx+radius).toFloat(),
                (cy + radius).toFloat(),
                startAngle,
                sweepAngle,
                false,
                paint!!)
    }

    companion object {
        private val DEFAULT_CIRCLE_COLOR = Color.RED
    }
}
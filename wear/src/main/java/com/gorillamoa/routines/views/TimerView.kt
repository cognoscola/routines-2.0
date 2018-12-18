package com.gorillamoa.routines.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast

class TimerView : View {

    public enum class ClockState{
        undefined,
        set,
        running
    }

    var circleColor = DEFAULT_CIRCLE_COLOR
        set(circleColor) {
            field = circleColor
            invalidate()
        }

    private var grayLine: Paint? = null
    private var greenLine: Paint? = null
    private var counter = 0
    private var maxTime = 0
    private var clockState:ClockState = ClockState.undefined

    private var angleIncrement = 0.0f

    private  var threadHandler:Handler = Handler()

    var elapsedTimeSecond = 40
        set(value){

        }


    fun getState():ClockState {
        return this.clockState
    }


    val startAngle = -90.0f
    var sweepAngle = 0f

    public fun setCountdownSeconds(value:Int){
        clockState = ClockState.set
        Toast.makeText(this.context, "armed", Toast.LENGTH_SHORT).show()
        sweepAngle = (60 / 60) * 360.0f

        maxTime = value
        counter = value
        recalculateAngle()





        invalidate()
    }

    public fun start(){
        tick()
        Toast.makeText(this.context, "running",Toast.LENGTH_SHORT).show()
    }


    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        grayLine = Paint()
        grayLine!!.isAntiAlias = true
        grayLine!!.style = Paint.Style.STROKE
        grayLine!!.strokeWidth = 3f
        grayLine!!.color = this.circleColor

        greenLine = Paint()
        greenLine!!.isAntiAlias = true
        greenLine!!.style = Paint.Style.STROKE
        greenLine!!.strokeWidth = 3f
        greenLine!!.color = COMPLETED_CIRCLE_COLOR


        threadHandler = Handler()

        setWillNotDraw(false)

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


        grayLine!!.color = this.circleColor
//        canvas.drawCircle(cx.toFloat(), cy.toFloat(), radius.toFloat(), grayLine!!)
        canvas.drawArc(
                (cx - radius).toFloat(),
                (cy - radius).toFloat(),
                (cx+radius).toFloat(),
                (cy + radius).toFloat(),
                startAngle,
                sweepAngle,
                false,
                grayLine!!)

        greenLine!!.color = COMPLETED_CIRCLE_COLOR
        canvas.drawArc(
                (cx - radius).toFloat(),
                (cy - radius).toFloat(),
                (cx+radius).toFloat(),
                (cy + radius).toFloat(),
                startAngle,
                angleIncrement,
                false,
                greenLine!!)
    }

    private fun recalculateAngle(){
        val temp = ((maxTime.toFloat() - counter.toFloat()).div(maxTime))
        Log.d("recalculateAngle","diffence= ${maxTime - counter} temp = $temp")
        angleIncrement = 360.0f * temp

        Log.d("recalculateAngle","counter= $counter maxtime = ${maxTime} angle = ${angleIncrement}")

    }

    val runnable:Runnable = Runnable {
        recalculateAngle()

        this@TimerView.invalidate()


        if(counter > 0){
            tick()
            counter--
        }else{
            Toast.makeText(this.context, "Done", Toast.LENGTH_SHORT).show()
        }

    }

    fun tick(){
        threadHandler.postDelayed(runnable,1000)
    }




    companion object {
        private val DEFAULT_CIRCLE_COLOR = Color.GRAY
        private val COMPLETED_CIRCLE_COLOR = Color.GREEN
    }


}
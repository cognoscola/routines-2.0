package com.gorillamoa.routines.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Handler
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast


/**
 * A view for the timer. It is a Circle
 */
class TimerView : View {


    enum class ClockState{
        undefined,
        set,
        running
    }

    var circleColor = DEFAULT_CIRCLE_COLOR
        set(circleColor) {
            field = circleColor
            invalidate()
        }


    private var finishCallback:(()->Unit)? = null
    private var textUpdateCallback:((String)->Unit)? = null
    public fun setTextUpdateCallback(writer:(String)->Unit){
       textUpdateCallback = writer
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

        sweepAngle = (60 / 60) * 360.0f

        maxTime = value
        counter = value
        recalculateAngle()
        invalidate()
    }

    public fun start(finishCallback:(()->Unit)?){
        this@TimerView.finishCallback = finishCallback
        tick()
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

        textUpdateCallback?.invoke("$counter")
        this@TimerView.invalidate()


        if(counter < 3){

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val vibratorService = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator


                if (counter == 0) {
                    /*val longArray = LongArray(6)
                    longArray[0] = 0
                    longArray[1] = 200
                    longArray[2] = 200
                    longArray[3] = 200
                    longArray[4] = 200
                    longArray[5] = 400
                    vibratorService.vibrate(VibrationEffect.createWaveform(longArray, -1))*/

                    //1 longer vibrate on the last beep of the timer while on Workout mode
                    vibratorService.vibrate(VibrationEffect.createOneShot(600, VibrationEffect.DEFAULT_AMPLITUDE))
                }else{

                    vibratorService.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                }

            }
        }

        if(counter > 0){
            tick()
            counter--
        }else{
            textUpdateCallback?.invoke("Done")
            clockState = ClockState.undefined
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
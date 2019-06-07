package com.gorillamoa.routines.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.support.wearable.watchface.WatchFaceService
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.gorillamoa.routines.R
import com.gorillamoa.routines.core.data.Task
import com.gorillamoa.routines.core.scheduler.TaskScheduler
import java.lang.StringBuilder
import java.util.*

private const val COMPLETE = "complete"
private const val INCOMPLETE = "incomplete"

//TODO update task if it is deleted somewhere else

private const val taskTextSize =24.0f
private const val timeTextSize =28.0f

private const val TIME_MINUTE_FORMAT = "%02d"
private const val TIME_COLON = ":"
private const val TEXT_HALF_WIDTH = 2.0f

class Foreground{

    //includes buttons, texts and others

    //a list of touchable objects
    private val touchables= ArrayList<ClickableRectangle>()

    private var leftButton:CanvasButton? = null
    private var rightButton:CanvasButton? = null
    private var centerButton:SwitchingButton? = null
    private var switchingButton:SwitchingButton? = null


    companion object {
        val STATE_BREAKS = "break"
        val STATE_TIMER = "time"
        val STATE_ALARM = "alarm"
    }


    //Text Stuff

    private val timeTextPaint = TextPaint().apply {
        color = Color.WHITE
    }
    private val taskTextPaint =TextPaint().apply {
        color = Color.WHITE
        textSize = 24.0f
      /*  setShadowLayer(
                SHADOW_RADIUS, 0f, 0f, mWatchHandShadowColor)*/
    }


    private var timeTextHeight = 0f
    private var textHeight = 0f
    private var staticLayout: StaticLayout? = null
    private var textHalfWidth = 0f

    private var textBuilder = StringBuilder(5)
    private var timeString:String = ""
    private var textWidth:Float = 0.0f

    fun getState() = switchingButton!!.getState()


    /**
     * Note, we need to ensure that our dimensions are initialized. So when we create these features
     * they are given correct values
     */

    fun measureTouchables(context:WatchFaceService, screenWidth:Int, screenHeight:Int,
                          stateButtonCallback:(()->Any?)? = null,
                          leftButtonCallback:(()->Any?)? = null,
                          rightButtonCallback:(()->Any?)? = null,
                          centerButtonCallback:((Boolean)->Any?)? = null) {

//            ClickableRectangle.enableDebug()
        val mCenterX = screenWidth * 0.5f
        val mCenterY = screenHeight * 0.5f

        switchingButton = SwitchingButton(
                mCenterX.toInt(),
                mCenterY.toInt() + (screenHeight * 0.2f).toInt(),
                (screenWidth  * 0.18).toInt(),
                //use width to ensure we get a square object
                (screenWidth  *   0.18).toInt(),
                10,
                context).apply {
            onClickListener = {
                nextState()
                stateButtonCallback?.invoke()
            }
            addState(STATE_BREAKS, R.drawable.ic_break_time)
            addState(STATE_TIMER, R.drawable.ic_hourglass)
            touchables.add(this)
        }

        leftButton = CanvasButton((mCenterX - screenWidth * 0.2f).toInt() ,mCenterY.toInt(),(screenWidth*0.18f).toInt(),(screenWidth*0.18f).toInt()).apply {
            setImage(context, R.drawable.ic_chevron_left_white_24dp)
            onClickListener = {
                leftButtonCallback?.invoke()
            }
            touchables.add(this)
        }

        rightButton = CanvasButton((mCenterX + screenWidth * 0.2f).toInt() ,mCenterY.toInt(),(screenWidth*0.18f).toInt(),(screenWidth*0.18f).toInt()).apply {
            setImage(context, R.drawable.ic_chevron_right_black_24dp)
            onClickListener = {
                rightButtonCallback?.invoke()
            }
            touchables.add(this)
        }

        centerButton = SwitchingButton((mCenterX).toInt() ,(mCenterY).toInt(),(screenWidth*0.18f).toInt(),(screenWidth*0.18f).toInt(),0, context).apply {
            addState(INCOMPLETE, R.drawable.ic_radio_button_unchecked_black_24dp)
            addState(COMPLETE, R.drawable.ic_cc_checkmark)
            onClickListener = {

                centerButtonCallback?.invoke(nextState() == COMPLETE)
            }
            touchables.add(this)
        }

        //measure text height
        textHeight = mCenterY - screenHeight*0.09f - 30f //some text height


        timeTextPaint.isAntiAlias = true
        timeTextPaint.textSize = (timeTextSize * context.resources.displayMetrics.density)
        timeTextHeight =textHeight - timeTextPaint.ascent() + timeTextPaint.descent() - screenHeight*0.16f

    }

    fun configureTaskUI(task: Task?, context:Context){

        //create new text for the task name
        task?.apply {

            //Set the correct title
            val width = taskTextPaint.measureText(this.name)
            textHalfWidth = (width * 0.5).toFloat()

            val sb = StaticLayout.Builder.obtain(this.name, 0, this.name.length, taskTextPaint, width.toInt())
                    .setAlignment(Layout.Alignment.ALIGN_CENTER)
                    .setLineSpacing(0.0f, 1.0f)
                    .setIncludePad(false)
            staticLayout = sb.build()

            //make sure our complete button shows the correct status
            if (com.gorillamoa.routines.core.scheduler.TaskScheduler.isComplete(context, task.id ?: -1)) {
                centerButton?.setState(COMPLETE)
            }else centerButton?.setState(INCOMPLETE)

        }
    }


    fun drawButtons(canvas: Canvas) {
        leftButton?.draw(canvas)
        centerButton?.draw(canvas)
        rightButton?.draw(canvas)
        switchingButton?.draw(canvas)
    }

    fun updateTimeText(calendar: Calendar){
        textBuilder.clear()
        textBuilder.append(String.format(TIME_MINUTE_FORMAT, calendar.get(Calendar.HOUR_OF_DAY)))
        textBuilder.append(TIME_COLON)
        textBuilder.append(String.format(TIME_MINUTE_FORMAT, calendar.get(Calendar.MINUTE)))
        timeString = textBuilder.toString()
        textWidth = timeTextPaint.measureText(timeString)
    }

    fun drawTexts(xPos:Int,canvas: Canvas,mCalendar:Calendar) {

        //draw the task name
        staticLayout?.let {
            canvas.save()
            canvas.translate(xPos - textHalfWidth, textHeight)
            staticLayout!!.draw(canvas)
            canvas.restore()
        }

        //draw the time
        canvas.drawText(timeString,xPos - textWidth.div(TEXT_HALF_WIDTH), timeTextHeight, timeTextPaint)
    }

    /**
     * If this entity should consume the touch event
     */
    fun processTouch(x:Int,y:Int):Boolean{
        touchables.forEach {
            if (it.isTouched(x, y)) {
                return true
            }
        }
        return false
    }


}
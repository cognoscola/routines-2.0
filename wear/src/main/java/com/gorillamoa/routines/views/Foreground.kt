package com.gorillamoa.routines.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.support.wearable.watchface.WatchFaceService
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import com.gorillamoa.routines.R
import com.gorillamoa.routines.data.Task
import com.gorillamoa.routines.scheduler.TaskScheduler
import java.util.ArrayList

private const val COMPLETE = "complete"
private const val INCOMPLETE = "incomplete"

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
    private val mTextPaint=TextPaint().apply {
        color = Color.WHITE
        textSize = 24.0f
    }
    private var textHeight = 0f
    private var staticLayout: StaticLayout? = null
    private var textHalfWidth = 0f

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
                context).apply {
            onClickListener = {
                nextState()
                stateButtonCallback?.invoke()
            }
            addState(STATE_BREAKS, R.drawable.ic_break_time)
            addState(STATE_TIMER, R.drawable.ic_hourglass)
            addState(STATE_ALARM, R.drawable.ic_alarm)
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

        centerButton = SwitchingButton((mCenterX).toInt() ,(mCenterY).toInt(),(screenWidth*0.18f).toInt(),(screenWidth*0.18f).toInt(), context).apply {
            addState(INCOMPLETE, R.drawable.ic_radio_button_unchecked_black_24dp)
            addState(COMPLETE, R.drawable.ic_cc_checkmark)
            onClickListener = {

                centerButtonCallback?.invoke(nextState() == COMPLETE)
            }
            touchables.add(this)
        }

        //measure text height
        textHeight = mCenterY - screenHeight*0.09f - 30f //some text height
    }

    fun configureTaskUI(task: Task?,context:Context){

        //create new text for the task name
        task?.apply {

            //Set the correct title
            val width = mTextPaint.measureText(name)
            textHalfWidth = (width * 0.5).toFloat()

            val sb = StaticLayout.Builder.obtain(name, 0, name.length, mTextPaint, width.toInt())
                    .setAlignment(Layout.Alignment.ALIGN_CENTER)
                    .setLineSpacing(0.0f, 1.0f)
                    .setIncludePad(false)
            staticLayout = sb.build()

            //make sure our complete button shows the correct status
            if (TaskScheduler.isComplete(context, task.id ?: -1)) {
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

    fun drawTexts(xPos:Int,canvas: Canvas) {

        staticLayout?.let {

            canvas.save()
            canvas.translate(xPos - textHalfWidth, textHeight)
            staticLayout!!.draw(canvas)
            canvas.restore()
        }
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
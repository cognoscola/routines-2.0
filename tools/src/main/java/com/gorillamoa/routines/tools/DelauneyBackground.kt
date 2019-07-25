package com.gorillamoa.routines.tools

import android.content.Context
import android.graphics.Canvas
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.gorillamoa.routines.tools.delayneytriangle.LivingBackground
import java.lang.ref.WeakReference


//TODO add functions to accept new colors
//TODO add functionality to morph to new colors

class DelauneyBackground : View {


    private lateinit var livingBackground:LivingBackground


    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        setupAttributes(attrs)
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs){
        setupAttributes(attrs)
    }

    private fun setupAttributes(attrs: AttributeSet?) {
        Log.d("tag setupAttributes","Enters")

        livingBackground = LivingBackground(
                LivingBackground.Graphics.Low,
                true,
                LivingBackground.DENSITY_WATCH,
                LivingBackground.Shape.Landscape,
                context.isWatch())
        livingBackground.initializeBackground()

        // Obtain a typed array of attributes
//        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.SwitchPreference,
//                0, 0)

        // Extract custom attributes into member variables
//        key = typedArray.getString(R.styleable.SwitchPreference_key)?:""

    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)

        Log.d("Background","onVisibilityChanged")

        if(visibility == View.VISIBLE){

//            setLayerType(LAYER_TYPE_HARDWARE,null)
            livingBackground.comeOutOfAmbient()
        }else{
            livingBackground.setPresetstoAmbientMode()
        }
        updateTimer(true)

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        livingBackground.scaleBackground(w, h)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        livingBackground.drawBackground(canvas, timers = null)
    }


    //Handle FPS
    private val mUpdateTimeHandler = EngineHandler(this)

    companion object{
        val INTERACTIVE_UPDATE_RATE_MS_15FPS = 67L //15fps
        val MSG_UPDATE_TIME = 0
    }


    /**
     * Starts/stops the [.mUpdateTimeHandler] timer based on the state of the watch face.
     */
    private fun updateTimer(forceRedraw:Boolean = false) {
        mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME)
        if (shouldTimerBeRunning() or forceRedraw) {
            mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME)
        }
    }

    /**
     * Returns whether the [.mUpdateTimeHandler] timer should be running. The timer
     * should only run in active mode.
     */
    private fun shouldTimerBeRunning(): Boolean {

        if(!livingBackground.needsRedraw){
//            setLayerType(LAYER_TYPE_NONE,null)
        }

        return livingBackground.needsRedraw
        //TODO CHANGE THIS BACK
        //TODO ADD A CALLBACK TO LET US KNOW WHEN ANIMATION IS FINISHED!
//            return isVisible && !mAmbient
    }

    /**
     * Handle updating the time periodically in interactive mode.
     */
    fun handleUpdateTimeMessage() {
        invalidate()
        if (shouldTimerBeRunning()) {
            val timeMs = System.currentTimeMillis()
            val delayMs =/*if(livingBackground.isAlarmEnabled()){
                    INTERACTIVE_UPDATE_RATE_MS_15FPS - timeMs % INTERACTIVE_UPDATE_RATE_MS_15FPS
                } else{ INTERACTIVE_UPDATE_RATE_MS - timeMs % INTERACTIVE_UPDATE_RATE_MS}*/
                    INTERACTIVE_UPDATE_RATE_MS_15FPS - timeMs % INTERACTIVE_UPDATE_RATE_MS_15FPS
            mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs)
        }
    }

    private class EngineHandler(reference: DelauneyBackground) : Handler() {
        private val mWeakReference: WeakReference<DelauneyBackground> = WeakReference(reference)

        override fun handleMessage(msg: Message) {
            val engine = mWeakReference.get()
            if (engine != null) {
                when (msg.what) {
                    MSG_UPDATE_TIME -> engine.handleUpdateTimeMessage()
                }
            }
        }
    }


}

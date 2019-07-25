package com.gorillamoa.routines.tools

import android.content.Context
import android.graphics.Canvas
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.TextView
import com.gorillamoa.routines.tools.animation.CIEColor
import com.gorillamoa.routines.tools.delayneytriangle.LivingBackground
import java.lang.ref.WeakReference

class DelauneyTextView:TextView{

    private lateinit var livingBackground: LivingBackground

    private lateinit var topLeft:CIEColor
    private lateinit var topRight:CIEColor
    private lateinit var bottomRight:CIEColor
    private lateinit var bottomLeft:CIEColor

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        setupAttributes(attrs)
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs){
        setupAttributes(attrs)
    }

    private fun setupAttributes(attrs: AttributeSet?) {

        Log.d("tag setupAttributes","Enters")

        // Obtain a typed array of attributes
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.DelauneyTextView, 0, 0)

        // Extract custom attributes into member variables
        topLeft =  getCIE(typedArray.getColor(R.styleable.DelauneyTextView_topLeft,0))
        topRight = getCIE(typedArray.getColor(R.styleable.DelauneyTextView_topRight,0))
        bottomRight = getCIE(typedArray.getColor(R.styleable.DelauneyTextView_bottomRight,0))
        bottomLeft = getCIE(typedArray.getColor(R.styleable.DelauneyTextView_bottomLeft,0))

        livingBackground = LivingBackground(
                LivingBackground.Graphics.Low,
                false,
                LivingBackground.DENSITY_BUTTON,
                LivingBackground.Shape.Specified,context.isWatch(),
                400.0,
                60.0,
                topLeft,
                topRight,
                bottomRight,
                bottomLeft
        )
        livingBackground.initializeBackground()

    }

    private fun getCIE(color:Int):CIEColor{
        val r = color shr 16 and 0xFF
        val g = color shr 8 and 0xFF
        val b = color shr 0 and 0xFF
        return CIEColor(r.toFloat(),g.toFloat(),b.toFloat(),255f)

    }

    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)

        Log.d("Background","onVisibilityChanged")

        if(visibility == View.VISIBLE){
            livingBackground.comeOutOfAmbient()
        }else{
            livingBackground.setPresetstoAmbientMode()
        }
        updateTimer()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        livingBackground.scaleBackground(w, h)
    }

    override fun onDraw(canvas: Canvas) {
        //we'll draw the background first..
        livingBackground.drawBackground(canvas, timers = null)
        //and then he text
        super.onDraw(canvas)
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
    private fun updateTimer() {
        mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME)
        if (shouldTimerBeRunning()) {
            mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME)
        }
    }

    /**
     * Returns whether the [.mUpdateTimeHandler] timer should be running. The timer
     * should only run in active mode.
     */
    private fun shouldTimerBeRunning(): Boolean {

        return livingBackground.needsRedraw
        //TODO include ambient
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

    private class EngineHandler(reference: DelauneyTextView) : Handler() {
        private val mWeakReference: WeakReference<DelauneyTextView> = WeakReference(reference)

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
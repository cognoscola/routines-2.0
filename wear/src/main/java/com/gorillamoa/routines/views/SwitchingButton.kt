package com.gorillamoa.routines.views

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.ContextCompat
import com.gorillamoa.routines.R
import android.graphics.Bitmap
import java.util.*

//CLEAN this code

class SwitchingButton(
        centerX:Int,
        centerY:Int,
        width:Int,
        height:Int,
        val context: Context):ClickableRectangle(centerX,centerY,width,height){


    private var paint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.STROKE
        strokeWidth = 3.0f
        isAntiAlias = true
    }
    private var srcRect =Rect()
    private var bitmap:Bitmap? = null
    private var states=ArrayDeque<Pair<String,Int>>()
    private var currentState:String =""

    override fun draw(canvas: Canvas){
        super.draw(canvas)
        bitmap?.let {

            canvas.drawBitmap(bitmap!!,srcRect,dstRect,paint)
        }
    }

    /**
     * Add a state to this button. When pressed, the button switches to a different
     * state/icon
     */
    fun addState(state:String, icon:Int):String {

        //check if we can find an existing
        states.find { it.first == state }?.let { states.remove(it) }
        states.add(Pair(state, icon))

        if (currentState.isBlank()) {
            currentState = states.first.first

            if (bitmap == null) {
                bitmap = getBitmap(context, icon)
                val scale = width.toFloat() / bitmap!!.width.toFloat()
                bitmap = Bitmap.createScaledBitmap(bitmap!!,
                        (bitmap!!.width * scale).toInt(),
                        (bitmap!!.height * scale).toInt(), true)
                srcRect.set(0, 0, bitmap!!.width, bitmap!!.height)
            }
        }

        return currentState
    }

    fun getState():String{
        return currentState
    }

    /**
     * sets the next state
     */
    fun nextState():String{

        if (states.size > 0) {

            //remove the first and make it the last
            val current = states.removeFirst()
            states.add(current)

            //now we peek at the first
            val nextPair = states.peekFirst()

            //is it the same one as now?
            if (nextPair.first != currentState) {
                currentState = nextPair.first

                try {

                    bitmap?.recycle()
                    bitmap = getBitmap(context, nextPair.second)
                    val scale = width.toFloat() / bitmap!!.width.toFloat()
                    bitmap = Bitmap.createScaledBitmap(bitmap!!,
                            (bitmap!!.width * scale).toInt(),
                            (bitmap!!.height * scale).toInt(), true)

                } catch (e: Exception) {

                    bitmap?.recycle()
                    bitmap = getBitmap(context, R.drawable.ic_warning_black_24dp)
                    val scale = width.toFloat() / bitmap!!.width.toFloat()
                    bitmap = Bitmap.createScaledBitmap(bitmap!!,
                            (bitmap!!.width * scale).toInt(),
                            (bitmap!!.height * scale).toInt(), true)

                } finally {
                    srcRect.set(0,0,bitmap!!.width,bitmap!!.height)
                }
            }
        }else{

            return "Unknown"
        }

        return currentState

    }

    private fun getBitmap(context:Context,drawableRes: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(context,drawableRes)
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(drawable!!.getIntrinsicWidth(), drawable!!.getIntrinsicHeight(), Bitmap.Config.ARGB_8888)
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable!!.getIntrinsicHeight())
        drawable.draw(canvas)

        return bitmap
    }


}
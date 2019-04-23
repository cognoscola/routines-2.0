package com.gorillamoa.routines.views

import android.content.Context
import android.graphics.*
import androidx.core.content.ContextCompat

class CanvasButton(
        cX:Int,
        cY:Int,
        width:Int,
        height:Int
):ClickableRectangle(cX,cY,width,height){

    private var bitmap: Bitmap? =null
    private var srcRect:Rect? = null

    private var paint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.STROKE
        strokeWidth = 3.0f
        isAntiAlias = true
    }


    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        bitmap?.let {
            canvas.drawBitmap(it,srcRect,dstRect, paint)
        }
    }


    /**
     * Specify the image to draw
     * @param context is the application context
     * @param resourceId is the id of the drawable to fetch
     * @param srcRect specifies which portion of the image to draw. If null it will draw the entire
     * image by default
     */
    fun setImage(context: Context, resourceId:Int, sourceRect:Rect? = null){

        //generate the image
        val drawable = ContextCompat.getDrawable(context,resourceId)
        val canvas = Canvas()

        bitmap = Bitmap.createBitmap(drawable!!.getIntrinsicWidth(), drawable!!.getIntrinsicHeight(), Bitmap.Config.ARGB_8888)
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable!!.getIntrinsicHeight())
        drawable.draw(canvas)


        //now scale it
        val scale = width.toFloat() / bitmap!!.width.toFloat()
        bitmap = Bitmap.createScaledBitmap(bitmap!!,
                (bitmap!!.width * scale).toInt(),
                (bitmap!!.height * scale).toInt(), true)

        srcRect = sourceRect?:Rect(0,0,bitmap!!.width, bitmap!!.height)

    }

}
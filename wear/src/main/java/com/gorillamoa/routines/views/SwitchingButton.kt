package com.gorillamoa.routines.views

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.ContextCompat
import com.gorillamoa.routines.R
import android.graphics.Bitmap


class SwitchingButton(
        center_x:Int,
        center_y:Int,
        width:Int,
        height:Int,
        context: Context,
        vararg resources:Int):ClickableRectangle(){

    private val tag:String = SwitchingButton::class.java.name

    private var paint = Paint().apply {
        color = Color.BLUE
        style = Paint.Style.STROKE
        strokeWidth = 3.0f
        isAntiAlias = true
    }
    private var srcRect =Rect()
    private var dstRect =Rect()
    private var bitmap:Bitmap

    private var drawable:Drawable? = null

    init{
        dstRect.set(
                (center_x - (width*0.5).toInt()),
                (center_y - (height*0.5).toInt()),
                (center_x + (width*0.5).toInt()),
                (center_y + (height*0.5).toInt())

//          30,10,60,40

        )
        Log.d("$tag ","center_x: $center_x, center_y:$center_y, width:$width, height:$height")

//<div>Icons made by <a href="https://www.freepik.com/" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" 			    title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" 			    title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>

       // bitmap = BitmapFactory.decodeResource(res, R.drawable.ic_break_time)
//        val drawable = ContextCompat.getDrawable(context,R.drawable.ic_break_time)
//        bitmap = Bitmap.createBitmap(drawable!!.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)



        bitmap = getBitmap(context, R.drawable.ic_break_time)
        val scale = width.toFloat() / bitmap.width.toFloat()
        bitmap = Bitmap.createScaledBitmap(bitmap,
                (bitmap.width * scale).toInt(),
                (bitmap.height * scale).toInt(), true)
        srcRect.set(0,0,bitmap.width,bitmap.height)
    }

    fun draw(canvas: Canvas){

//        canvas.drawRect(left.toFloat(),top.toFloat(), right.toFloat(),bottom.toFloat(),paint)
//        canvas.drawRect(dstRect,paint)

        canvas.drawBitmap(bitmap,srcRect,dstRect,paint)
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

    fun isTouched(x:Int,y:Int):Boolean{

        if (dstRect.contains(x, y)) {
            onClickListener?.invoke()
            return true
        }else{
            return false
        }
    }

}
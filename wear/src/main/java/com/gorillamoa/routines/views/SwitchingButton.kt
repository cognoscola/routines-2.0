package com.gorillamoa.routines.views

import android.content.res.Resources
import android.graphics.*
import android.util.Log
import com.gorillamoa.routines.R


class SwitchingButton(
        center_x:Int,
        center_y:Int,
        width:Int,
        height:Int,

        res:Resources,
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

    init{
        dstRect.set(
                (center_x - (width*0.5).toInt()),
                (center_y - (height*0.5).toInt()),
                (center_x + (width*0.5).toInt()),
                (center_y + (height*0.5).toInt())

//          30,10,60,40


        )
        Log.d("$tag ","center_x: $center_x, center_y:$center_y, width:$width, height:$height")


        bitmap = BitmapFactory.decodeResource(res, R.drawable.bg)
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

    fun isTouched(x:Int,y:Int):Boolean{

        if (dstRect.contains(x, y)) {
            onClickListener?.invoke()
            return true
        }else{
            return false
        }
    }




}
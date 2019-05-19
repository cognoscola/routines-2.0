package com.gorillamoa.routines.views

import android.graphics.*
import com.gorillamoa.routines.utils.CircularTimer

const val OVERLAP_AMOUNT = 0.01f
const val ZERO = 0f
const val SECOND_HORIZONTAL_DISPLACEMENT = 6.0f
const val SECOND_VERTICAL_DISPLACEMENT = 10.0f
const val MINUTE_HORIZONTAL_DISPLACEMENT = 12.0f
const val MINUTE_VERTICAL_DISPLACEMENT = 10.0f
const val HOUR_HORIZONTAL_DISPLACEMENT = 25.0f

const val DEGREE_COLOR_SEPERATION =20

const val RADIUS_DISPLACEMENT_COLOR = 20f

const val LOWER_DEGREE_LIMIT = 0
const val UPPER_DEGREE_LIMIT = 360

class TimeHand(val type:Int){

    var radius:Float = 0f

    companion object {
        const val TYPE_SECOND = 0
        const val TYPE_MINUTE = 1
        const val TYPE_HOUR = 2
    }

    private var breakLinePaint= Paint().apply {

        isAntiAlias = true
        style = Paint.Style.FILL
        color = Color.RED
    }

    private var bgPaint= Paint().apply {

        isAntiAlias = true
        style = Paint.Style.FILL
        color = Color.RED
    }


    //Break hand color
    private var breakLinePath:Path? = null
    private var hour2Path:Path? = null
    private var bgPath:Path? = null

    fun measure(width:Int,height:Int,radius:Float){

        val mCenterX = width/2.0f
        val mCenterY = height/2.0f
        this.radius = radius

            when (type) {

                TYPE_SECOND -> {
                    breakLinePath = Path().apply {

                        reset()
                        moveTo(mCenterX, mCenterY - radius - SECOND_VERTICAL_DISPLACEMENT)
                        lineTo(mCenterX - SECOND_HORIZONTAL_DISPLACEMENT, ZERO)
                        lineTo(mCenterX + SECOND_HORIZONTAL_DISPLACEMENT, ZERO)
                        lineTo(mCenterX, mCenterY - radius - SECOND_VERTICAL_DISPLACEMENT)
                    }
                    breakLinePaint.setShadowLayer(3.0f, 0.0f, 0.0f, Color.BLACK)
                }
                TYPE_MINUTE -> {

                    breakLinePath = Path().apply {
                        reset()
                        moveTo(mCenterX, mCenterY - radius +15f)
                        lineTo(mCenterX - MINUTE_HORIZONTAL_DISPLACEMENT, 0f)
                        lineTo(mCenterX + MINUTE_HORIZONTAL_DISPLACEMENT, 0f)
                        lineTo(mCenterX, mCenterY - radius + 15f)
                    }
                    breakLinePaint.setShadowLayer(6.0f, 0.0f, 0.0f, Color.BLACK)
                }
                TYPE_HOUR ->{

                    bgPath = Path().apply {

                        reset()
                        moveTo(mCenterX, mCenterY - radius)
                        lineTo(mCenterX - HOUR_HORIZONTAL_DISPLACEMENT, 0f)
                        lineTo(mCenterX + HOUR_HORIZONTAL_DISPLACEMENT, 0f)
                        lineTo(mCenterX, mCenterY - radius)
                    }

                    breakLinePath = Path().apply {
                        reset()
                        moveTo(mCenterX, mCenterY - radius)
                        lineTo(mCenterX - HOUR_HORIZONTAL_DISPLACEMENT, 0f)
                        lineTo(mCenterX, 0f)
                        lineTo(mCenterX, mCenterY - radius)
                    }

                    hour2Path = Path().apply {
                        reset()
                        moveTo(mCenterX, mCenterY - radius)
                        lineTo(mCenterX ,0f)
                        lineTo(mCenterX + HOUR_HORIZONTAL_DISPLACEMENT, 0f)
                        lineTo(mCenterX, mCenterY - radius)
                    }

                    breakLinePaint.clearShadowLayer()
                    bgPaint.setShadowLayer(10.0f,0.0f,0.0f,Color.BLACK)
                }
            }

    }

    fun draw(canvas: Canvas, cX:Float, cY:Float, bounds: Rect, bg:LivingBackground, angleDegrees:Float){

        when(type){
            TYPE_SECOND->{
                breakLinePaint.color =bg.getColor(
                        CircularTimer.getXFromDegree(angleDegrees, radius + RADIUS_DISPLACEMENT_COLOR, cX),
                        CircularTimer.getYFromDegree(angleDegrees, radius + RADIUS_DISPLACEMENT_COLOR, cY),
                        bounds.width().toFloat(),
                        bounds.height().toFloat())

                canvas.drawPath(breakLinePath!!,breakLinePaint)
            }
            TYPE_MINUTE  ->{
                breakLinePaint.color =bg.getColor(
                        CircularTimer.getXFromDegree(angleDegrees, radius + RADIUS_DISPLACEMENT_COLOR, cX),
                        CircularTimer.getYFromDegree(angleDegrees, radius + RADIUS_DISPLACEMENT_COLOR, cY),
                        bounds.width().toFloat(),
                        bounds.height().toFloat())

                canvas.drawPath(breakLinePath!!,breakLinePaint)
            }
            TYPE_HOUR->{

                bgPaint.color =bg.getColor(
                        CircularTimer.getXFromDegree(angleDegrees, radius + RADIUS_DISPLACEMENT_COLOR, cX),
                        CircularTimer.getYFromDegree(angleDegrees, radius + RADIUS_DISPLACEMENT_COLOR, cY),
                        bounds.width().toFloat(),
                        bounds.height().toFloat())

                canvas.drawPath(bgPath!!,bgPaint)

                breakLinePaint.color = bg.getColor(
                        CircularTimer.getXFromDegree(angleDegrees + DEGREE_COLOR_SEPERATION, radius, cX),
                        CircularTimer.getYFromDegree(angleDegrees + DEGREE_COLOR_SEPERATION, radius, cY),
                        bounds.width().toFloat(),
                        bounds.height().toFloat()
                )
                canvas.drawPath(breakLinePath!!,breakLinePaint)

                breakLinePaint.color = bg.getColor(
                        CircularTimer.getXFromDegree(angleDegrees - DEGREE_COLOR_SEPERATION, radius, cX),
                        CircularTimer.getYFromDegree(angleDegrees - DEGREE_COLOR_SEPERATION, radius, cY),
                        bounds.width().toFloat(),
                        bounds.height().toFloat()
                )
                canvas.drawPath(hour2Path!!,breakLinePaint)
            }
        }
    }
}
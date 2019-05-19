package com.gorillamoa.routines.views

import android.graphics.*
import com.gorillamoa.routines.utils.CircularTimer

class FloatingCrystal(val type:Int){
    private var radius:Float = 0.0f
    private var path1: Path? = null
    private var path2: Path? = null
    private var shadowPath: Path? = null

    private val shadowPainter = Paint().apply {
        strokeWidth = 0f
        isAntiAlias = true
        setShadowLayer(3.0f, 0f, 0f, Color.WHITE)
    }
    private  val painter = Paint().apply {
        strokeWidth = 0.0f
        isAntiAlias = true
        strokeCap = Paint.Cap.SQUARE
    }

    fun measure(width:Int, height:Int, radius:Float){

        //we'll assume that
        val mCenterX = width/2.0f
        val mCenterY = height/2.0f
        this.radius = radius

        when (type) {
            TimeHand.TYPE_SECOND -> {

                painter.setShadowLayer(3.0f,0f,0f, Color.WHITE)

                path1 = Path().apply {
                    reset()

                    moveTo(mCenterX, mCenterY - radius + SECOND_VERTICAL_DISPLACEMENT )
                    lineTo(mCenterX - SECOND_HORIZONTAL_DISPLACEMENT, mCenterY -radius )
                    lineTo(mCenterX + SECOND_HORIZONTAL_DISPLACEMENT, mCenterY - radius)
                    lineTo(mCenterX, mCenterY - radius + SECOND_VERTICAL_DISPLACEMENT)

                    moveTo(mCenterX -SECOND_HORIZONTAL_DISPLACEMENT, mCenterY - radius )
                    lineTo(mCenterX , mCenterY- radius - SECOND_VERTICAL_DISPLACEMENT )
                    lineTo(mCenterX +SECOND_HORIZONTAL_DISPLACEMENT, mCenterY- radius )
                    lineTo(mCenterX -SECOND_HORIZONTAL_DISPLACEMENT, mCenterY- radius )
                }

            }
            TimeHand.TYPE_MINUTE ->{
                shadowPath = Path().apply {
                    fillType = Path.FillType.EVEN_ODD
                    moveTo(mCenterX, mCenterY - radius + MINUTE_VERTICAL_DISPLACEMENT )
                    lineTo(mCenterX - MINUTE_HORIZONTAL_DISPLACEMENT, mCenterY -radius )
                    lineTo(mCenterX , mCenterY- radius - MINUTE_VERTICAL_DISPLACEMENT )
                    lineTo(mCenterX + MINUTE_HORIZONTAL_DISPLACEMENT, mCenterY - radius)
                    lineTo(mCenterX, mCenterY - radius + MINUTE_VERTICAL_DISPLACEMENT)
                }

                path1 = Path().apply {
                    fillType = Path.FillType.EVEN_ODD
                    reset()
                    moveTo(mCenterX, mCenterY - radius + MINUTE_VERTICAL_DISPLACEMENT )
                    lineTo(mCenterX , mCenterY- radius - MINUTE_VERTICAL_DISPLACEMENT )
                    lineTo(mCenterX + MINUTE_HORIZONTAL_DISPLACEMENT, mCenterY - radius)
                    lineTo(mCenterX, mCenterY - radius + MINUTE_VERTICAL_DISPLACEMENT)
                }
                path2 = Path().apply {
                    fillType = Path.FillType.EVEN_ODD
                    reset()
                    moveTo(mCenterX+ OVERLAP_AMOUNT, mCenterY - radius + MINUTE_VERTICAL_DISPLACEMENT)
                    lineTo(mCenterX -MINUTE_HORIZONTAL_DISPLACEMENT, mCenterY- radius )
                    lineTo(mCenterX+OVERLAP_AMOUNT , mCenterY- radius - MINUTE_VERTICAL_DISPLACEMENT)
                    lineTo(mCenterX+OVERLAP_AMOUNT , mCenterY- radius + MINUTE_VERTICAL_DISPLACEMENT )

                }
            }
        }
    }

    /**
     * Draw the timer at the specified tick (minute or second)
     * @param canvas is the canvas on which to draw upon
     * @param circleCenterX is the circleCenterX coordinate of the center of the circle
     * @param circleCenterY is the circleCenterY coordinate of the center of the circle
     * @param bg is the background to draw on top of
     * @param angleDegrees specifies the angle at which to draw the hand
     */
    fun draw(canvas: Canvas, circleCenterX:Float, circleCenterY:Float, bounds: Rect, bg:LivingBackground, angleDegrees:Float){

        if ((angleDegrees < LOWER_DEGREE_LIMIT) or (angleDegrees > UPPER_DEGREE_LIMIT)) {
            throw Exception("tick must be 0 <= tick <= 60")
        }
        //use the background to extract color

        when (type) {
            TimeHand.TYPE_SECOND -> {
                painter.color =bg.getColor(
                        CircularTimer.getXFromDegree(angleDegrees, radius, circleCenterX),
                        CircularTimer.getYFromDegree(angleDegrees, radius, circleCenterY),
                        bounds.width().toFloat(),
                        bounds.height().toFloat())
                canvas.drawPath(path1!!,painter)
            }
            TimeHand.TYPE_MINUTE -> {
                shadowPainter.color =bg.getColor(
                        CircularTimer.getXFromDegree(angleDegrees, radius, circleCenterX),
                        CircularTimer.getYFromDegree(angleDegrees, radius, circleCenterY),
                        bounds.width().toFloat(),
                        bounds.height().toFloat())
                canvas.drawPath(shadowPath!!,shadowPainter)

                painter.color = bg.getColor(
                        CircularTimer.getXFromDegree(angleDegrees - DEGREE_COLOR_SEPERATION, radius, circleCenterX),
                        CircularTimer.getYFromDegree(angleDegrees - DEGREE_COLOR_SEPERATION, radius, circleCenterY),
                        bounds.width().toFloat(),
                        bounds.height().toFloat()
                )
                canvas.drawPath(path1!!,painter)
                painter.color = bg.getColor(
                        CircularTimer.getXFromDegree(angleDegrees + DEGREE_COLOR_SEPERATION, radius, circleCenterX),
                        CircularTimer.getYFromDegree(angleDegrees + DEGREE_COLOR_SEPERATION, radius, circleCenterY),
                        bounds.width().toFloat(),
                        bounds.height().toFloat()
                )
                canvas.drawPath(path2!!,painter)
            }
        }
    }
}
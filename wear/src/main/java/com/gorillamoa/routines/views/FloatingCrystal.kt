package com.gorillamoa.routines.views

import android.graphics.*
import com.gorillamoa.routines.utils.CircularTimer

const val HORIZONTAL_DISPLACEMENT = 30.0f
const val VERTICAL_DISPLACEMENT = 30f
const val COLOR_DISPLACEMENT = 5.0f
private const val DEGREE_COLOR_SEPERATION =10

class FloatingCrystal {

    companion object {
        private var path1: Path? = null
        private var path2: Path? = null
        private var path3: Path? = null
        private var path4: Path? = null
        private var radius:Float = 0f
        private var width: Int = 0
        private var height:Int = 0

        fun measureDiamond(width: Int, height: Int, radius: Float){
            val mCenterX = width / 2.0f
            val mCenterY = height / 2.0f
            this.width = width
            this.height = height
            this.radius = radius

            path1 = Path().apply {
                fillType = Path.FillType.EVEN_ODD
                reset()
                moveTo(mCenterX, mCenterY - radius + VERTICAL_DISPLACEMENT)
                lineTo(mCenterX, mCenterY - radius - VERTICAL_DISPLACEMENT)
                lineTo(mCenterX + HORIZONTAL_DISPLACEMENT, mCenterY - radius)
                lineTo(mCenterX, mCenterY - radius + VERTICAL_DISPLACEMENT)
            }
            path2 = Path().apply {
                fillType = Path.FillType.EVEN_ODD
                reset()
                moveTo(mCenterX , mCenterY - radius + VERTICAL_DISPLACEMENT)
                lineTo(mCenterX - HORIZONTAL_DISPLACEMENT, mCenterY - radius)
                lineTo(mCenterX, mCenterY - radius - VERTICAL_DISPLACEMENT)
                lineTo(mCenterX, mCenterY - radius + VERTICAL_DISPLACEMENT)

            }
        }
    }


    private val painter1 = Paint().apply {
        strokeWidth = 0.0f
        isAntiAlias = true
        strokeCap = Paint.Cap.SQUARE
    }

    private val painter2 = Paint().apply {
        strokeWidth = 0.0f
        isAntiAlias = true
        strokeCap = Paint.Cap.SQUARE
    }
    private val painter3 = Paint().apply {
        strokeWidth = 0.0f
        isAntiAlias = true
        strokeCap = Paint.Cap.SQUARE
    }
    private val painter4 = Paint().apply {
        strokeWidth = 0.0f
        isAntiAlias = true
        strokeCap = Paint.Cap.SQUARE
    }


    /**
     * Draw the timer at the specified tick (minute or second)
     * @param canvas is the canvas on which to draw upon
     * @param circleCenterX is the circleCenterX coordinate of the center of the circle
     * @param circleCenterY is the circleCenterY coordinate of the center of the circle
     * @param bg is the background to draw on top of
     * @param angleDegrees specifies the angle at which to draw the hand
     */
    fun draw(canvas: Canvas,angleDegrees: Float,bg:LivingBackground) {

        painter1.color = bg.getColor(
                CircularTimer.getXFromDegree(angleDegrees + DEGREE_COLOR_SEPERATION, radius + COLOR_DISPLACEMENT, width/2.0f),
                CircularTimer.getYFromDegree(angleDegrees + DEGREE_COLOR_SEPERATION, radius+ COLOR_DISPLACEMENT, height/2.0f),
                width.toFloat(),
                height.toFloat()
        )

        painter2.color = bg.getColor(
                CircularTimer.getXFromDegree(angleDegrees - DEGREE_COLOR_SEPERATION, radius + COLOR_DISPLACEMENT, width/2.0f),
                CircularTimer.getYFromDegree(angleDegrees - DEGREE_COLOR_SEPERATION, radius + COLOR_DISPLACEMENT, height/2.0f),
                width.toFloat(),
                height.toFloat()
        )

        canvas.drawPath(path1!!, painter1)
        canvas.drawPath(path2!!, painter2)
    }
}
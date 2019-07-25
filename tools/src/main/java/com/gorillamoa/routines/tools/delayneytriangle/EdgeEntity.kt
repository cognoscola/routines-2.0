package com.gorillamoa.routines.tools.delayneytriangle

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.badlogic.ashley.core.Entity
import com.gorillamoa.routines.tools.animation.AlphaComponent
import com.gorillamoa.routines.tools.animation.ZERO_INT
import io.github.jdiemke.triangulation.Edge2D

class EdgeEntity(var itself: Edge2D): Entity() {

    var parent: TriangleEntity? = null
    var neighbour: TriangleEntity? = null
    var animationLatch = false

    private var needsRedraw = false
    fun setNeedsRedraw(draw: Boolean) {
        needsRedraw = draw
    }

    fun needsRedraw(): Boolean {
        return needsRedraw
    }

    companion object {

        val paint = Paint().apply {
            strokeWidth = 1.0f
            color = Color.WHITE
            isAntiAlias = true
        }

        val renderFunction: (Canvas, EdgeEntity) -> Any = { canvas, entity ->
            entity.getComponent(AlphaComponent::class.java).apply {
                if (alpha > ZERO_INT) {
                    val edge = entity.itself
                    paint.alpha = alpha
                    canvas.drawLine(
                            edge.a.x.toFloat(),
                            edge.a.y.toFloat(),
                            edge.b.x.toFloat(),
                            edge.b.y.toFloat(), paint)
                }
            }
        }

    }

    fun resetAnimationLatch() {
        animationLatch = false
    }

}
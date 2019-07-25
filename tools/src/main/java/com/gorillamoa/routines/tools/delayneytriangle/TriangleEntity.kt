package com.gorillamoa.routines.tools.delayneytriangle

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.gorillamoa.routines.tools.animation.*
import io.github.jdiemke.triangulation.Triangle2D

class TriangleEntity(val itself: Triangle2D): Entity() {

    var edgeEntityAB: EdgeEntity? = null
    var edgeEntityAC: EdgeEntity? = null
    var edgeEntityBC: EdgeEntity? = null
    var animationLatch = false

    companion object {

        val paint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        val path = Path()
        val renderFunction: (Canvas, TriangleEntity) -> Any = { canvas, entity ->

            path.reset()
            path.moveTo(entity.itself.qA().x.toFloat(), entity.itself.qA().y.toFloat())
            path.lineTo(entity.itself.qB().x.toFloat(), entity.itself.qB().y.toFloat())
            path.lineTo(entity.itself.qC().x.toFloat(), entity.itself.qC().y.toFloat())
            path.lineTo(entity.itself.qA().x.toFloat(), entity.itself.qA().y.toFloat())

            paint.color = entity.getComponent(ColorComponent::class.java).color

            canvas.drawPath(path, paint)
        }

        //clean move these functions else where
        fun getTriangleToLightUpGiven(edgeEntity: EdgeEntity): TriangleEntity? {
            //check the edge's neighbours
            if (shouldLightUp(edgeEntity.neighbour)) {
                return edgeEntity.neighbour
            }

            if (shouldLightUp(edgeEntity.parent)) {
                return edgeEntity.parent
            }

            return null
        }

        fun isEdgeNodeLit(edgeEntity: EdgeEntity?): Boolean {
            return edgeEntity?.animationLatch ?: true
        }

        fun shouldLightUp(triangleNode: TriangleEntity?): Boolean {

            return triangleNode?.let {
                if (!it.animationLatch) {
                    if (isEdgeNodeLit(it.edgeEntityAB) and isEdgeNodeLit(it.edgeEntityAC) and isEdgeNodeLit(it.edgeEntityBC)) {
                        it.animationLatch = true
                    }
                }
                it.animationLatch
            } ?: false
        }
    }

    fun setTriangleColor(color: Int, engine: Engine) {

        val component = getComponent(ColorComponent::class.java)
                ?: engine.createComponent(ColorComponent::class.java).apply {
                    add(this@apply)
                }
        component.color = color
    }

    fun getCenterX(): Float {
        return (itself.qA().x + itself.qB().x + itself.qC().x).div(THREE_FLOAT).toFloat()
    }

    fun getCenterY(): Float {
        return (itself.qA().y + itself.qB().y + itself.qC().y).div(THREE_FLOAT).toFloat()
    }

    fun fadeColorFromAmbient(engine: Engine, startColor: CIEColor, final: CIEColor) {


    }

    fun resetAnimationLatch() {
        animationLatch = false
    }

    private var redraw = false

    fun setNeedsRedraw(draw: Boolean) {
        redraw = draw
    }

    fun needsRedraw(): Boolean {
        return redraw
    }
}

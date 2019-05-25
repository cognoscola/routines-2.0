package com.gorillamoa.routines.animation

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem

class RenderSystem:IteratingSystem(Family.all(EdgeComponent::class.java).get()){


    var canvas: Canvas? = null
    val painter =  Paint().apply {

        strokeWidth = 1.0f
        color = Color.WHITE
        isAntiAlias = true
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {

        val edge = entity.getComponent(EdgeComponent::class.java)
        entity.getComponent(AlphaComponent::class.java).apply {
            if (alpha > 0) {

                painter.alpha = alpha
                canvas?.drawLine(edge.x1, edge.y1, edge.x2, edge.y2, painter)
            }
        }
    }

}
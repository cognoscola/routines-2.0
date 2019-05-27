package com.gorillamoa.routines.animation

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.gorillamoa.routines.views.LivingBackground

class RenderSystem:IteratingSystem(Family.all(EdgeComponent::class.java).get()){

    @Suppress("unused")
    private val tag:String = RenderSystem::class.java.name
    var canvas: Canvas? = null
    val painter =  Paint().apply {

        strokeWidth = 1.0f
        color = Color.WHITE
        isAntiAlias = true
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {

        //TODO move edge to its own entity with its own render function

        canvas?.let {

            if (entity is LivingBackground.TriangleEntity) {
                
                LivingBackground.TriangleEntity.renderFunction.invoke(canvas!!,entity)

            }else{
                val edgeComponent = entity.getComponent(EdgeComponent::class.java)
                entity.getComponent(AlphaComponent::class.java).apply {
                    if (alpha > 0) {

                        val edge = edgeComponent.edgeNode.itself
                        painter.alpha = alpha
                        canvas?.drawLine(
                                edge.a.x.toFloat(),
                                edge.a.y.toFloat(),
                                edge.b.x.toFloat(),
                                edge.b.y.toFloat(), painter)
                    }
                }

            }
        }



    }

}
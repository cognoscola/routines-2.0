package com.gorillamoa.routines.animation

import android.graphics.Canvas
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.gorillamoa.routines.views.LivingBackground

class RenderSystem:IteratingSystem(Family.all(RenderComponent::class.java).get()){

    @Suppress("unused")
    private val tag:String = RenderSystem::class.java.name
    var canvas: Canvas? = null

    override fun processEntity(entity: Entity, deltaTime: Float) {

        canvas?.let {

            if (entity is LivingBackground.EdgeEntity) {
                LivingBackground.EdgeEntity.renderFunction.invoke(canvas!!,entity)
            }

            if (entity is LivingBackground.TriangleEntity) {
                LivingBackground.TriangleEntity.renderFunction.invoke(canvas!!,entity)
            }
        }
    }

}
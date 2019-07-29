package com.gorillamoa.routines.tools.animation

import android.graphics.Canvas
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.gorillamoa.routines.tools.delayneytriangle.EdgeEntity
import com.gorillamoa.routines.tools.delayneytriangle.TriangleEntity

class RenderSystem(val canvas: Canvas):IteratingSystem(Family.all(RenderComponent::class.java).get()){

    @Suppress("unused")
    private val tag:String = RenderSystem::class.java.name


    override fun processEntity(entity: Entity, deltaTime: Float) {

        canvas.let {

            if (entity is EdgeEntity) {
                EdgeEntity.renderFunction.invoke(canvas,entity)
            }

            if (entity is TriangleEntity) {
                TriangleEntity.renderFunction.invoke(canvas,entity)
            }
        }
    }
}
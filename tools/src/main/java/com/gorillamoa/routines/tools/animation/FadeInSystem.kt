package com.gorillamoa.routines.tools.animation

import android.util.Log
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.gorillamoa.routines.tools.delayneytriangle.EdgeEntity


/**
 * We're going to fade the lines from left to right
 */
class FadeInSystem:IteratingSystem(Family.all(AlphaComponent::class.java,FadeInEffectComponent::class.java).get()) {

    @Suppress("unused")
    private val tag:String = FadeInSystem::class.java.name

    companion object {
        fun startFadingIn(delaySecond:Double, rate:Int, entity: Entity,engine:Engine) {
            engine.createComponent(FadeInEffectComponent::class.java).apply {

                startDelaySecond = delaySecond
                fadeRatePerFrame = rate
                entity.add(this@apply)
            }
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {

        val fadeProperties = entity.getComponent(FadeInEffectComponent::class.java)
        fadeProperties?.let {
            entity.getComponent(AlphaComponent::class.java).apply {

                //theres a delay happening, so just countdown
                if (fadeProperties.startDelaySecond > ZERO_FLOAT) {
                    fadeProperties.startDelaySecond -= deltaTime
                }else{
                    //now start the fade in process
                    if (alpha < TWOFIFTYFIVE) {
                        alpha += fadeProperties.fadeRatePerFrame
                    }
                    alpha = Math.min(alpha, TWOFIFTYFIVE)



                    if (alpha == TWOFIFTYFIVE) {
                        entity.remove(FadeInEffectComponent::class.java)
                    }

                }
            }
        }

        if (entity is EdgeEntity) {
            Log.d("$tag processEntity","set Redraw to true!")
            entity.setNeedsRedraw(true)
        }
    }
}


package com.gorillamoa.routines.animation

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.gorillamoa.routines.utils.ZERO_FLOAT
import com.gorillamoa.routines.utils.ZERO_INT

class FadeOutSystem:IteratingSystem(Family.all(AlphaComponent::class.java, FadeOutEffectComponent::class.java).get()){

    companion object {
        fun startFadingOut(delaySecond:Double, rate:Int, entity: Entity,engine: Engine) {
            engine.createComponent(FadeOutEffectComponent::class.java).apply {

                startDelaySecond = delaySecond
                fadeRatePerFrame = rate
                entity.add(this@apply)
            }
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {

        val fadeProperties = entity.getComponent(FadeOutEffectComponent::class.java)
        entity.getComponent(AlphaComponent::class.java)?.apply {

            if (fadeProperties.startDelaySecond > ZERO_FLOAT) {
                fadeProperties.startDelaySecond -= deltaTime
            }else{

                if (alpha > ZERO_INT) {
                    alpha -= fadeProperties.fadeRatePerFrame
                }
                alpha = Math.max(alpha, ZERO_INT)

                if (alpha == ZERO_INT) {
                    entity.remove(FadeOutEffectComponent::class.java)
                    entity.remove(RenderComponent::class.java)
                }
            }
        }
    }


}
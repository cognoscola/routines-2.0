package com.gorillamoa.routines.animation

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.gorillamoa.routines.utils.ZERO_FLOAT
import com.gorillamoa.routines.utils.ZERO_INT
import kotlin.math.roundToInt

class FadeOutSystem:IteratingSystem(Family.all(AlphaV2Component::class.java).get()){

    override fun processEntity(entity: Entity, deltaTime: Float) {

        entity.getComponent(AlphaV2Component::class.java)?.apply {

            if (delaySecond > ZERO_FLOAT) {
                delaySecond -= deltaTime
            }else{

                if (alpha > ZERO_INT) {
                    alpha -= fadeRatePerFrame
                }
                alpha = Math.max(alpha, ZERO_INT)

                if (alpha == ZERO_INT) {
                    entity.remove(AlphaV2Component::class.java)
                    entity.remove(RenderComponent::class.java)
                }
            }
        }
    }
}
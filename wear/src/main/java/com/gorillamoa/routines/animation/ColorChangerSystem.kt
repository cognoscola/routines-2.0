package com.gorillamoa.routines.animation

import android.graphics.Color
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.gorillamoa.routines.utils.*
import kotlin.math.roundToInt

class ColorChangerSystem: IteratingSystem(Family.all(ColorComponent::class.java, ColorChangerEffectComponent::class.java).get()){

    companion object {
        fun startChanging(initialColor:CIEColor , targetColor:CIEColor, rate:Double, entity: Entity, engine: Engine) {
            engine.createComponent(ColorChangerEffectComponent::class.java).apply {

                this@apply.initialColor.r = initialColor.r
                this@apply.initialColor.g = initialColor.g
                this@apply.initialColor.b = initialColor.b
                this@apply.initialColor.a = initialColor.a

                this@apply.targetColor.r = targetColor.r
                this@apply.targetColor.g = targetColor.g
                this@apply.targetColor.b = targetColor.b
                this@apply.targetColor.a = targetColor.a
                timeToChangeSecond = rate
                timeElapsed = ZERO_D
                entity.add(this@apply)
            }
        }
        private var percent =ZERO_FLOAT
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {

        val changeProperties = entity.getComponent(ColorChangerEffectComponent::class.java)
        entity.getComponent(ColorComponent::class.java)?.apply {

            if (changeProperties.timeElapsed < changeProperties.timeToChangeSecond) {
                changeProperties.timeElapsed += deltaTime

                //calculate the new color at this new time.
                percent = (changeProperties.timeElapsed / changeProperties.timeToChangeSecond).toFloat()
                percent =  com.gorillamoa.routines.tools.animation.ONE_FLOAT - Math.min( percent, com.gorillamoa.routines.tools.animation.ONE_FLOAT)
                changeProperties.targetColor.lerp(changeProperties.initialColor, percent,newColor)

                color = Color.argb(
                        newColor.a.roundToInt(),
                        newColor.r.roundToInt(),
                        newColor.g.roundToInt(),
                        newColor.b.roundToInt()
                )

            }else{

                //we're done, now remove from this family
                color = Color.argb(
                        changeProperties.targetColor.a.roundToInt(),
                        changeProperties.targetColor.r.roundToInt(),
                        changeProperties.targetColor.g.roundToInt(),
                        changeProperties.targetColor.b.roundToInt())
                entity.remove(ColorChangerEffectComponent::class.java)
            }
        }
    }
}
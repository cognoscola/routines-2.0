package com.gorillamoa.routines.animation

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import com.gorillamoa.routines.utils.CIEColor

class ColorChangerEffectComponent:Component,Pool.Poolable{

    var targetColor = CIEColor(0f,0f,0f,0f)
    var initialColor= CIEColor(0f,0f,0f,0f)
    var timeToChangeSecond = 0.369
    var timeElapsed = 0.0

    override fun reset() {
        targetColor.apply {
            r = 0.0f
            g = 0.0f
            b = 0.0f
            a = 0.0f
        }

        initialColor.apply {
            r = 0.0f
            g = 0.0f
            b = 0.0f
            a = 0.0f
        }
        timeToChangeSecond = 0.0
    }
}
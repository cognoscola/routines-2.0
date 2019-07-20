package com.gorillamoa.routines.tools.animation

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class FadeInEffectComponent:Component,Pool.Poolable{

    var startDelaySecond = 0.0
    var fadeRatePerFrame = 0

    override fun reset() {
        startDelaySecond = 0.0
        fadeRatePerFrame = 0
    }
}
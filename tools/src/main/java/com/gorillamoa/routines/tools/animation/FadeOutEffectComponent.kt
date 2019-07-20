package com.gorillamoa.routines.tools.animation

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class FadeOutEffectComponent:Component,Pool.Poolable{
    var startDelaySecond = 0.0
    var fadeRatePerFrame = 0

    override fun reset() {
        var startDelaySecond = 0.0
        var fadeRatePerFrame = 0
    }
}
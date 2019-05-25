package com.gorillamoa.routines.animation

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class EdgeComponent(
        var x1:Float,
        var y1:Float,
        var x2:Float,
        var y2:Float
):Component,Pool.Poolable{

    override fun reset() {
        x1 = 0f
        y1 = 0f
        x2 = 0f
        y2 = 0f
    }
}
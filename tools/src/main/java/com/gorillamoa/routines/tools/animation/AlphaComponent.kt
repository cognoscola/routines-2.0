package com.gorillamoa.routines.tools.animation

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool

class AlphaComponent:Component,Pool.Poolable{

    var alpha = 0 //alpha between 0 and 255

    override fun reset() {
        alpha = 0
    }

}
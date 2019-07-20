package com.gorillamoa.routines.tools.animation

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool


class ColorComponent:Component,Pool.Poolable{

    var color:Int = 0
    var newColor:CIEColor = CIEColor(0.0f,0.0f,0.0f,0.0f)

    override fun reset() {
        color = 0
    }
}
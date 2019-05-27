package com.gorillamoa.routines.animation

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.utils.Pool
import com.gorillamoa.routines.views.LivingBackground

class EdgeComponent(
       var edgeNode:LivingBackground.EdgeNode
):Component,Pool.Poolable{

    override fun reset() {

    }
}
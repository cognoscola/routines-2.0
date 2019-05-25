package com.gorillamoa.routines.animation

import android.graphics.Canvas
import com.badlogic.ashley.core.Component

class DrawableComponent:Component{

    var render:((canvas: Canvas)->Any?)? = null

}
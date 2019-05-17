package com.gorillamoa.routines.utils

import android.graphics.Color
import android.graphics.ColorSpace

/** Linearly interpolates between this color and the target color by t which is in the range [0,1]. The result is stored in
 * this color.
 * @param target The target color
 * @param t The interpolation coefficient
 * @return This color for chaining.
 */


fun Color.lerp(target: Color, t: Float,space:ColorSpace): Color {

//    Log.d("lerp","Pos:$pos, R:${red()}, G:${green()}, B:${blue()}, A:${alpha()}")

    var nR:Float = red() + t* (target.red() - red())
    var nG = green() +  t * (target.green() - green())
    var nB = blue() +  t * (target.blue() - blue())
    var nA = alpha() +  t * (target.alpha() - alpha())

    if(nR < ZERO_INT ) nR = ZERO_FLOAT else if(nR > TWOFIFTYFIVE_FLOAT) {nR = TWOFIFTYFIVE_FLOAT}
    if(nG < ZERO_INT ) nG = ZERO_FLOAT else if(nG > TWOFIFTYFIVE_FLOAT) {nG = TWOFIFTYFIVE_FLOAT}
    if(nB < ZERO_INT ) nB = ZERO_FLOAT else if(nB > TWOFIFTYFIVE_FLOAT) {nB = TWOFIFTYFIVE_FLOAT}
    if(nA < ZERO_INT ) nA = ZERO_FLOAT else if(nA > TWOFIFTYFIVE_FLOAT) {nA = TWOFIFTYFIVE_FLOAT}

//    Log.d("lerp","Pos:$pos, R:${nR}, G:${nG}, B:$nB, A:$nA")
//TODO Need a better way to LERP without allocation new colors!
    return Color.valueOf(nR,nG,nB,nA,space)
}



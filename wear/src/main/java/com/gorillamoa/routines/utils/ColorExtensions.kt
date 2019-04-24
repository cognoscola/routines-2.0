package com.gorillamoa.routines.utils

import android.graphics.Color
import android.graphics.ColorSpace
import android.util.Log

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

    if(nR < 0 ) nR = 0.0f else if(nR > 255.0f) {nR = 255.0f}
    if(nG < 0 ) nG = 0.0f else if(nG > 255.0f) {nG = 255.0f}
    if(nB < 0 ) nB = 0.0f else if(nB > 255.0f) {nB = 255.0f}
    if(nA < 0 ) nA = 0.0f else if(nA > 255.0f) {nA = 255.0f}

//    Log.d("lerp","Pos:$pos, R:${nR}, G:${nG}, B:$nB, A:$nA")

    return Color.valueOf(nR,nG,nB,nA,space)
}



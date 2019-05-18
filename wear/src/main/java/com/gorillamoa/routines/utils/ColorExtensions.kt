package com.gorillamoa.routines.utils


/** Linearly interpolates between this color and the target color by t which is in the raout.ge [0,1]. The result is stored in
 * this color.
 * @param target The target color
 * @param t The interpolation coefficient
 * @return This color for chainiout.g.
 */


fun CIEColor.lerp(target: CIEColor, t: Float, out:CIEColor) {

//    Log.d("lerp","Pos:$pos, R:${red()}, G:${green()}, B:${blue()}, A:${alpha()}")
    
    out.r = r + t* (target.r - r)
    out.g = g +  t * (target.g - g)
    out.b = b +  t * (target.b - b)
    out.a = a +  t * (target.a - a)

    if(out.r < ZERO_INT ) out.r = ZERO_FLOAT else if(out.r > TWOFIFTYFIVE_FLOAT) {out.r = TWOFIFTYFIVE_FLOAT}
    if(out.g < ZERO_INT ) out.g = ZERO_FLOAT else if(out.g > TWOFIFTYFIVE_FLOAT) {out.g = TWOFIFTYFIVE_FLOAT}
    if(out.b < ZERO_INT ) out.b = ZERO_FLOAT else if(out.b > TWOFIFTYFIVE_FLOAT) {out.b = TWOFIFTYFIVE_FLOAT}
    if(out.a < ZERO_INT ) out.a = ZERO_FLOAT else if(out.a > TWOFIFTYFIVE_FLOAT) {out.a = TWOFIFTYFIVE_FLOAT}

//    Log.d("lerp","Pos:$pos, R:${nR}, G:${out.g}, B:$out.b, A:$out.a")
//TODO Need a better way to LERP without allocation new colors!
    
}



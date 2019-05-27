package com.gorillamoa.routines.animation

import com.badlogic.ashley.core.Component

class AlphaV2Component:Component{

    var alpha = 0 //alpha between 0 and 255
    var delaySecond = 0.0 //how many seconds to delay until fading starts
    var fadeRatePerFrame = 0 //the fade rate

}
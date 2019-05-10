package com.gorillamoa.routines.utils

import java.util.*
import java.util.concurrent.TimeUnit

class CircularTimer{

    private var endTime = 0L
    private var startTime = 0L
    private var minutes = 0L
    private var upTime = 0L

    var startAngle = 0.0f
    var sweepAngle = 0.0f

    fun isRunning():Boolean{
        return (upTime<= endTime) and ((startTime + endTime + minutes) != 0L)
    }

    fun reset(){
        minutes = 0
        startTime = 0
        endTime = 0
    }

    fun setSelectedMinute(start:Long,timetoTrigger:Long){

        val cal = Calendar.getInstance()
        cal.timeInMillis = start
        cal.timeInMillis = timetoTrigger

        startTime = start
        endTime = timetoTrigger

        minutes = TimeUnit.MILLISECONDS.toMinutes(endTime - startTime)
    }

    fun calculateAngles(mCalendar: Calendar){
        if (isRunning()) {
            upTime = mCalendar.timeInMillis
            startAngle = calculateStartAngle(mCalendar)
            sweepAngle = calculateSweepAngle(mCalendar)
        }
    }

    private fun calculateStartAngle(mCalendar:Calendar):Float{

        return if (isRunning()) {
            return (mCalendar.get(Calendar.MINUTE) * ANGLE_PER_MINUTE) + ((ANGLE_PER_SECOND) * mCalendar.get(Calendar.SECOND)) - ZERO_ANGLE
        } else 0.0f

    }

    private fun calculateSweepAngle(mCalendar: Calendar):Float{

        return if (isRunning()) {

            val upTime = mCalendar.timeInMillis
            val diffMillis = endTime - startTime

            val dtPercent = (upTime - startTime).toDouble() / (diffMillis.toDouble())
            val remainingTimePercent = 1.0 - dtPercent
            return (remainingTimePercent * minutes * ANGLE_PER_MINUTE).toFloat()
        } else  0.0f

    }

    companion object {

        const val TWO = 2.0f
        const val ZER0 = 0.0f
        const val ZERO_ANGLE = 90.0f
        const val ANGLE_PER_MINUTE = 6.0f
        const val ANGLE_PER_SECOND = 1/60.0f




    }
}
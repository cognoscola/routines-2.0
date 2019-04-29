package com.gorillamoa.routines.views

import android.util.Log
import io.github.jdiemke.triangulation.Triangle2D
import io.github.jdiemke.triangulation.Vector2D
import java.util.*

private const val zeroD = 0.0

class BorderTrianglePack(val width:Double,val height:Double){

    @Suppress("unused")
    private val tag:String = BorderTrianglePack::class.java.name

    enum class Border {
        Left,
        Top,
        Right,
        Bottom,
        None
    }


    private var triangle: Triangle2D? = null

    private var potentialPoint: Vector2D? = null
    private var oldPointLocation: Vector2D? = null
    private var border:Border = Border.None

    private var xRepositioned = false
    private var yRepositioned = false

    fun examinePotential(questionableTriangle: Triangle2D) {

        Log.d("$tag examinePotential","Examining" +
                " A(${questionableTriangle.a.x}, ${questionableTriangle.a.y}) " +
                "B(${questionableTriangle.b.x}, ${questionableTriangle.b.y}) " +
                "C(${questionableTriangle.c.x}, ${questionableTriangle.c.y}) ")

        xRepositioned = false
        yRepositioned = false
        potentialPoint = null
        oldPointLocation = null
        border = Border.None
        triangle = questionableTriangle

        //before we morph it, check that we're not touching the edge
        //does this triangle touch the outer rim?? //We know it does if one of the vertices lies on the edge

        border = isTouchingEdges(questionableTriangle.a)
        if (border == Border.None) {
            border = isTouchingEdges(questionableTriangle.b)
            if(border == Border.None){
                border = isTouchingEdges(questionableTriangle.c)
                if(border != Border.None){
                    potentialPoint = questionableTriangle.c
                }
            } else {
                potentialPoint = questionableTriangle.b }
        }else {
            potentialPoint = questionableTriangle.a }


        //One of the vertices touches the edge lets save the coordinates before we move it
        potentialPoint?.let {
            oldPointLocation = Vector2D(potentialPoint!!.x, potentialPoint!!.y)
            Log.d("$tag examinePotential","Point on Border: $border")
        }

    }


    private fun isTouchingEdges(point: Vector2D):Border{

        var border = Border.None
        if(point.x == zeroD) border = Border.Left
        if(point.x == width) border = Border.Right
        if(point.y == zeroD) border = Border.Top
        if(point.y == height) border = Border.Bottom
        return  border
    }

    fun checkMovement(pointList: Vector<Vector2D>): Triangle2D? {
        potentialPoint?.let {

            xRepositioned = (potentialPoint!!.x != oldPointLocation!!.x)
            yRepositioned = (potentialPoint!!.y != oldPointLocation!!.y)
            return if (xRepositioned or yRepositioned ) {
                //they don't match, so it has relocated. Create new triangle
                //we already know A, since it is the morphed point
                val newA = Vector2D(potentialPoint!!.x, potentialPoint!!.y)
                createNewTriangle(border, oldPointLocation!!,newA, pointList)
            } else{
                Log.d("$tag checkMovement","Vertex did not move")
                null
            }
        }
        Log.d("$tag checkMovement","Not touching edge ")
        return null
    }
    private fun createNewTriangle(border:Border, old:Vector2D, pointA: Vector2D, pointList: Vector<Vector2D>):Triangle2D?{
        //now create the other 2
        Log.d("$tag createNewTriangle","old (${old.x} ${old.y})")


        return when (border) {
            Border.Left -> {
                Log.d("$tag createNewTriangle","Left")
                //get our list of possible Y coordinates
                val possibilities = doubleArrayOf(
                        0.0,
                        pointList[4].y,pointList[5].y,pointList[6].y,pointList[7].y,height)

                possibilities.sort()
                Log.d("$tag createNewTriangle","Possibilities: ${possibilities.joinToString(",")}")

                val index = possibilities.indexOf(possibilities.minBy { Math.abs(it - pointA.y) }?:0.0)
                if((index != 0 ) and (index != possibilities.lastIndex) and (index != -1)){
                    Log.d("$tag createNewTriangle","Success")
                    Triangle2D(pointA, Vector2D(0.0,possibilities[index - 1] ), Vector2D(0.0,possibilities[index+1]))
                    }
                else {
                    Log.d("$tag createNewTriangle","Index Not found!")
                    null
                }
            }

            Border.Top ->{
                Log.d("$tag createNewTriangle","Top")
                val possibilities = doubleArrayOf(
                        0.0,
                        pointList[8].x,pointList[9].x,pointList[10].x,pointList[11].x, width)

                possibilities.sort()
                Log.d("$tag createNewTriangle","Possibilities: ${possibilities.joinToString(",")}")
                val index = possibilities.indexOf(possibilities.minBy { Math.abs(it - pointA.x) }?:0.0)
                if((index != 0 ) and (index != possibilities.lastIndex) and (index != -1)){
                    Log.d("$tag createNewTriangle","Success")
                    Triangle2D(pointA, Vector2D(possibilities[index - 1],0.0 ), Vector2D(possibilities[index+1],0.0))
                }
                else {
                    Log.d("$tag createNewTriangle","Index Not found!")
                    null
                }
            }
            Border.Right ->{
                Log.d("$tag createNewTriangle","Right")
                val possibilities = doubleArrayOf(
                        0.0,
                        pointList[12].y,pointList[13].y,pointList[14].y,pointList[15].y, height)

                possibilities.sort()
                Log.d("$tag createNewTriangle","Possibilities: ${possibilities.joinToString(",")}")
                val index = possibilities.indexOf(possibilities.minBy { Math.abs(it - pointA.y) }?:0.0)
                if((index != 0 ) and (index != possibilities.lastIndex) and (index != -1)){
                    Log.d("$tag createNewTriangle","Success")
                    Triangle2D(pointA, Vector2D(width,possibilities[index - 1] ), Vector2D(width,possibilities[index+1]))
                }
                else {
                    Log.d("$tag createNewTriangle","Index Not found!")
                    null
                }
            }
            Border.Bottom ->{
                Log.d("$tag createNewTriangle","Bottom")
                val possibilities = doubleArrayOf(
                        0.0,
                        pointList[16].x,pointList[17].x,pointList[18].x,pointList[19].x, width)

                possibilities.sort()
                Log.d("$tag createNewTriangle","Possibilities: ${possibilities.joinToString("\n")}")
                val index = possibilities.indexOf(possibilities.minBy { Math.abs(it - pointA.x) }?:0.0)
                if((index != 0 ) and (index != possibilities.lastIndex) and (index != -1)){
                    Log.d("$tag createNewTriangle","Success")
                    Triangle2D(pointA, Vector2D(possibilities[index - 1], height), Vector2D(possibilities[index+1], height))
                }
                else {
                    Log.d("$tag createNewTriangle","Index Not found!")
                    null
                }
            }
            else -> {
                Log.d("$tag createNewTriangle","Unknown Border")
                null
            }
        }
    }



}

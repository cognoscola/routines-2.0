package com.gorillamoa.routines.views

import android.util.Log
import io.github.jdiemke.triangulation.Edge2D
import io.github.jdiemke.triangulation.Triangle2D
import io.github.jdiemke.triangulation.TriangleSoup
import io.github.jdiemke.triangulation.Vector2D

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

    private var pointOnEdge: Vector2D? = null
    private var oldPointLocation: Vector2D? = null
    private var border:Border = Border.None

    fun isTouchingBorder(questionableTriangle: Triangle2D):Boolean {

        Log.d("$tag examinePotential","Examining" +
                " A(${questionableTriangle.a.x}, ${questionableTriangle.a.y}) " +
                "B(${questionableTriangle.b.x}, ${questionableTriangle.b.y}) " +
                "C(${questionableTriangle.c.x}, ${questionableTriangle.c.y}) ")

        pointOnEdge = null
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
                    pointOnEdge = questionableTriangle.c
                }
            } else {
                pointOnEdge = questionableTriangle.b }
        }else {
            pointOnEdge = questionableTriangle.a }

        //potential Point contains the coordinates of the original a, b, ,c
        pointOnEdge?.let {
            oldPointLocation = Vector2D(pointOnEdge!!.x, pointOnEdge!!.y)
            Log.d("$tag examinePotential","Point on Border: $border")
            return true
        }
        return false
    }


    private fun isTouchingEdges(point: Vector2D):Border{

        var border = Border.None
        if(point.x == zeroD) border = Border.Left
        if(point.x == width) border = Border.Right
        if(point.y == zeroD) border = Border.Top
        if(point.y == height) border = Border.Bottom
        return  border
    }

    fun checkMovement(triangleSoup: TriangleSoup,triangle:Triangle2D): Triangle2D? {

        pointOnEdge?.let {

            return if (triangle.hasMorphed()) {
                //they don't match, so it has relocated. Create new triangle
                //we already know A, since it is the morphed point
                val newA:Vector2D?
                if (pointOnEdge == triangle.a) {
                    newA = Vector2D(triangle.qA().x, triangle.qA().y)
                }else if (pointOnEdge == triangle.b) {
                    newA = Vector2D(triangle.qB().x, triangle.qB().y)
                }else if (pointOnEdge == triangle.c) {
                    newA = Vector2D(triangle.qC().x, triangle.qC().y)
                }else { newA = null}

                newA?.let {
                    createNewTriangle(border, triangle,newA, triangleSoup)
                }?: kotlin.run {
                    Log.d("$tag checkMovement","Vertex did not move")
                null
                }

            } else{
                kotlin.run { Log.d("$tag checkMovement","Vertex did not move") }
                null
            }
        }
        Log.d("$tag checkMovement","Not touching edge ")
        return null
    }
    private fun createNewTriangle(border:Border, triangle: Triangle2D, pointA: Vector2D, soup:TriangleSoup):Triangle2D?{
        //now create the other 2
//        Log.d("$tag createNewTriangle","old (${old.x} ${old.y})")

        //we have the old point and the new point. Old point is kind of useless unless
        //we'd like to create 2 triangles with the old point acting as a kind of middle point
        //but more on that later... for now just make 1 triangle

        //Get a neighbouring triangle (there should only be one) of our current triangle. We do so
        //by taking an edge and seeing if it is shared.

        //Which point is A again?
        var neighbour:Triangle2D? = null
        var edge:Edge2D? = null
        var pointC :Vector2D? = null //pointC must be the last unused point of our triangle

        if ((triangle.a == pointA) and (neighbour == null)) {
            //edge will be A - B or A -C
            //lets try A - B
            edge = Edge2D(triangle.a, triangle.b)
            neighbour = soup.findNeighbour(triangle,edge).let {

                pointC = Vector2D(triangle.qC().x, triangle.qC().y)
                it
            } ?: kotlin.run {
                edge = Edge2D(triangle.a,triangle.c)
                soup.findNeighbour(triangle,edge)?.let {

                    pointC = Vector2D(triangle.qB().x, triangle.qB().y)
                    it
                }?: kotlin.run {
                    Log.d("$tag createNewTriangle","Something went wrong, lets try other cases")
                    neighbour = null
                    edge = null
                    null
                }
            }
        }

        if ((triangle.b == pointA) and (neighbour == null)) {
            //edge will be B - A or B -C
            edge = Edge2D(triangle.b, triangle.a)
            neighbour = soup.findNeighbour(triangle,edge)?.let {

                pointC = Vector2D(triangle.qC().x, triangle.qC().y)
                it
            } ?: kotlin.run {
                edge = Edge2D(triangle.b,triangle.c)
                soup.findNeighbour(triangle,edge)?.let {

                    pointC = Vector2D(triangle.qA().x, triangle.qA().y)
                    it
                }?: kotlin.run {
                    Log.d("$tag createNewTriangle","Something went wrong, lets try last cases")
                    neighbour = null
                    edge = null
                    null
                }
            }
        }

        if ((triangle.c == pointA) and (neighbour == null)) {
            //edge will be C - A or C -B
            edge = Edge2D(triangle.c, triangle.a)
            neighbour = soup.findNeighbour(triangle,edge)?.let {
                pointC = Vector2D(triangle.qB().x,triangle.qB().y)
                it
            } ?: kotlin.run {
                edge = Edge2D(triangle.c,triangle.b)
                soup.findNeighbour(triangle,edge)?.let {
                    pointC =Vector2D(triangle.qA().x,triangle.qA().y)
                    it
                }?: kotlin.run {
                    Log.d("$tag createNewTriangle","Something's up! WTF")
                    neighbour = null
                    edge = null
                    null
                }
            }
        }

        if(neighbour == null){
            Log.d("$tag createNewTriangle","Couldn't find any neighbours! Possibly because of morph")
            return null
        }

        //at this point we should have a neighbouring triangle
        //find the point that is not a part of the edge
        var pointB:Vector2D? = null
        if ((neighbour!!.a != edge!!.a) and (neighbour!!.a != edge!!.b)) {
            //we'll use point neighbour point A as the 2nd point
            pointB = Vector2D(neighbour!!.a.x,neighbour!!.a.y)
        }

        if ((neighbour!!.b != edge!!.a) and (neighbour!!.b != edge!!.b) and (pointB != null)) {
            //we'll use point neighbour point A as the 2nd point
            pointB = Vector2D(neighbour!!.b.x,neighbour!!.b.y)
        }

        if ((neighbour!!.c != edge!!.a) and (neighbour!!.c != edge!!.b) and (pointB != null)) {
            //we'll use point neighbour point c as the 2nd point
            pointB = Vector2D(neighbour!!.c.x,neighbour!!.c.y)
        }

        //as a final precaution we'll make sure this point is on the EDGE
        if (!((pointB!!.x == 0.0) or (pointB.x == width) or (pointB.y == 0.0) or (pointB.y == height))) {

            Log.d("$tag createNewTriangle","This point won't work! it doesn't lie on the edge")
            return null
        }

        //Now we have all points, make the triangle!
        Log.d("$tag createNewTriangle","Success")
        return Triangle2D(pointA,pointB,pointC)


/*
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
*/
    }


}

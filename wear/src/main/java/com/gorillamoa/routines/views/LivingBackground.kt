package com.gorillamoa.routines.views

import android.graphics.*
import android.os.SystemClock
import android.util.Log
import androidx.palette.graphics.Palette
import com.gorillamoa.routines.utils.lerp
import java.util.*
import kotlin.math.roundToInt
import android.os.VibrationEffect
import android.os.Vibrator
import io.github.jdiemke.triangulation.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.cos
import kotlin.math.sin

private const val WORKING_BITMAP_WIDTH = 200
private const val WORKING_BITMAP_HEIGHT = 200

private const val widthD = WORKING_BITMAP_WIDTH.toDouble()
private const val heightD = WORKING_BITMAP_WIDTH.toDouble()


class LivingBackground{

    @Suppress("unused")
    private val tag:String = LivingBackground::class.java.name

    private lateinit var mAlarmPaint:Paint
    private lateinit var mBackgroundBitmap: Bitmap
    private lateinit var morphedBitmap:Bitmap
    private lateinit var mGrayBackgroundBitmap: Bitmap

    private lateinit var workingBitmap:Bitmap
    private lateinit var workingCanvas:Canvas

    private lateinit var mBackgroundPaint: Paint
    private lateinit var mMorphPaint:Paint
    private lateinit var debugPaint:Paint
    private val baseDrawingMode: Xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
    private val morphDrawingMode: Xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    private val bgDrawingMode: Xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_ATOP)
    private var morphPath = Path().apply {
        fillType = Path.FillType.EVEN_ODD

    }
    private var morphAngle = 0.0f

    val lab = ColorSpace.get(ColorSpace.Named.CIE_LAB)

    private lateinit var palette: Palette

    var scale = 0.0f
    var triangleSoup:ArrayList<Triangle2D>? = null
    var circleTangents:List<Edge2D>? = null
    var intersectingMap:HashMap<Edge2D,ArrayList<Triangle2D>> = HashMap()
    var overlappingMap:HashMap<Edge2D,ArrayList<Triangle2D>> = HashMap()
    var touchingMap:HashMap<Edge2D,ArrayList<Triangle2D>> = HashMap()

    //COLORS
    private val backgroundAlpha = 255.0f
    private val topLeft = Color.valueOf(255.0f,245.0f,235.0f,backgroundAlpha,lab)
    private val bottomRight = Color.valueOf(127.0f,39.0f,4.0f, backgroundAlpha,lab)
    private val bottomLeft = Color.valueOf(175.0f,111.0f,84.0f, backgroundAlpha,lab)
    private val topRight = Color.valueOf(175.0f,111.0f,84.0f,backgroundAlpha,lab)


    private var isAlarmOn = false
    private var isAlarmAlphaIncreasing = true
    private var currentTimeCounter = 0L
    private var currentAlarmAlpha = 0.0f
    private val TIME2MAX = 1000 // 1 second to light up, and 1 to show up.
    private val MAXALPHA = 255.0f
    private var lastMeasuredTime = 0L
    private var dt = 0L

    val vibrationEffect = VibrationEffect.createOneShot(1000,VibrationEffect.DEFAULT_AMPLITUDE)
    lateinit var vibrator:Vibrator

    fun enableAlarm(){ isAlarmOn = true}
    fun disableAlarm() {isAlarmOn = false}
    fun isAlarmEnabled() = isAlarmOn
//TODO we'll show 1 generic alarm, but modify that alarm slightly (e.g. color) to indicate which type of alarm went off

    fun initializeBackground(vibratorService: Vibrator,paletteCallback:((Palette)->Any?)? = null ) {
      vibrator = vibratorService

        mBackgroundPaint = Paint().apply {
            color = Color.BLACK
        }

        mMorphPaint = Paint().apply {

            color =Color.BLACK
            isAntiAlias = true
            strokeWidth = 2.0f
            style = Paint.Style.FILL
        }

        mAlarmPaint = Paint().apply {
            color = Color.RED
            isAntiAlias = true
            strokeWidth = 1.0f
            style = Paint.Style.STROKE
            //too performant
           /* setShadowLayer(
                    6f, 0f, 0f, Color.RED)*/
        }

        debugPaint = Paint().apply {
            color = Color.BLUE
            isAntiAlias = true
            strokeWidth = 1.0f
            style = Paint.Style.STROKE
        }

        generateBackgroundBitmaps()

        /* Extracts colors from background image to improve watchface style. */
        Palette.from(mBackgroundBitmap).generate {
            it?.apply {
                palette = it
                paletteCallback?.invoke(it)
            }
        }.get()

    }

    fun getPalette() = palette


    /**
     * Draw the background. There are 3 main steps:
     * 1. Draw the morphed background
     * 2. Draw the unmorphed background
     * 3. Draw Features
     */
    fun drawBackground(canvas: Canvas, mAmbient:Boolean, mLowBitAmbient:Boolean, mBurnInProtection:Boolean, bounds:Rect, rotation:Float) {

        if (mAmbient && (mLowBitAmbient || mBurnInProtection)) {
            canvas.drawColor(Color.BLACK)
        } else if (mAmbient) {
            canvas.drawBitmap(mGrayBackgroundBitmap, 0f, 0f, mBackgroundPaint)
        } else {
//            canvas.drawBitmap(mBackgroundBitmap, 0f, 0f, mBackgroundPaint)

            //recreate new path
            //TODO uncomment
/*            morphPath.reset()
            morphPath.moveTo(bounds.width().div(2.0f), bounds.height().div(2.0f))
            morphPath.lineTo(bounds.width().div(2.0f), 0.0f)
            morphPath.arcTo(0.0f,0.0f, bounds.width().toFloat(), bounds.height().toFloat(),-90.0f,rotation,true)
            morphPath.lineTo(bounds.width().div(2.0f), bounds.height().div(2.0f))*/


/*
            //draw morph background
            //TODO uncomment
            workingCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            mMorphPaint.color = Color.RED
            mMorphPaint.xfermode = baseDrawingMode
            workingCanvas.drawPath(morphPath, mMorphPaint)
            mMorphPaint.xfermode =morphDrawingMode
            workingCanvas.drawBitmap(morphedBitmap,0.0f,0.0f,mMorphPaint)
            mMorphPaint.xfermode = bgDrawingMode
            workingCanvas.drawBitmap(mBackgroundBitmap,0.0f,0.0f,mMorphPaint)
*/

            canvas.save()
            canvas.scale(1/scale, 1/scale)
            canvas.translate(bounds.right/2.0f,bounds.bottom/2.0f)
            canvas.drawBitmap(morphedBitmap,0.0f,0.0f,mAlarmPaint)
            canvas.restore()

//            canvas.drawBitmap(workingBitmap,bounds.left.toFloat(),bounds.top.toFloat(),mAlarmPaint)



            //draw our tangent temporarily
//            canvas.save()
//            canvas.scale(scale, scale)
            mAlarmPaint.alpha = 255
            circleTangents?.forEach {

                //draw the tangent
//                canvas.drawLine(it.a.x.toFloat(),it.a.y.toFloat(),it.b.x.toFloat(),it.b.y.toFloat(), debugPaint)

/*
                mAlarmPaint.color = Color.RED
                var triangleSet = intersectingMap[it]
                triangleSet?.let { list ->
                    list.forEach { triangle ->
                        canvas.drawLine(triangle.a.x.toFloat(),triangle.a.y.toFloat(),triangle.b.x.toFloat(),triangle.b.y.toFloat(),mAlarmPaint)
                        canvas.drawLine(triangle.b.x.toFloat(),triangle.b.y.toFloat(),triangle.c.x.toFloat(),triangle.c.y.toFloat(),mAlarmPaint)
                        canvas.drawLine(triangle.c.x.toFloat(),triangle.c.y.toFloat(),triangle.a.x.toFloat(),triangle.a.y.toFloat(),mAlarmPaint)
                    }
                }
*/

                canvas.save()
                canvas.scale(scale,scale)
                mAlarmPaint.color = Color.YELLOW
                var triangleSet = overlappingMap[it]
                triangleSet?.let { list ->
                    list.forEach { triangle ->
                        canvas.drawLine(triangle.a.x.toFloat(),triangle.a.y.toFloat(),triangle.b.x.toFloat(),triangle.b.y.toFloat(),mAlarmPaint)
                        canvas.drawLine(triangle.b.x.toFloat(),triangle.b.y.toFloat(),triangle.c.x.toFloat(),triangle.c.y.toFloat(),mAlarmPaint)
                        canvas.drawLine(triangle.c.x.toFloat(),triangle.c.y.toFloat(),triangle.a.x.toFloat(),triangle.a.y.toFloat(),mAlarmPaint)
                    }
                }
                canvas.restore()

/*
                mAlarmPaint.color = Color.GREEN
                triangleSet = touchingMap[it]
                triangleSet?.let { list ->
                    list.forEach { triangle ->
                        canvas.drawLine(triangle.a.x.toFloat(),triangle.a.y.toFloat(),triangle.b.x.toFloat(),triangle.b.y.toFloat(),mAlarmPaint)
                        canvas.drawLine(triangle.b.x.toFloat(),triangle.b.y.toFloat(),triangle.c.x.toFloat(),triangle.c.y.toFloat(),mAlarmPaint)
                        canvas.drawLine(triangle.c.x.toFloat(),triangle.c.y.toFloat(),triangle.a.x.toFloat(),triangle.a.y.toFloat(),mAlarmPaint)
                    }
                }
*/

            }

           // mAlarmPaintcanvas.restore()

            canvas.save()
            canvas.scale(scale,scale)

            if (isAlarmOn) {

                if(lastMeasuredTime == 0L) {
                    lastMeasuredTime = SystemClock.uptimeMillis()
                    return
                }

                dt = (SystemClock.uptimeMillis() - lastMeasuredTime) //first dt will be 0

                if (isAlarmAlphaIncreasing) {

                    currentTimeCounter += dt
                    if(currentTimeCounter > 1000.0) {
                        currentTimeCounter = 1000
                        isAlarmAlphaIncreasing = false
                    }
                }else{
                    currentTimeCounter -= dt
                    if(currentTimeCounter < 0.0) {
                        currentTimeCounter = 0
                        isAlarmAlphaIncreasing = true

                        //sound a vibration
                        vibrator.vibrate(vibrationEffect)
                    }
                }
                currentAlarmAlpha = (currentTimeCounter.toFloat().div(TIME2MAX.toFloat())* MAXALPHA)
                if(currentAlarmAlpha > 255.0)currentAlarmAlpha = 255f else if(currentAlarmAlpha < 0){currentAlarmAlpha = 0f}
                lastMeasuredTime = SystemClock.uptimeMillis()

                mAlarmPaint.alpha = currentAlarmAlpha.roundToInt()

//                canvas.save()
//                canvas.scale(scale,scale)
                //now draw the lines

                //TODO find performance between drawing another image on top and drawing these lines
                triangleSoup?.forEach {

                    //TODO don't draw duplicate triangles
                    canvas.drawLine(it.a.x.toFloat(),it.a.y.toFloat(),it.b.x.toFloat(),it.b.y.toFloat(),mAlarmPaint)
                    canvas.drawLine(it.b.x.toFloat(),it.b.y.toFloat(),it.c.x.toFloat(),it.c.y.toFloat(),mAlarmPaint)
                    canvas.drawLine(it.c.x.toFloat(),it.c.y.toFloat(),it.a.x.toFloat(),it.a.y.toFloat(),mAlarmPaint)
                }

            }
            canvas.restore()
        }
    }


    var testShader:LinearGradient? = null

    fun scaleBackground(width:Int,height:Int){

        scale = width.toFloat() / mBackgroundBitmap.width.toFloat()

        mBackgroundBitmap = Bitmap.createScaledBitmap(mBackgroundBitmap,
                (mBackgroundBitmap.width * scale).toInt(),
                (mBackgroundBitmap.height * scale).toInt(), true)

        morphedBitmap = Bitmap.createScaledBitmap(morphedBitmap,
                (morphedBitmap.width * scale).toInt(),
                (morphedBitmap.height * scale).toInt(), true)



        workingBitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
        workingCanvas = Canvas(workingBitmap)


        //Use this opportunity to create a morphed background

        //first determine radius of morphing circle
        //lets just make the radius somewhere close to the outer rim of the circular watch

    }

    private fun morphOnce(tangent: Edge2D, triangle: Triangle2D){

        var moprhedSuccess = false
        triangle.computeClosestPointsToAEdge(tangent)
        moprhedSuccess = triangle.moveClosestUntouchingVertexToQ()

    }

/*
    private fun morph(tangent: Edge2D){
        //
        var moprhedSuccess = false
        var status = -1

        val morphedList = ArrayList<Triangle2D>()
        val triangleSet = intersectingMap[tangent]
        triangleSet?.let { list ->


            list.forEach { triangle ->

                triangle.computeClosestPointsToAEdge(tangent)
                moprhedSuccess = triangle.moveClosestUntouchingVertexToQ()

                if (moprhedSuccess) {

                    //check once more if we're actually intersecting or not
                    if (isIntersecting(tangent.a, tangent.b, triangle.a, triangle.b, triangle.c) != INTERSECTING) {

                        morphedList.add(triangle)
                        list.remove(triangle)
                    }

                }


                */
/*status = isIntersecting(tangent.a, tangent.b, triangle.a, triangle.b, triangle.c)
                while ((status == INTERSECTING) and moprhedSuccess) {
                    moprhedSuccess =triangle.moveClosestUntouchingVertexToQ()
                }

                if (status != INTERSECTING) {
                    //remove from this list

                }*//*

            }

            if (morphedList.size > 0) {
                morphedMap[tangent] = morphedList
            }
        }

    }
*/



    private fun FindEdgeBetweenPointAndLine(tangent:Edge2D, point: Vector2D, outEdge2D: Edge2D){
        findQ(tangent,point,outEdge2D.b)
    }

    private fun findQ(edge: Edge2D, point:Vector2D, outPoint:Vector2D) {
        val px = edge.b.x - edge.a.x //x portion of our line
        val py = edge.b.y - edge.a.y //y portion of our line
        val temp = px * px + py * py  // denominator of our d = sqrt(A cross B)/sqrt( temp )
        var u = ((point.x - edge.a.x) * px + (point.y - edge.a.y) * py) / temp
        if (u > 1) {
            u = 1.0
        } else if (u < 0) {
            u = 0.0
        }

        outPoint.x =  edge.a.x + u * px //Qx
        outPoint.y = edge.a.y + u * py //Qy

//        val dx = x - point.x
//        val dy = y - point.y

//        return Math.sqrt((dx * dx + dy * dy))

    }

    /** Check whether P and Q lie on the same side of line AB */
    private fun Side(p: Vector2D, q: Vector2D, a: Vector2D, b: Vector2D): Float {
        val z1 = (b.x - a.x) * (p.y - a.y) - (p.x - a.x) * (b.y - a.y)
        val z2 = (b.x - a.x) * (q.y - a.y) - (q.x - a.x) * (b.y - a.y)
        return (z1 * z2).toFloat()
    }

    val INTERSECTING = 0 // RED
    val NOT_INTERSECTING = 1
    val OVERLAPPING = 2 //Yellow
    val TOUCHING = 3 //Green

    /* Check whether segment P0P1 intersects with triangle t0t1t2 */
    fun isIntersecting(p0: Vector2D, p1: Vector2D, t0: Vector2D, t1: Vector2D, t2: Vector2D): Int {
        /* Check whether segment is outside one of the three half-planes
     * delimited by the triangle. */
        val f1 = Side(p0, t2, t0, t1)
        val f2 = Side(p1, t2, t0, t1)
        val f3 = Side(p0, t0, t1, t2)
        val f4 = Side(p1, t0, t1, t2)
        val f5 = Side(p0, t1, t2, t0)
        val f6 = Side(p1, t1, t2, t0)
        /* Check whether triangle is totally inside one of the two half-planes
     * delimited by the segment. */
        val f7 = Side(t0, t1, p0, p1)
        val f8 = Side(t1, t2, p0, p1)

        /* If segment is strictly outside triangle, or triangle is strictly
     * apart from the line, we're not intersecting */
        if (f1 < 0 && f2 < 0 || f3 < 0 && f4 < 0 || f5 < 0 && f6 < 0
                || f7 > 0 && f8 > 0)
            return NOT_INTERSECTING

        /* If segment is aligned with one of the edges, we're overlapping */
        if (f1 == 0f && f2 == 0f || f3 == 0f && f4 == 0f || f5 == 0f && f6 == 0f)
            return OVERLAPPING

        /* If segment is outside but not strictly, or triangle is apart but
     * not strictly, we're touching */
        if (f1 <= 0 && f2 <= 0 || f3 <= 0 && f4 <= 0 || f5 <= 0 && f6 <= 0
                || f7 >= 0 && f8 >= 0)
            return TOUCHING

        /* If both segment points are strictly inside the triangle, we
     * are not intersecting either */
        return if (f1 > 0 && f2 > 0 && f3 > 0 && f4 > 0 && f5 > 0 && f6 > 0) NOT_INTERSECTING else INTERSECTING

        /* Otherwise we're intersecting with at least one edge */
    }

    fun initGrayBackgroundBitmap() {
        mGrayBackgroundBitmap = Bitmap.createBitmap(
                mBackgroundBitmap.width,
                mBackgroundBitmap.height,
                Bitmap.Config.ARGB_8888)
        val canvas = Canvas(mGrayBackgroundBitmap)
        val grayPaint = Paint()
        val colorMatrix = ColorMatrix()
        colorMatrix.setSaturation(0f)
        val filter = ColorMatrixColorFilter(colorMatrix)
        grayPaint.colorFilter = filter
        canvas.drawBitmap(mBackgroundBitmap, 0f, 0f, grayPaint)
    }

    private fun generateWaterColorBackground():Bitmap{
        val height = 200.0f
        val width = 200.0f
        val max_radius = 40.0f
        val intermidiateBitmap = Bitmap.createBitmap(width.toInt(),height.toInt(),Bitmap.Config.ARGB_8888)
        val canvas = Canvas(intermidiateBitmap)

        val alpha = 40.0f

        canvas.drawColor(Color.WHITE)

        val painter =Paint().apply {
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        //TODO look up how to spread colors better
        //we'll make a gradient of 4 colors for now,


        val random = Random()
        for (i in 0..250) {

            val x = random.nextFloat() * width
            val y = random.nextFloat() * height
            var radius = random.nextFloat() * max_radius + 10.0f

            if(radius < 0.0) radius = 0.0f

            val colorLeft = topLeft.lerp(bottomLeft,y/height,lab)
            val colorRight = topRight.lerp(bottomRight,y/height,lab)
            val final =  colorLeft.lerp(colorRight,x/width,lab)
            painter.color = Color.argb(final.alpha().roundToInt(),final.red().roundToInt(), final.green().roundToInt(), final.blue().roundToInt())

            canvas.drawCircle(x,y,radius,painter)
        }

        val finalBitmap = intermidiateBitmap.copy(Bitmap.Config.ARGB_8888,false)
        intermidiateBitmap.recycle()
        return finalBitmap

    }

    private fun generateBitmapFromTriangles(width:Double, height:Double, triangles:List<Triangle2D>):Bitmap{


        //we'll make a gradient of 4 colors for now,
        val intermidiateBitmap = Bitmap.createBitmap(width.toInt(), height.toInt(),Bitmap.Config.ARGB_8888)
        val canvas = Canvas(intermidiateBitmap)

        var centerX:Double
        var centerY:Double
        val path=Path()
        path.fillType = Path.FillType.EVEN_ODD

        val painter =Paint().apply {

            style = Paint.Style.FILL_AND_STROKE
            isAntiAlias = true
            strokeWidth = 1.0f
            color =Color.WHITE

        }

        canvas.drawColor(Color.WHITE)
        triangles.forEach {

            //                    Log.d("$tag generateBackgroundBitmaps","Triangle: A(${it.a.x},${it.a.y}) B(${it.b.x},${it.b.y}) C(${it.c.x},${it.c.y})")

            //first find the Center Coordinates
            centerX = (it.a.x + it.b.x + it.c.x).div(3.0)
            centerY = (it.a.y + it.b.y + it.c.y).div(3.0)

//                    Log.d("$tag generateBackgroundBitmaps","Centroid: $centerX, $centerY")

            //now use the coordinates to locate the correct color
            val colorLeft = topLeft.lerp(bottomLeft, centerY.toFloat() / height.toFloat(), lab)
            val colorRight = topRight.lerp(bottomRight, centerY.toFloat() / height.toFloat(), lab)
            val final = colorLeft.lerp(colorRight, centerX.toFloat() / width.toFloat(), lab)
            painter.color = Color.argb(final.alpha().roundToInt(), final.red().roundToInt(), final.green().roundToInt(), final.blue().roundToInt())


            //draw the centroids
            //  canvas.drawPoint(centerX.toFloat(),centerY.toFloat(),painter)

            //now we draw the triangle
            path.moveTo(it.a.x.toFloat(), it.a.y.toFloat())
            path.lineTo(it.b.x.toFloat(), it.b.y.toFloat())
            path.lineTo(it.c.x.toFloat(), it.c.y.toFloat())
            path.lineTo(it.a.x.toFloat(), it.a.y.toFloat())

            canvas.drawPath(path, painter)
            path.reset()
        }

        val finalBitmap = intermidiateBitmap.copy(Bitmap.Config.ARGB_8888,false)
        intermidiateBitmap.recycle()
        return  finalBitmap

    }

    private fun generateBackgroundBitmaps() {

        val zeroD = 0.0

        //we'll create delayney triangles
        val points =61

        val random = Random()

        //initialize an empty array of floating points to mark the vertices of our triangles
        val point2ds = Vector<Vector2D>(points)

        //place points on the corners of our quad
        point2ds.addElement(Vector2D(0.0,0.0)) //top left
        point2ds.addElement(Vector2D(widthD,heightD)) // bottom right
        point2ds.addElement(Vector2D(0.0,heightD)) // bottom left
        point2ds.addElement(Vector2D(widthD,0.0)) //top right

        val halfWidth = widthD.times(0.5)
        val halfHeight = heightD.times(0.5)

        for (i in 4 until points) {

            //place points on the edges of the quad
            if (i < 8) {
                //left edge
                point2ds.addElement(Vector2D(  0.0, random.nextDouble() * heightD))
            } else if (i < 12) {
                //top edge
                point2ds.addElement(Vector2D((widthD * random.nextDouble()), heightD))
            } else if (i < 16) {
                //right edge
                point2ds.addElement(Vector2D(widthD, random.nextDouble() * heightD))
            } else if (i < 20) {
                //bottom edgeD
                point2ds.addElement(Vector2D(widthD * random.nextDouble(), 0.0))

                //in order to disperse points more evenly across the entire quad, the quad is split into 4 quadrants
                // and random points are generated within each quadrant
            } else if (i < 30) {
                //bottom left quadrant
                point2ds.addElement(Vector2D(random.nextDouble() * halfWidth, random.nextDouble() * halfHeight))
            } else if (i < 40) {
                //top left quadrant
                point2ds.addElement(Vector2D(random.nextDouble() *halfWidth, (random.nextDouble() * halfHeight) + halfHeight))
            } else if (i < 50) {
                //top right quadrant
                point2ds.addElement(Vector2D((random.nextDouble() * halfWidth) + halfWidth, (random.nextDouble()*halfHeight + halfHeight) ))
            } else if (i < 60) {
                //bottom right quadrant
                point2ds.addElement(Vector2D((random.nextDouble() * halfWidth) + halfWidth , random.nextDouble() * halfHeight))
            }
        }

        triangleSoup = try {
            val delaunayTriangulator = DelaunayTriangulator(point2ds)
            delaunayTriangulator.triangulate()
            delaunayTriangulator.triangles as ArrayList<Triangle2D>

        } catch (e: NotEnoughPointsException) {
            Log.d("$tag generateBackgroundBitmaps","Woops Triangulation")
            null
        }

        if (triangleSoup == null) {
            return
        }

        mBackgroundBitmap = generateBitmapFromTriangles(widthD, heightD,triangleSoup!!)

        //generate Morphed background
        val radius = (WORKING_BITMAP_WIDTH.div(2.0) - 15.0).toFloat()
        //now find all the triangles that intersect with this circle, we do this by dividing the circle into tangents
        //60 of them since 360/12 = 30 sections
        var xCenter = WORKING_BITMAP_WIDTH.div(2.0)
        var yCenter = WORKING_BITMAP_HEIGHT.div(2.0)

        val degreesPerSection = 12.0
        val numSections = (360.0 / degreesPerSection).roundToInt()
        circleTangents = List(numSections) {pos ->

            //the first tangent is the line between 0 and 6 degrees:
            val start:Vector2D
            val end:Vector2D
            if (pos < numSections - 1 ) {
                start = Vector2D((radius * sin(Math.toRadians(pos *degreesPerSection))) + xCenter,(radius * -cos(Math.toRadians(pos*degreesPerSection))) + xCenter)
                end = Vector2D((radius * sin(Math.toRadians((pos + 1)*degreesPerSection))) + xCenter,(radius * -cos(Math.toRadians((pos + 1)*degreesPerSection))) + xCenter)

            }else{
                start = Vector2D((radius * sin(Math.toRadians(pos*degreesPerSection))) + xCenter,radius * -cos(Math.toRadians(pos * degreesPerSection)) + xCenter)
                end = Vector2D((radius * sin(Math.toRadians(0.0))) + xCenter,(radius * -cos(Math.toRadians(0.0))) + xCenter)
            }

            val tangent = Edge2D(start,end)

            //Now find all that touch this tangent
            val touching = ArrayList<Triangle2D>()
            val overlapping = ArrayList<Triangle2D>()
            val intersecting = ArrayList<Triangle2D>()

            val borderTrianglePack = BorderTrianglePack(widthD, heightD)

            val trianglesToAdd = ArrayList<Triangle2D>()

            triangleSoup?.forEach { triangle ->

                when(isIntersecting(tangent.a,tangent.b,triangle.a,triangle.b,triangle.c)){
                    TOUCHING ->{ touching.add(triangle) }
                    OVERLAPPING -> { overlapping.add(triangle)}
                    INTERSECTING -> {

                        borderTrianglePack.examinePotential(triangle)
                        morphOnce(tangent,triangle)
                        borderTrianglePack.checkMovement(point2ds)?.let { trianglesToAdd.add(it) }
                        if (isIntersecting(tangent.a, tangent.b, triangle.a, triangle.b, triangle.c) == INTERSECTING) {

                            //in the space that was made.
                            borderTrianglePack.examinePotential(triangle)
                            morphOnce(tangent,triangle)
                            borderTrianglePack.checkMovement(point2ds)?.let { trianglesToAdd.add(it) }

                            if (isIntersecting(tangent.a, tangent.b, triangle.a, triangle.b, triangle.c) == INTERSECTING){
                                intersecting.add(triangle)
                            }else{
                                touching.add(triangle)
                            }
                        }else{
                            touching.add(triangle)
                        }
                    }
                }
            }
            if (touching.size > 0) {
                touchingMap[tangent] = touching
            }
            if (overlapping.size > 0) {
                overlappingMap[tangent] = overlapping
            }
            if (intersecting.size > 0) {
                intersectingMap[tangent] = intersecting
            }

            trianglesToAdd.forEach {
                triangleSoup!!.add(it)
            }
            trianglesToAdd.clear()

            tangent
        }

        //We morphed the background slightly, now lets create the bitmap for it.
        morphedBitmap = generateBitmapFromTriangles(widthD,heightD, triangleSoup!!)

    }

}
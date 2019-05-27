package com.gorillamoa.routines.views

import android.graphics.*
import android.os.SystemClock
import android.util.Log
import androidx.palette.graphics.Palette
import java.util.*
import kotlin.math.roundToInt
import android.os.VibrationEffect
import android.os.Vibrator
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.PooledEngine
import com.gorillamoa.routines.animation.*
import com.gorillamoa.routines.utils.CIEColor
import com.gorillamoa.routines.utils.CircularTimer
import com.gorillamoa.routines.utils.lerp
import io.github.jdiemke.triangulation.*
import kotlin.collections.ArrayList

private const val WORKING_BITMAP_WIDTH = 200

private const val widthD = WORKING_BITMAP_WIDTH.toDouble()
private const val heightD = WORKING_BITMAP_WIDTH.toDouble()

//TODO transition between off and on smoothly (by showing edges etc..)
//one idea is to place different 3 of 4 different bitmaps with varying alphas and just remove
//them one by one
class LivingBackground {

    @Suppress("unused")
    private val tag: String = LivingBackground::class.java.name

    private lateinit var mAlarmPaint: Paint
    private lateinit var mBackgroundBitmap: Bitmap
    private lateinit var morphedBitmap: Bitmap
    private lateinit var mGrayBackgroundBitmap: Bitmap

    private lateinit var workingBitmap: Bitmap
    private lateinit var workingCanvas: Canvas

    private lateinit var mBackgroundPaint: Paint
    private lateinit var mMorphPaint: Paint
    private lateinit var debugPaint: Paint

    private lateinit var triangulator: DelaunayTriangulator

    private val baseDrawingMode: Xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC)
    private val morphDrawingMode: Xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    private val bgDrawingMode: Xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_ATOP)

    //FOR animation
    private var engine= PooledEngine()
    private var fadeSystem:VectorFadeSystem? = null
    private var renderSystem:RenderSystem? = null

    private var morphPath = Path().apply {
        fillType = Path.FillType.EVEN_ODD

    }

//    val lab = ColorSpace.get(ColorSpace.Named.CIE_LAB)

    private lateinit var palette: Palette

    var scale = 0.0f
    var triangleSoup: ArrayList<Triangle2D>? = null

    //COLORS
    private val backgroundAlpha = 255.0f

    private var isAlarmOn = false
    private var isAlarmAlphaIncreasing = true
    private var currentTimeCounter = 0L
    private var currentAlarmAlpha = 0.0f
    private val TIME2MAX = 1000.0f // 1 second to light up, and 1 to show up.
    private val MAXALPHA = 255.0f
    private var lastMeasuredTime = 0L
    private var dt = 0L

    val vibrationEffect = VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE)
    lateinit var vibrator: Vibrator

    init {

        LivingBackground.topLeft.apply {
            r = 255.0f
            g = 245.0f
            b = 230.0f
            a = backgroundAlpha
        }

        LivingBackground.bottomRight.apply {
            r = 127f
            g = 39f
            b = 4f
            a = backgroundAlpha
        }

        LivingBackground.bottomLeft.apply {
            r = 175f
            g = 111f
            b = 84f
            a = backgroundAlpha
        }

        LivingBackground.topRight.apply {
            r = 175f
            g = 111f
            b = 84f
            a = backgroundAlpha
        }

    }

    fun enableAlarm() {
        isAlarmOn = true

    }

    fun disableAlarm() {
        isAlarmOn = false
    }

    fun toggleTransition(){
        fadeSystem?.toggleTransition()
    }

    fun isAlarmEnabled() = isAlarmOn
//TODO we'll show 1 generic alarm, but modify that alarm slightly (e.g. color) to indicate which type of alarm went off

    fun initializeBackground(vibratorService: Vibrator, paletteCallback: ((Palette) -> Any?)? = null) {
        vibrator = vibratorService

        mBackgroundPaint = Paint().apply {
            color = Color.BLACK
        }

        mMorphPaint = Paint().apply {

            color = Color.BLACK
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
     * delta time is in seconds!
     */
    fun drawAlarm(canvas: Canvas,deltaTimeMillis:Float){

        renderSystem?.canvas = canvas
        engine.update(deltaTimeMillis/1000)
    }


    /**
     * Draw the background. There are 3 main steps:
     * 1. Draw the morphed background
     * 2. Draw the unmorphed background
     * 3. Draw Features
     */
    fun drawBackground(canvas: Canvas,
                       mAmbient: Boolean,
                       mLowBitAmbient: Boolean,
                       mBurnInProtection: Boolean,
                       bounds: Rect, vararg timers:CircularTimer) {

        dt = (SystemClock.uptimeMillis() - lastMeasuredTime) //first dt will be 0


        if (mAmbient && (mLowBitAmbient || mBurnInProtection)) {
            canvas.drawColor(Color.BLACK)
        } else if (mAmbient) {
            canvas.drawBitmap(mGrayBackgroundBitmap, 0f, 0f, mBackgroundPaint)
        } else {

            //add a smooth transition
            timers.forEach {

                //TODO UNCOMMENT
              /*  if (it.isRunning()) {

                    morphPath.reset()
                    //move to the center
                    morphPath.moveTo(bounds.width().div(CircularTimer.TWO), bounds.height().div(CircularTimer.TWO))
                    morphPath.lineTo(bounds.width().div(CircularTimer.TWO), 0.0f)
                    morphPath.arcTo(0.0f, 0.0f, bounds.width().toFloat(), bounds.height().toFloat(), it.startAngle, it.sweepAngle, true)
                    morphPath.lineTo(bounds.width().div(CircularTimer.TWO), bounds.height().div(CircularTimer.TWO))

                    //TODO configure to work with multiple timers
                    workingCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                    mMorphPaint.color = Color.RED
                    mMorphPaint.xfermode = baseDrawingMode
                    workingCanvas.drawPath(morphPath, mMorphPaint)
                    mMorphPaint.xfermode = morphDrawingMode
                    //draw morph background
                    workingCanvas.drawBitmap(morphedBitmap, 0.0f, 0.0f, mMorphPaint)
                    mMorphPaint.xfermode = bgDrawingMode

                }else{
                    mMorphPaint.xfermode = baseDrawingMode
                }*/
            }
            //TODO delete this
            mMorphPaint.xfermode = baseDrawingMode

            workingCanvas.drawBitmap(mBackgroundBitmap, 0.0f, 0.0f, mMorphPaint)
            canvas.drawBitmap(workingBitmap, 0.0f, 0.0f, mBackgroundPaint)

            canvas.save()
            canvas.scale(scale, scale)

            if (isAlarmOn) {


                if (isAlarmAlphaIncreasing) {

                    currentTimeCounter += dt
                    if (currentTimeCounter > 1000.0) {
                        currentTimeCounter = 1000
                        isAlarmAlphaIncreasing = false
                    }
                } else {
                    currentTimeCounter -= dt
                    if (currentTimeCounter < 0.0) {
                        currentTimeCounter = 0
                        isAlarmAlphaIncreasing = true

                        //sound a vibration
                        vibrator.vibrate(vibrationEffect)
                    }
                }
                //Log.d("$tag drawBackground","Alpha $currentAlarmAlpha")
                currentAlarmAlpha = (currentTimeCounter.toFloat().div(TIME2MAX) * MAXALPHA)
                if (currentAlarmAlpha > 255.0) currentAlarmAlpha = 255f else if (currentAlarmAlpha < 0) {
                    currentAlarmAlpha = 0f
                }

                mAlarmPaint.alpha = currentAlarmAlpha.roundToInt()

                //TODO find performance between drawing another image on top and drawing these lines
                triangleSoup?.forEach {

                    //TODO don't draw duplicate triangles
                    canvas.drawLine(it.a.x.toFloat(), it.a.y.toFloat(), it.b.x.toFloat(), it.b.y.toFloat(), mAlarmPaint)
                    canvas.drawLine(it.b.x.toFloat(), it.b.y.toFloat(), it.c.x.toFloat(), it.c.y.toFloat(), mAlarmPaint)
                    canvas.drawLine(it.c.x.toFloat(), it.c.y.toFloat(), it.a.x.toFloat(), it.a.y.toFloat(), mAlarmPaint)
                }
            }
            canvas.restore()
        }

        if ((lastMeasuredTime == 0L) or (dt > 100)) {
            lastMeasuredTime = SystemClock.uptimeMillis()
            return
        }

        canvas.save()
        canvas.scale(scale,scale)
        drawAlarm(canvas,dt.toFloat())
        canvas.restore()
        lastMeasuredTime = SystemClock.uptimeMillis()
    }

    fun scaleBackground(width: Int, height: Int) {

        scale = width.toFloat() / mBackgroundBitmap.width.toFloat()

        mBackgroundBitmap = Bitmap.createScaledBitmap(mBackgroundBitmap,
                (mBackgroundBitmap.width * scale).toInt(),
                (mBackgroundBitmap.height * scale).toInt(), true)

        //TODO may delete this
/*        morphedBitmap = Bitmap.createScaledBitmap(morphedBitmap,
                (morphedBitmap.width * scale).toInt(),
                (morphedBitmap.height * scale).toInt(), true)*/

        workingBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        workingCanvas = Canvas(workingBitmap)


        //prepare ashley
        engine.apply {
            fadeSystem =VectorFadeSystem()
            renderSystem = RenderSystem()
            addSystem(fadeSystem)
            addSystem(renderSystem)
            addSystem(FadeOutSystem())
        }
    }

    /* Check whether segment P0P1 intersects with triangle t0t1t2 */

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

/*
    private fun generateWaterColorBackground(): Bitmap {
        val height = 200.0f
        val width = 200.0f
        val max_radius = 40.0f
        val intermidiateBitmap = Bitmap.createBitmap(width.toInt(), height.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(intermidiateBitmap)

        val alpha = 40.0f

        canvas.drawColor(Color.WHITE)

        val painter = Paint().apply {
            style = Paint.Style.FILL
            isAntiAlias = true
        }


        //we'll make a gradient of 4 colors for now,

        val random = Random()
        for (i in 0..250) {

            val x = random.nextFloat() * width
            val y = random.nextFloat() * height
            var radius = random.nextFloat() * max_radius + 10.0f

            if (radius < 0.0) radius = 0.0f

            val colorLeft = topLeft.lerp(bottomLeft, y / height, lab)
            val colorRight = topRight.lerp(bottomRight, y / height, lab)
            val final = colorLeft.lerp(colorRight, x / width, lab)
            painter.color = Color.argb(final.alpha().roundToInt(), final.red().roundToInt(), final.green().roundToInt(), final.blue().roundToInt())

            canvas.drawCircle(x, y, radius, painter)
        }

        val finalBitmap = intermidiateBitmap.copy(Bitmap.Config.ARGB_8888, false)
        intermidiateBitmap.recycle()
        return finalBitmap

    }
*/

    private fun generateBitmapFromTriangles(width: Double, height: Double, triangles: List<Triangle2D>): Bitmap {

        //we'll make a gradient of 4 colors for now,
        val intermidiateBitmap = Bitmap.createBitmap(width.toInt(), height.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(intermidiateBitmap)

        var centerX: Double
        var centerY: Double
        val path = Path()
        path.fillType = Path.FillType.EVEN_ODD

        val painter = Paint().apply {

            style = Paint.Style.FILL_AND_STROKE
            isAntiAlias = true
            strokeWidth = 1.0f
            color = Color.WHITE

        }

        canvas.drawColor(Color.WHITE)
        triangles.forEach {

            //                    Log.d("$tag generateBackgroundBitmaps","Triangle: A(${it.a.x},${it.a.y}) B(${it.b.x},${it.b.y}) C(${it.c.x},${it.c.y})")

            //first find the Center Coordinates
            centerX = (it.qA().x + it.qB().x + it.qC().x).div(3.0)
            centerY = (it.qA().y + it.qB().y + it.qC().y).div(3.0)

//                    Log.d("$tag generateBackgroundBitmaps","Centroid: $centerX, $centerY")

            //now use the coordinates to locate the correct color
            painter.color = getColor(centerX.toFloat(),centerY.toFloat(),width.toFloat(),height.toFloat())

            //draw the centroids
            //  canvas.drawPoint(centerX.toFloat(),centerY.toFloat(),painter)

            //now we draw the triangle
            path.moveTo(it.qA().x.toFloat(), it.qA().y.toFloat())
            path.lineTo(it.qB().x.toFloat(), it.qB().y.toFloat())
            path.lineTo(it.qC().x.toFloat(), it.qC().y.toFloat())
            path.lineTo(it.qA().x.toFloat(), it.qA().y.toFloat())

            canvas.drawPath(path, painter)
            path.reset()
        }

        val finalBitmap = intermidiateBitmap.copy(Bitmap.Config.ARGB_8888, false)
        intermidiateBitmap.recycle()
        return finalBitmap

    }

    private fun generateBackgroundBitmaps() {

        //we'll create delayney triangles
        val points = 61

        val random = Random()

        //initialize an empty array of floating points to mark the vertices of our triangles
        val point2ds = Vector<Vector2D>(points)

        //place points on the corners of our quad
        point2ds.addElement(Vector2D(0.0, 0.0)) //top left
        point2ds.addElement(Vector2D(widthD, heightD)) // bottom right
        point2ds.addElement(Vector2D(0.0, heightD)) // bottom left
        point2ds.addElement(Vector2D(widthD, 0.0)) //top right

        val halfWidth = widthD.times(0.5)
        val halfHeight = heightD.times(0.5)

        for (i in 4 until points) {

            //place points on the edges of the quad
            if (i < 8) {
                //left edge
                point2ds.addElement(Vector2D(0.0, random.nextDouble() * heightD))
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
                point2ds.addElement(Vector2D(random.nextDouble() * halfWidth, (random.nextDouble() * halfHeight) + halfHeight))
            } else if (i < 50) {
                //top right quadrant
                point2ds.addElement(Vector2D((random.nextDouble() * halfWidth) + halfWidth, (random.nextDouble() * halfHeight + halfHeight)))
            } else if (i < 60) {
                //bottom right quadrant
                point2ds.addElement(Vector2D((random.nextDouble() * halfWidth) + halfWidth, random.nextDouble() * halfHeight))
            }
        }

        triangleSoup = try {
            triangulator = DelaunayTriangulator(point2ds)
            triangulator.triangulate()
            triangulator.triangles as ArrayList<Triangle2D>

        } catch (e: NotEnoughPointsException) {
            Log.d("$tag generateBackgroundBitmaps", "Woops Triangulation")
            null
        }

        if (triangleSoup == null) {
            return
        }

        mBackgroundBitmap = generateBitmapFromTriangles(widthD, heightD, triangleSoup!!)

        //TODO START OF MORPHED BG

        val edges = ArrayList<EdgeEntity>()
        val triangleNodes = ArrayList<TriangleEntity>()
//        val edgeNodes = ArrayList<EdgeEntity>()

        var noABFound = false
        var noACFound = false
        var noBCFound = false

        triangulator.triangleSoup.triangles.forEach{ triangle ->

            //We'll create a triangle node so that we know which edges belong to which triangle
            //This way we can avoid searching for it, and just remember it. saves CPU usage
            // but takes up memory
            val triEntity = TriangleEntity(triangle)

            //First triangle
            //TODO we can optimize this slightly by using vector notation instead.
            //check that any of our current edges matches with any triangle's edge

            if (edges.size == 0) {
                val edgeAB = Edge2D(Vector2D(triangle.a.x, triangle.a.y), Vector2D(triangle.b.x, triangle.b.y))
                triEntity.edgeEntityAB = EdgeEntity(edgeAB).apply {
                    parent = triEntity
                    edges.add(this@apply)
                }
            }

            loop@ for (i in 0 until edges.size) {

                val edge = edges.get(i).itself
                if (!((edge.a.x == triangle.a.x) and (edge.a.y == triangle.a.y) and (edge.b.x == triangle.b.x) and (edge.b.y == triangle.b.y))) {
                    noABFound = true
                    break@loop
                }
            }
            if (noABFound) {
                val edgeAB = Edge2D(Vector2D(triangle.a.x, triangle.a.y), Vector2D(triangle.b.x, triangle.b.y))
                triEntity.edgeEntityAB = EdgeEntity(edgeAB).apply {
                    parent = triEntity
                    edges.add(this@apply)
                }
            }

            loop@ for (i in 0 until edges.size) {

                val edge = edges.get(i).itself
                if (((edge.a.x == triangle.a.x) and (edge.a.y == triangle.a.y) and (edge.b.x == triangle.c.x) and (edge.b.y == triangle.c.y))) {
                    noACFound = true
                    break@loop
                }
            }

            if (noACFound) {
                val edgeAC =Edge2D(Vector2D(triangle.a.x, triangle.a.y), Vector2D(triangle.c.x, triangle.c.y))
                triEntity.edgeEntityAC = EdgeEntity(edgeAC).apply {
                    parent = triEntity
                    edges.add(this@apply)
                }
            }

            loop@ for (i in 0 until edges.size) {

                val edge = edges.get(i).itself
                if (!((edge.a.x == triangle.b.x) and (edge.a.y == triangle.b.y) and (edge.b.x == triangle.c.x) and (edge.b.y == triangle.c.y))) {
                    noBCFound = true
                    break@loop
                }
            }

            if (noBCFound) {
                val edgeBC =Edge2D(Vector2D(triangle.b.x, triangle.b.y), Vector2D(triangle.c.x, triangle.c.y))
                triEntity.edgeEntityBC = EdgeEntity(edgeBC).apply {
                    parent =triEntity
                    edges.add( this@apply)
                }
            }

            //before we add this triangle to the list of triangle nodes, complete any missing information
            //On nodes we already contain as well as on on the nodes of this triangle node.

            //we'll start with the triangles nodes that already exist.
            //for each triangle, check that one of our nodes is part of that triangle's edge nodes
            triangleNodes.forEach {otherTriangleEntities ->

               //for each triangle, check if that triangle is neighbour to this triangle.
                //check that we haven't found a neighbour first
                triEntity.edgeEntityAB?.let {

                    if (triEntity.edgeEntityAB!!.neighbour == null) {

                        if (otherTriangleEntities.itself.isNeighbour(triEntity.edgeEntityAB!!.itself)) {

                            //lets make this triNode a neighbour of our current triangle
                            triEntity.edgeEntityAB!!.neighbour = otherTriangleEntities

                            //we may as well update the neighbour on triNode as well
                            otherTriangleEntities.edgeEntityAB?.let {

                                if (otherTriangleEntities.edgeEntityAB!!.neighbour == null) {
                                    if (triEntity.itself.isNeighbour(otherTriangleEntities.edgeEntityAB!!.itself)) {
                                        otherTriangleEntities.edgeEntityAB!!.neighbour = triEntity
                                    }
                                }
                            }
                            otherTriangleEntities.edgeEntityAC?.let {

                                if (otherTriangleEntities.edgeEntityAC!!.neighbour == null) {
                                    if (triEntity.itself.isNeighbour(otherTriangleEntities.edgeEntityAC!!.itself)) {
                                        otherTriangleEntities.edgeEntityAC!!.neighbour = triEntity
                                    }
                                }
                            }

                            otherTriangleEntities.edgeEntityBC?.let {
                                if (otherTriangleEntities.edgeEntityBC!!.neighbour == null) {
                                    if (triEntity.itself.isNeighbour(otherTriangleEntities.edgeEntityBC!!.itself)) {
                                        otherTriangleEntities.edgeEntityBC!!.neighbour = triEntity
                                    }
                                }

                            }
                        }
                    }
                }

                //repeat for the other edges of this triangle
                triEntity.edgeEntityAC?.let {
                    if (triEntity.edgeEntityAC!!.neighbour == null) {

                        if (otherTriangleEntities.itself.isNeighbour(triEntity.edgeEntityAC!!.itself)) {

                            //lets make this triNode a neighbour of our current triangle
                            triEntity.edgeEntityAC!!.neighbour = otherTriangleEntities


                            //we may as well update the neighbour on triNode as well
                            otherTriangleEntities.edgeEntityAB?.let {

                                if (otherTriangleEntities.edgeEntityAB!!.neighbour == null) {
                                    if (triEntity.itself.isNeighbour(otherTriangleEntities.edgeEntityAB!!.itself)) {
                                        otherTriangleEntities.edgeEntityAB!!.neighbour = triEntity
                                    }
                                }
                            }
                            otherTriangleEntities.edgeEntityAC?.let {

                                if (otherTriangleEntities.edgeEntityAC!!.neighbour == null) {
                                    if (triEntity.itself.isNeighbour(otherTriangleEntities.edgeEntityAC!!.itself)) {
                                        otherTriangleEntities.edgeEntityAC!!.neighbour = triEntity
                                    }
                                }
                            }

                            otherTriangleEntities.edgeEntityBC?.let {

                                if (otherTriangleEntities.edgeEntityBC!!.neighbour == null) {
                                    if (triEntity.itself.isNeighbour(otherTriangleEntities.edgeEntityBC!!.itself)) {
                                        otherTriangleEntities.edgeEntityBC!!.neighbour = triEntity
                                    }
                                }
                            }
                        }
                    }

                }

                //repeat for the other edges of this triangle
                triEntity.edgeEntityBC?.let {
                    if (triEntity.edgeEntityBC!!.neighbour == null) {

                        if (otherTriangleEntities.itself.isNeighbour(triEntity.edgeEntityBC!!.itself)) {

                            //lets make this triNode a neighbour of our current triangle
                            triEntity.edgeEntityBC!!.neighbour = otherTriangleEntities

                            //we may as well update the neighbour on triNode as well
                            otherTriangleEntities.edgeEntityAB?.let {
                                if (otherTriangleEntities.edgeEntityAB!!.neighbour == null) {
                                    if (triEntity.itself.isNeighbour(otherTriangleEntities.edgeEntityAB!!.itself)) {
                                        otherTriangleEntities.edgeEntityAB!!.neighbour = triEntity
                                    }
                                }

                            }

                            otherTriangleEntities.edgeEntityAC?.let {
                                if (otherTriangleEntities.edgeEntityAC!!.neighbour == null) {
                                    if (triEntity.itself.isNeighbour(otherTriangleEntities.edgeEntityAC!!.itself)) {
                                        otherTriangleEntities.edgeEntityAC!!.neighbour = triEntity
                                    }
                                }

                            }

                            otherTriangleEntities.edgeEntityBC?.let {
                                if (otherTriangleEntities.edgeEntityBC!!.neighbour == null) {
                                    if (triEntity.itself.isNeighbour(otherTriangleEntities.edgeEntityBC!!.itself)) {
                                        otherTriangleEntities.edgeEntityBC!!.neighbour = triEntity
                                    }
                                }

                            }
                        }
                    }
                }

            }

            //now add this triangle to our triangle nodes
            triangleNodes.add(triEntity)
        }

        edges.forEach { edgeEntity ->

            edgeEntity.add(RenderComponent())
            edgeEntity.add(EdgeComponent())
            edgeEntity.add(AlphaComponent().apply {

                delaySecond = ((Math.min(edgeEntity.itself.a.x, edgeEntity.itself.b.x) / widthD) * 0.5)
                alpha = 0
                realDelayTime = 0.0f
            })
            engine.addEntity(edgeEntity)
        }

        triangleNodes.forEach {
            engine.addEntity(it)
        }


        //we have collected our edges to draw, now we must draw the white triangle when All 3 edges are fully at full alpha.


        //generate Morphed background
        //if image is square, height = with, so
        //radius = % * width /2

        //100 - 15 / 100 =

        //TODO start of MORPHED BG
/*
        val radius = (WORKING_BITMAP_WIDTH.div(2.0) - 15.0).toFloat()
        //now find all the triangles that intersect with this circle, we do this by dividing the circle into tangents
        //60 of them since 360/12 = 30 sections
        val xCenter = WORKING_BITMAP_WIDTH.div(2.0)
//        var yCenter = WORKING_BITMAP_HEIGHT.div(2.0)

        val degreesPerSection = 12.0
        val numSections = (360.0 / degreesPerSection).roundToInt()

        for(pos in 1..numSections){
//        circleTangents = List(numSections) { pos ->

            //the first tangent is the line between 0 and 6 degrees:
            val end = if (pos < numSections - 1) {
//                start = Vector2D((radius * sin(Math.toRadians(pos * degreesPerSection))) + xCenter,
// (radius * -cos(Math.toRadians(pos * degreesPerSection))) + xCenter)
                Vector2D((radius * sin(Math.toRadians((pos + 1) * degreesPerSection))) + xCenter,
                        (radius * -cos(Math.toRadians((pos + 1) * degreesPerSection))) + xCenter)
            } else {
//                start = Vector2D((radius * sin(Math.toRadians(pos * degreesPerSection))) + xCenter
// radius * -cos(Math.toRadians(pos * degreesPerSection)) + xCenter)
                Vector2D((radius * sin(Math.toRadians(0.0))) + xCenter,
                        (radius * -cos(Math.toRadians(0.0))) + xCenter)
            }
            point2ds.add(end)
        }

        triangulator = DelaunayTriangulator(point2ds)
        triangulator.triangulate()

        //We morphed the background slightly, now lets create the bitmap for it.
        morphedBitmap = generateBitmapFromTriangles(widthD, heightD, triangulator.triangles as ArrayList<Triangle2D>)
*/

        //TODO END OF MORPHED
    }


    companion object {
        /**
         * This is pre-allocated memory to calculate colors
         */
        private val colorLeft by lazy { CIEColor(0f,0f,0f,0f) }
        private val colorRight by lazy { CIEColor(0f,0f,0f,0f) }
        private val topLeft by lazy { CIEColor(0f,0f,0f,0f) }
        private val topRight by lazy { CIEColor(0f,0f,0f,0f) }
        private val bottomLeft by lazy { CIEColor(0f,0f,0f,0f) }
        private val bottomRight by lazy { CIEColor(0f,0f,0f,0f) }
        private val final by lazy { CIEColor(0f,0f,0f,0f) }

    }

    fun getColor(x:Float,y:Float,width:Float,height:Float):Int{
        topLeft.lerp(bottomLeft, y / height, colorLeft)
        topRight.lerp(bottomRight, y / height, colorRight)
        colorLeft.lerp(colorRight, x / width, final)

        return Color.argb(final.a.roundToInt(), final.r.roundToInt(), final.g.roundToInt(), final.b.roundToInt())
    }

    //TODO make sure we remove the properties and make them into components later
    class EdgeEntity(var itself: Edge2D):Entity(){

        var parent:TriangleEntity? = null
        var neighbour:TriangleEntity? = null
        var latch = false

        companion object {

            val paint =  Paint().apply {
                strokeWidth = 1.0f
                color = Color.WHITE
                isAntiAlias = true
            }

            val renderFunction:(Canvas,EdgeEntity)->Any = { canvas, entity ->
                entity.getComponent(AlphaComponent::class.java).apply {
                    if (alpha > 0) {
                        val edge = entity.itself
                        paint.alpha = alpha
                        canvas.drawLine(
                                edge.a.x.toFloat(),
                                edge.a.y.toFloat(),
                                edge.b.x.toFloat(),
                                edge.b.y.toFloat(), paint)
                    }
                }


            }
        }
    }

    class TriangleEntity(val itself:Triangle2D):Entity(){

        var edgeEntityAB: EdgeEntity? = null
        var edgeEntityAC: EdgeEntity? = null
        var edgeEntityBC: EdgeEntity? = null
        var latch = false

       companion object {

           val paint= Paint().apply {
               color = Color.WHITE
               style = Paint.Style.FILL
           }
           val path = Path()
           val renderFunction:(Canvas, TriangleEntity)->Any = { canvas, entity ->

               path.reset()
               path.moveTo(entity.itself.qA().x.toFloat(), entity.itself.qA().y.toFloat())
               path.lineTo(entity.itself.qB().x.toFloat(), entity.itself.qB().y.toFloat())
               path.lineTo(entity.itself.qC().x.toFloat(), entity.itself.qC().y.toFloat())
               path.lineTo(entity.itself.qA().x.toFloat(), entity.itself.qA().y.toFloat())

               paint.alpha = entity.getComponent(AlphaV2Component::class.java).alpha
               canvas.drawPath(path, paint)
           }
       }
    }
}
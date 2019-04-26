package com.gorillamoa.routines.views

import android.graphics.*
import android.util.Log
import androidx.palette.graphics.Palette
import com.gorillamoa.routines.utils.lerp
import io.github.jdiemke.triangulation.DelaunayTriangulator
import io.github.jdiemke.triangulation.NotEnoughPointsException
import io.github.jdiemke.triangulation.Triangle2D
import io.github.jdiemke.triangulation.Vector2D
import java.util.*
import kotlin.math.roundToInt

class LivingBackground():Drawable{

    @Suppress("unused")
    private val tag:String = LivingBackground::class.java.name

    private lateinit var mBackgroundPaint: Paint
    private lateinit var mBackgroundBitmap: Bitmap
    private lateinit var mGrayBackgroundBitmap: Bitmap

    private lateinit var palette: Palette




    fun initializeBackground(paletteCallback:((Palette)->Any?)? = null ) {
        mBackgroundPaint = Paint().apply {
            color = Color.BLACK
        }

        mBackgroundBitmap = generateDelauneyBackgroundImage()

        /* Extracts colors from background image to improve watchface style. */
        Palette.from(mBackgroundBitmap).generate {
            it?.apply {
                palette = it
                paletteCallback?.invoke(it)
            }
        }.get()
    }

    fun getPalette() = palette





    fun drawBackground(canvas: Canvas, mAmbient:Boolean, mLowBitAmbient:Boolean, mBurnInProtection:Boolean) {

        if (mAmbient && (mLowBitAmbient || mBurnInProtection)) {
            canvas.drawColor(Color.BLACK)
        } else if (mAmbient) {
            canvas.drawBitmap(mGrayBackgroundBitmap, 0f, 0f, mBackgroundPaint)
        } else {
            canvas.drawBitmap(mBackgroundBitmap, 0f, 0f, mBackgroundPaint)
        }
    }


    override fun draw(canvas: Canvas) {

    }

    fun scaleBackground(width:Int,height:Int){

        val scale = width.toFloat() / mBackgroundBitmap.width.toFloat()

        mBackgroundBitmap = Bitmap.createScaledBitmap(mBackgroundBitmap,
                (mBackgroundBitmap.width * scale).toInt(),
                (mBackgroundBitmap.height * scale).toInt(), true)
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
        val lab = ColorSpace.get(ColorSpace.Named.CIE_LAB)
        val alpha = 40.0f

        canvas.drawColor(Color.WHITE)

        val painter =Paint().apply {
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        //TODO look up how to spread colors better
        //we'll make a gradient of 4 colors for now,
        val topLeft = Color.valueOf(255.0f,245.0f,235.0f,alpha,lab)
        val bottomRight = Color.valueOf(127.0f,39.0f,4.0f, alpha,lab)
        val bottomLeft = Color.valueOf(175.0f,111.0f,84.0f, alpha,lab)
        val topRight = Color.valueOf(175.0f,111.0f,84.0f,alpha,lab)

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

    private fun generateDelauneyBackgroundImage():Bitmap {

        val height = 200.0f
        val width = 200.0f
        val intermidiateBitmap = Bitmap.createBitmap(width.toInt(),height.toInt(),Bitmap.Config.ARGB_8888)
        val canvas = Canvas(intermidiateBitmap)
        val lab = ColorSpace.get(ColorSpace.Named.CIE_LAB)
        val alpha = 255.0f
//            val alpha = 10.0f


        //draw .. lets say.. 200 circles on this white canvas
        canvas.drawColor(Color.WHITE)

        val painter =Paint().apply {
            style = Paint.Style.FILL_AND_STROKE
            isAntiAlias = true
            strokeWidth = 2.0f
        }

        //TODO look up how to spread colors better
        //we'll make a gradient of 4 colors for now,
        val topLeft = Color.valueOf(255.0f,245.0f,235.0f,alpha,lab)
        val bottomRight = Color.valueOf(127.0f,39.0f,4.0f, alpha,lab)
        val bottomLeft = Color.valueOf(175.0f,111.0f,84.0f, alpha,lab)
        val topRight = Color.valueOf(175.0f,111.0f,84.0f,alpha,lab)

        //we'll create delayney triangles
        val POINTS =61

        val random = Random()

        //initialize an empty array of floating points to mark the vertices of our triangles
        val point2ds = Vector<Vector2D>(POINTS)

        //place points on the corners of our quad
        point2ds.addElement(Vector2D(0.0,0.0))
        point2ds.addElement(Vector2D(width.toDouble(),height.toDouble()))
        point2ds.addElement(Vector2D(0.0,height.toDouble()))
        point2ds.addElement(Vector2D(width.toDouble(),0.0))

        val halfWidth = width.toDouble().times(0.5)
        val halfHeight = height.toDouble().times(0.5)

        for (i in 4 until POINTS) {

            //place points on the edges of the quad
            if (i < 8) {
                //left edge
                point2ds.addElement(Vector2D(  0.0, random.nextDouble() * height))
            } else if (i < 12) {
                //top edge
                point2ds.addElement(Vector2D((width.toDouble() * random.nextDouble()), height.toDouble()))
            } else if (i < 16) {
                //right edge
                point2ds.addElement(Vector2D(width.toDouble(), random.nextDouble() * height.toDouble()))
            } else if (i < 20) {
                //bottom edge
                point2ds.addElement(Vector2D(width.toDouble() * random.nextDouble(), 0.0))

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

        val triangleSoup:List<Triangle2D>? = try {
            val delaunayTriangulator = DelaunayTriangulator(point2ds)
            delaunayTriangulator.triangulate()
            delaunayTriangulator.triangles as List<Triangle2D>

        } catch (e: NotEnoughPointsException) {
            Log.d("$tag generateDelauneyBackgroundImage","Woops Triangulation")
            null

        }

        var centerX:Double
        var centerY:Double
        val path=Path()
        path.fillType = Path.FillType.EVEN_ODD

        triangleSoup?.let {

            //TODO work with floats because we don't need double precision
            triangleSoup.forEach {

                //                    Log.d("$tag generateDelauneyBackgroundImage","Triangle: A(${it.a.x},${it.a.y}) B(${it.b.x},${it.b.y}) C(${it.c.x},${it.c.y})")

                //first find the Center Coordinates
                centerX = (it.a.x + it.b.x + it.c.x).div(3.0)
                centerY = (it.a.y + it.b.y + it.c.y).div(3.0)

//                    Log.d("$tag generateDelauneyBackgroundImage","Centroid: $centerX, $centerY")

                //now use the coordinates to locate the correct color
                val colorLeft = topLeft.lerp(bottomLeft,centerY.toFloat()/height,lab)
                val colorRight = topRight.lerp(bottomRight,centerY.toFloat()/height,lab)
                val final =  colorLeft.lerp(colorRight,centerX.toFloat()/width,lab)
                painter.color = Color.argb(final.alpha().roundToInt(),final.red().roundToInt(), final.green().roundToInt(), final.blue().roundToInt())


                //draw the centroids
                //  canvas.drawPoint(centerX.toFloat(),centerY.toFloat(),painter)

                //now we draw the triangle
                path.moveTo(it.a.x.toFloat(),it.a.y.toFloat())
                path.lineTo(it.b.x.toFloat(),it.b.y.toFloat())
                path.lineTo(it.c.x.toFloat(),it.c.y.toFloat())
                path.lineTo(it.a.x.toFloat(),it.a.y.toFloat())

                canvas.drawPath(path,painter)
                path.reset()
            }
        }
        //Determine the center position of each triangle

//                canvas.drawCircle(x,y,radius,painter)


        val finalBitmap = intermidiateBitmap.copy(Bitmap.Config.ARGB_8888,false)
//            intermidiateBitmap.recycle()
        return finalBitmap
    }


}
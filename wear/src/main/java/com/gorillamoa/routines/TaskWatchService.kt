package com.gorillamoa.routines

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.SystemClock
import androidx.palette.graphics.Palette
import android.support.wearable.watchface.CanvasWatchFaceService
import android.support.wearable.watchface.WatchFaceService
import android.support.wearable.watchface.WatchFaceStyle
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import android.view.SurfaceHolder
import com.gorillamoa.routines.data.Task
import com.gorillamoa.routines.extensions.*
import com.gorillamoa.routines.receiver.AlarmReceiver
import com.gorillamoa.routines.receiver.AlarmReceiver.Companion.ACTION_REST
import com.gorillamoa.routines.receiver.AlarmReceiver.Companion.ACTION_TIMER
import com.gorillamoa.routines.scheduler.TaskScheduler
import com.gorillamoa.routines.views.CanvasButton
import com.gorillamoa.routines.views.ClickableRectangle
import com.gorillamoa.routines.views.SwitchingButton
import com.gorillamoa.routines.views.TimerView

import java.lang.ref.WeakReference
import java.util.*
import kotlin.math.roundToInt

/**
 * Updates rate in milliseconds for interactive mode. We update once a second to advance the
 * second hand.
 */
private const val INTERACTIVE_UPDATE_RATE_MS = 1000

/**
 * Handler message id for updating the time periodically in interactive mode.
 */
private const val MSG_UPDATE_TIME = 0

private const val HOUR_STROKE_WIDTH = 5f
private const val MINUTE_STROKE_WIDTH = 3f
private const val SECOND_TICK_STROKE_WIDTH = 2f

private const val CENTER_GAP_AND_CIRCLE_RADIUS = 4f

private const val SHADOW_RADIUS = 6f

private const val PERCENT_OF_RADIUS = 0.7

private const val COMPLETE = "complete"
private const val INCOMPLETE = "incomplete"


/**
 * Analog watch face with a ticking second hand. In ambient mode, the second hand isn't
 * shown. On devices with low-bit ambient mode, the hands are drawn without anti-aliasing in ambient
 * mode. The watch face is drawn with less contrast in mute mode.
 *
 *
 * Important Note: Because watch face apps do not have a default Activity in
 * their project, you will need to set your Configurations to
 * "Do not launch Activity" for both the Wear and/or Application modules. If you
 * are unsure how to do this, please review the "Run Starter project" section
 * in the Google Watch Face Code Lab:
 * https://codelabs.developers.google.com/codelabs/watchface/index.html#0
 */
class TaskWatchService : CanvasWatchFaceService() {

    //TODO create pager like effect (or other animation) when switching tasks

    override fun onCreateEngine(): Engine {
        return Engine()
    }

    private class EngineHandler(reference: TaskWatchService.Engine) : Handler() {
        private val mWeakReference: WeakReference<TaskWatchService.Engine> = WeakReference(reference)

        override fun handleMessage(msg: Message) {
            val engine = mWeakReference.get()
            if (engine != null) {
                when (msg.what) {
                    MSG_UPDATE_TIME -> engine.handleUpdateTimeMessage()
                }
            }
        }
    }


    inner class Engine : CanvasWatchFaceService.Engine() {
        private val tag:String = Engine::class.java.name

        private lateinit var mCalendar: Calendar

        private var mRegisteredTimeZoneReceiver = false
        private var mMuteMode: Boolean = false
        private var mCenterX: Float = 0F
        private var mCenterY: Float = 0F



        private var mSecondHandLength: Float = 0F
        private var sMinuteHandLength: Float = 0F
        private var sHourHandLength: Float = 0F

        /* Colors for all hands (hour, minute, seconds, ticks) based on photo loaded. */
        private var mWatchHandColor: Int = 0
        private var mWatchHandHighlightColor: Int = 0
        private var mWatchHandShadowColor: Int = 0

        private lateinit var mHourPaint: Paint
        private lateinit var mMinutePaint: Paint
        private lateinit var mSecondPaint: Paint
        private lateinit var mTickAndCirclePaint: Paint

        private lateinit var mBackgroundPaint: Paint
        private lateinit var mBackgroundBitmap: Bitmap
        private lateinit var mGrayBackgroundBitmap: Bitmap

        private var mAmbient: Boolean = false
        private var mLowBitAmbient: Boolean = false
        private var mBurnInProtection: Boolean = false

        /* Handler to update the time once a second in interactive mode. */
        private val mUpdateTimeHandler = EngineHandler(this)

        private var xLastTouch = 0
        private var yLastTouch = 0
        private var lastTimeTouch = 0L


        //Break hand color
        private lateinit var mBreakLinePaint:Paint

        //clean code TASK STUFF
        private lateinit var mTextPaint:TextPaint
        private var textHeight = 0f
        private var staticLayout: StaticLayout? = null
        private var textHalfWidth = 0f
        private var leftButton:CanvasButton? = null
        private var rightButton:CanvasButton? = null
        private var centerButton:SwitchingButton? = null

        private val touchables= ArrayList<ClickableRectangle>()



        //todo save selected Minute and break Interval
        //clean breaks stuff into their own class
        private var selectedMinute = 0
        private var breakInterval:Int = 20

        private var lines = 0
        private var breakIntervalDegree = 0f
        private var mSelectedMinuteDegree = 0f
        private var middleSectionRadius = 0f
        private var isRestAlarmEnabled = false
        private var isTimerEnabled = false

        private var switchingButton:SwitchingButton? = null

        private val STATE_BREAKS = "break"
        private val STATE_TIMER = "time"
        private val STATE_ALARM = "alarm"

        //clean the timer stuff into its own class
        private lateinit var timerView:TimerView

        //TODO we'll show 1 generic alarm, but modify that alarm slightly (e.g. color) to indicate which type of alarm went off
        private var alarmTriggered = false

        //create a shared preference listener so that we can update the watchface UI when
        //changes to preference variables occur
         private val preferenceListener= SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->

            if (key == isRestAlarmActive) {

                if (!hasTouchedScreenRecently()) {
                    if(sharedPreferences.getBoolean(key,false)) enableRestPeriods() else disableRestPeriods()
                }
            }

            //TODO ADD A REST ALARM TRIGGER RESPONSE TO THE BREAKS

            if (key == isTimerAlarmTriggered) {

                //we're just going to fire off
                if (sharedPreferences.getBoolean(key, false)) {
                    alarmTriggered =true
                }

            }

        }

        private val mTimeZoneReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                mCalendar.timeZone = TimeZone.getDefault()
                invalidate()
            }
        }


        override fun onCreate(holder: SurfaceHolder) {
            super.onCreate(holder)

            setWatchFaceStyle(WatchFaceStyle.Builder(this@TaskWatchService)
                    .setAcceptsTapEvents(true)
                    .build())

            mCalendar = Calendar.getInstance()

            isRestAlarmEnabled = isRestAlarmActive()
            isTimerEnabled = isTimerAlarmActive()

            TaskScheduler.getNextUncompletedTask(this@TaskWatchService){ task -> configureTaskUI(task) }

            initializeBackground()
            initializeWatchFace()
            initializeFeatures(selectedMinute)

            applicationContext.getLocalSettings().registerOnSharedPreferenceChangeListener(preferenceListener)
        }

        private fun measureFeatures(){
            timerView = TimerView(mCenterX.toInt(),mCenterY.toInt(),mCenterX.toInt() - 30)



        }

        /**
         * Note, we need to ensure that our dimensions are initialized. So when we create these features
         * they are given correct values
         */
        private fun measureTouchables(screenWidth:Int, screenHeight:Int) {
//            ClickableRectangle.enableDebug()

            switchingButton = SwitchingButton(
                    mCenterX.toInt(),
                    mCenterY.toInt() + (screenHeight * 0.2f).toInt(),
                    (screenWidth  * 0.18).toInt(),
                    //use width to ensure we get a square object
                    (screenWidth  *   0.18).toInt(),
                    this@TaskWatchService).apply {
                onClickListener = {
                    nextState()
                    invalidate()

                }
                addState(STATE_BREAKS,R.drawable.ic_break_time)
                addState(STATE_TIMER,R.drawable.ic_hourglass)
                addState(STATE_ALARM,R.drawable.ic_alarm)
                touchables.add(this)
            }

            leftButton = CanvasButton((mCenterX - screenWidth * 0.2f).toInt() ,mCenterY.toInt(),(screenWidth*0.18f).toInt(),(screenWidth*0.18f).toInt()).apply {
                setImage(this@TaskWatchService,R.drawable.ic_left_skin_direction)
                onClickListener = {

                    TaskScheduler.getPreviousOrderedTask(this@TaskWatchService,currentTask?.id?:0){configureTaskUI(it)}
                }
                touchables.add(this)
            }
            rightButton = CanvasButton((mCenterX + screenWidth * 0.2f).toInt() ,mCenterY.toInt(),(screenWidth*0.18f).toInt(),(screenWidth*0.18f).toInt()).apply {
                setImage(this@TaskWatchService,R.drawable.ic_right_skin_arrow)
                onClickListener = {

                    TaskScheduler.getNextOrderedTask(this@TaskWatchService,currentTask?.id?:0){ configureTaskUI(it) }


                }
                touchables.add(this)
            }
            centerButton = SwitchingButton((mCenterX).toInt() ,(mCenterY).toInt(),(screenWidth*0.18f).toInt(),(screenWidth*0.18f).toInt(), this@TaskWatchService).apply {
                addState(INCOMPLETE,R.drawable.ic_radio_button_unchecked_black_24dp)
                addState(COMPLETE,R.drawable.ic_cc_checkmark)
                onClickListener = {
                    when (nextState()) {
                        INCOMPLETE ->{ TaskScheduler.uncompleteTask(this@TaskWatchService, currentTask?.id?:-1)}
                        COMPLETE ->{TaskScheduler.completeTask(this@TaskWatchService,currentTask?.id?:-1)}
                        else ->{ } // do nothing
                    }
                    invalidate()
                }
                touchables.add(this)
            }

            //measure text height
            textHeight = mCenterY - screenHeight*0.09f - 30f //some text height
        }

        /**
         * if the user touched the screen in the past 5 seconds
         */
        private fun hasTouchedScreenRecently():Boolean = (SystemClock.uptimeMillis() - lastTimeTouch) < 1500

        private fun enableRestPeriods(){
            isRestAlarmEnabled = true

            selectedMinute = if (hasTouchedScreenRecently()) {
                val radians = Math.atan2((xLastTouch - mCenterX).toDouble(), -(yLastTouch - mCenterY).toDouble())
                val angle: Double =
                        when (radians) {
                            in 0.0..Math.PI -> {
                                radians * 180 / Math.PI
                            }
                            else -> 180 * (1 + (1 - Math.abs(radians) / Math.PI))
                        }
                (angle / 6.0).roundToInt()
            }

            else{
                //activate in 18 minutes from now (it takes about 2 minutes to recognize the activity)
                (mCalendar.get(Calendar.MINUTE) + 18).rem(60)
            }

            initializeFeatures(selectedMinute)
            applicationContext.saveAlarmRestStatus(true)
        }

        private fun disableRestPeriods() {
            applicationContext.getAlarmService().cancel(getRestPendingIntent())
            isRestAlarmEnabled = false
            applicationContext.saveAlarmRestStatus(false)
        }

        /**
         * Enable a timer to go off in specified minutes
         * @param minutes is the amount of minutes until the alarm goes off
         */
        private fun enableTimer(minutes:Int){
            isTimerEnabled = true
            timerView.minute = minutes

            val timeToTrigger = System.currentTimeMillis() + (minutes * 60 * 1000) - (mCalendar.get(Calendar.SECOND) * 1000)

            //TODO MOVE THIS TO alarm extensions
            getAlarmService().set(
                    AlarmManager.RTC_WAKEUP,
                    timeToTrigger,
                    getTimerPendingIntent()
            )

            saveTimerTime(timeToTrigger)
        }

        private fun disableTimer(){
            timerView.reset()
            //TODO move this to alarm extensions
            applicationContext.getAlarmService().cancel(getTimerPendingIntent())
            isTimerEnabled = false
            applicationContext.saveAlarmTimerTriggerStatus(false)

        }

        private fun configureTaskUI(task:Task?){

            //create new text for the task name

          task?.apply {

              //Set the correct title
                val width = mTextPaint.measureText(name)
                textHalfWidth = (width * 0.5).toFloat()

                val sb = StaticLayout.Builder.obtain(name, 0, name.length, mTextPaint, width.toInt())
                        .setAlignment(Layout.Alignment.ALIGN_CENTER)
                        .setLineSpacing(0.0f, 1.0f)
                        .setIncludePad(false)
                staticLayout = sb.build()

              //make sure our complete button shows the correct status
              if (TaskScheduler.isComplete(this@TaskWatchService, task.id ?: -1)) {
                  centerButton?.setState(COMPLETE)
              }else centerButton?.setState(INCOMPLETE)

            }
            currentTask = task
            invalidate()

        }

        var currentTask:Task? = null


        private fun initializeBackground() {
            mBackgroundPaint = Paint().apply {
                color = Color.BLACK
            }
            mBackgroundBitmap = BitmapFactory.decodeResource(resources, R.drawable.bg)

            /* Extracts colors from background image to improve watchface style. */
            Palette.from(mBackgroundBitmap).generate {
                it?.let {
                    mWatchHandHighlightColor = it.getVibrantColor(Color.RED)
                    mWatchHandColor = it.getLightVibrantColor(Color.WHITE)
                    mWatchHandShadowColor = it.getDarkMutedColor(Color.BLACK)
                    updateWatchHandStyle()
                }
            }
        }

        private fun initializeWatchFace() {
            /* Set defaults for colors */
            mWatchHandColor = Color.WHITE
            mWatchHandHighlightColor = Color.RED
            mWatchHandShadowColor = Color.BLACK

            mHourPaint = Paint().apply {
                color = mWatchHandColor
                strokeWidth = HOUR_STROKE_WIDTH
                isAntiAlias = true
                strokeCap = Paint.Cap.ROUND
                setShadowLayer(
                        SHADOW_RADIUS, 0f, 0f, mWatchHandShadowColor)
            }

            mMinutePaint = Paint().apply {
                color = mWatchHandColor
                strokeWidth = MINUTE_STROKE_WIDTH
                isAntiAlias = true
                strokeCap = Paint.Cap.ROUND
                setShadowLayer(
                        SHADOW_RADIUS, 0f, 0f, mWatchHandShadowColor)
            }

            mSecondPaint = Paint().apply {
                color = mWatchHandHighlightColor
                strokeWidth = SECOND_TICK_STROKE_WIDTH
                isAntiAlias = true
                strokeCap = Paint.Cap.ROUND
                setShadowLayer(
                        SHADOW_RADIUS, 0f, 0f, mWatchHandShadowColor)
            }

            mTickAndCirclePaint = Paint().apply {
                color = mWatchHandColor
                strokeWidth = SECOND_TICK_STROKE_WIDTH
                isAntiAlias = true
                style = Paint.Style.STROKE
                setShadowLayer(
                        SHADOW_RADIUS, 0f, 0f, mWatchHandShadowColor)
            }

            //
            mBreakLinePaint = Paint().apply {
                color = Color.MAGENTA
                strokeWidth = 3f
                style = Paint.Style.STROKE
            }


            //clean up
            mTextPaint = TextPaint().apply {
                color = Color.WHITE
                textSize = 24.0f
            }


        }

        private fun initializeFeatures(minute:Int){

            if (isRestAlarmEnabled) {

                mSelectedMinuteDegree = minute.times(6f)

                //todo figure out # of lines to on given arbitrary minutes
                //60 minutes divided by this interval
                lines = 60 / breakInterval
                breakIntervalDegree = 6 * breakInterval.toFloat()

                //Lets find out what our Alarm Intervals are

                val intervals= ArrayList<Int>()
                intervals.add(minute)

                for (i in 1..(lines - 1)) {
                    intervals.add((minute + i*breakInterval).rem(60))
                }

                Log.d("initializeFeatures","Intervals: ${intervals.joinToString(",")}")


                //The first alarm should go off on the next available interval.
                //which one is the correct interval?
                //if current minute is > latest inverval, alarm should go off in Min(intervals) + (60 - current) minutes
                //else alarm should go off at the lowest of (Intervali - current) that is possible

                //current minutes
                var minutesTilAlarm = 60
                val cMinutes = mCalendar.get(Calendar.MINUTE)
                if (cMinutes >= intervals.max()?:0) {
                    minutesTilAlarm = (intervals.min()?:0) + (60 - cMinutes)
                }
                else {
                    intervals.forEach {
                        val tdiff = it - cMinutes
                        if (tdiff > 0) {
                            if(tdiff < minutesTilAlarm) {
                                minutesTilAlarm = tdiff
                            }
                        }
                    }
                }

                Log.d("$tag initializeFeatures","Next Alarm in $minutesTilAlarm minutes")

                //TODO MOVE THIS TO alarm extensions
                val manager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                manager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        //set the alarm to go off on the minute
                        System.currentTimeMillis() + (minutesTilAlarm * 60 * 1000) - (mCalendar.get(Calendar.SECOND) * 1000),
                        breakInterval.toLong() * 60L * 1000L,
                        getRestPendingIntent()
                )
            }
        }

        private fun getTimerPendingIntent():PendingIntent{
            return Intent(this@TaskWatchService, AlarmReceiver::class.java).let {
                it.action = ACTION_TIMER
                PendingIntent.getBroadcast(this@TaskWatchService, 0, it,PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }

        private fun getRestPendingIntent():PendingIntent{
            return Intent(this@TaskWatchService, AlarmReceiver::class.java).let {
                it.action = ACTION_REST
                PendingIntent.getBroadcast(this@TaskWatchService, 0, it,PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }


        override fun onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME)
            applicationContext.getLocalSettings().unregisterOnSharedPreferenceChangeListener(preferenceListener)
            super.onDestroy()
        }

        override fun onPropertiesChanged(properties: Bundle) {
            super.onPropertiesChanged(properties)
            mLowBitAmbient = properties.getBoolean(
                    WatchFaceService.PROPERTY_LOW_BIT_AMBIENT, false)
            mBurnInProtection = properties.getBoolean(
                    WatchFaceService.PROPERTY_BURN_IN_PROTECTION, false)
        }

        override fun onTimeTick() {
            super.onTimeTick()
            invalidate()
        }

        override fun onAmbientModeChanged(inAmbientMode: Boolean) {
            super.onAmbientModeChanged(inAmbientMode)
            mAmbient = inAmbientMode

            updateWatchHandStyle()

            // Check and trigger whether or not timer should be running (only
            // in active mode).
            updateTimer()
        }

        private fun updateWatchHandStyle() {
            if (mAmbient) {
                mHourPaint.color = Color.WHITE
                mMinutePaint.color = Color.WHITE
                mSecondPaint.color = Color.WHITE
                mTickAndCirclePaint.color = Color.WHITE
                mBreakLinePaint.color = Color.WHITE

                mHourPaint.isAntiAlias = false
                mMinutePaint.isAntiAlias = false
                mSecondPaint.isAntiAlias = false
                mTickAndCirclePaint.isAntiAlias = false
                mBreakLinePaint.isAntiAlias = false

                mHourPaint.clearShadowLayer()
                mMinutePaint.clearShadowLayer()
                mSecondPaint.clearShadowLayer()
                mTickAndCirclePaint.clearShadowLayer()
                mBreakLinePaint.clearShadowLayer()

            } else {
                mHourPaint.color = mWatchHandColor
                mMinutePaint.color = mWatchHandColor
                mSecondPaint.color = mWatchHandHighlightColor
                mTickAndCirclePaint.color = mWatchHandColor
                mBreakLinePaint.color = Color.MAGENTA


                mHourPaint.isAntiAlias = true
                mMinutePaint.isAntiAlias = true
                mSecondPaint.isAntiAlias = true
                mTickAndCirclePaint.isAntiAlias = true
                mBreakLinePaint.isAntiAlias = true

                mHourPaint.setShadowLayer(
                        SHADOW_RADIUS, 0f, 0f, mWatchHandShadowColor)
                mMinutePaint.setShadowLayer(
                        SHADOW_RADIUS, 0f, 0f, mWatchHandShadowColor)
                mSecondPaint.setShadowLayer(
                        SHADOW_RADIUS, 0f, 0f, mWatchHandShadowColor)
                mTickAndCirclePaint.setShadowLayer(
                        SHADOW_RADIUS, 0f, 0f, mWatchHandShadowColor)
                mBreakLinePaint.setShadowLayer(
                        SHADOW_RADIUS, 0f, 0f, mWatchHandShadowColor)
            }
        }

        override fun onInterruptionFilterChanged(interruptionFilter: Int) {
            super.onInterruptionFilterChanged(interruptionFilter)
            val inMuteMode = interruptionFilter == WatchFaceService.INTERRUPTION_FILTER_NONE

            /* Dim display in mute mode. */
            if (mMuteMode != inMuteMode) {
                mMuteMode = inMuteMode
                mHourPaint.alpha = if (inMuteMode) 100 else 255
                mMinutePaint.alpha = if (inMuteMode) 100 else 255
                mSecondPaint.alpha = if (inMuteMode) 80 else 255
                invalidate()
            }
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)

            /*
             * Find the coordinates of the center point on the screen, and ignore the window
             * insets, so that, on round watches with a "chin", the watch face is centered on the
             * entire screen, not just the usable portion.
             */
            mCenterX = width / 2f
            mCenterY = height / 2f

            middleSectionRadius = (mCenterY *PERCENT_OF_RADIUS).toFloat()

            /*
             * Calculate lengths of different hands based on watch screen size.
             */
            mSecondHandLength = (mCenterX * 0.875).toFloat()
            sMinuteHandLength = (mCenterX * 0.75).toFloat()
            sHourHandLength = (mCenterX * 0.5).toFloat()

            /* Scale loaded background image (more efficient) if surface dimensions change. */
            val scale = width.toFloat() / mBackgroundBitmap.width.toFloat()

            mBackgroundBitmap = Bitmap.createScaledBitmap(mBackgroundBitmap,
                    (mBackgroundBitmap.width * scale).toInt(),
                    (mBackgroundBitmap.height * scale).toInt(), true)

            /*
             * Create a gray version of the image only if it will look nice on the device in
             * ambient mode. That means we don't want devices that support burn-in
             * protection (slight movements in pixels, not great for images going all the way to
             * edges) and low ambient mode (degrades image quality).
             *
             * Also, if your watch face will know about all images ahead of time (users aren't
             * selecting their own photos for the watch face), it will be more
             * efficient to create a black/white version (png, etc.) and load that when you need it.
             */
            if (!mBurnInProtection && !mLowBitAmbient) {
                initGrayBackgroundBitmap()
            }

            measureFeatures()
            measureTouchables(width,height)

        }

        private fun initGrayBackgroundBitmap() {
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

        /**
         * Captures tap event (and tap type). The [WatchFaceService.TAP_TYPE_TAP] case can be
         * used for implementing specific logic to handle the gesture.
         */
        override fun onTapCommand(tapType: Int, x: Int, y: Int, eventTime: Long) {
            when (tapType) {
                WatchFaceService.TAP_TYPE_TOUCH -> {
                    // The user has started touching the screen.
                }
                WatchFaceService.TAP_TYPE_TOUCH_CANCEL -> {
                    // The user has started a different gesture or otherwise cancelled the tap.
                }
                WatchFaceService.TAP_TYPE_TAP ->{
                    // The user has completed the tap gesture.

                    if (alarmTriggered) {
                        alarmTriggered = false

                        //TODO disable corresponding alarm
                        disableTimer()

                    }else{
                        xLastTouch = x
                        yLastTouch = y
                        lastTimeTouch = eventTime

                        //TODO process UI touches more eloquently.
                        var buttonTouched = false
                        touchables.forEach {
                            buttonTouched = it.isTouched(x,y)
                            if (buttonTouched) {
                                return
                            }
                        }
                        if (!buttonTouched) {
                            when (switchingButton?.getState()) {
                                STATE_ALARM -> {}
                                STATE_BREAKS -> { if(isTouchingCenter(x,y)) disableRestPeriods() else enableRestPeriods() }
                                STATE_TIMER -> {if(isTouchingCenter(x,y)) disableTimer() else enableTimer(getSelectedMinute(x,y))}
                                else ->{}
                            }
                        }
                    }
                }
            }
            invalidate()
        }

        /**
         * Given coordinates, determine if we're touching the inner or outer part of the screen
         */
        private fun isTouchingCenter(x:Int, y:Int):Boolean{

            val dSquare = ((x - mCenterX) * (x - mCenterX)) + ((y - mCenterY) * (y - mCenterY))
            val rSquare = (mCenterX * mCenterX * PERCENT_OF_RADIUS * PERCENT_OF_RADIUS)
            return dSquare < rSquare
        }

        private fun getSelectedMinute(x:Int,y:Int):Int{
                val radians = Math.atan2((x - mCenterX).toDouble(), -(y - mCenterY).toDouble())
                return ((when (radians) { //convert radians to degrees

                    //if between 0 - PI angle is 0 - 180
                    in 0.0..Math.PI -> {
                        radians * 180 / Math.PI
                    }
                    //angle is >180 so add the angle to 180
                    else -> 180 * (1 + (1 - Math.abs(radians) / Math.PI))
                    //6 degrees per minute
                }) / 6.0).roundToInt()
        }


        override fun onDraw(canvas: Canvas, bounds: Rect) {
            val now = System.currentTimeMillis()
            mCalendar.timeInMillis = now

            drawBackground(canvas)
            drawWatchFace(canvas)
            drawFeatures(canvas)
            drawButtons(canvas)
        }

        private fun drawButtons(canvas: Canvas) {
            //TODO don't draw this everyframe
            leftButton?.draw(canvas)
            centerButton?.draw(canvas)
            rightButton?.draw(canvas)
            switchingButton?.draw(canvas)
        }

        private fun drawBackground(canvas: Canvas) {

            /*if (mAmbient && (mLowBitAmbient || mBurnInProtection)) {
                canvas.drawColor(Color.BLACK)
            } else if (mAmbient) {
                canvas.drawBitmap(mGrayBackgroundBitmap, 0f, 0f, mBackgroundPaint)
            } else {
                canvas.drawBitmap(mBackgroundBitmap, 0f, 0f, mBackgroundPaint)
            }*/
            canvas.drawColor(Color.BLACK)
        }

        private fun drawFeatures(canvas: Canvas) {

            //TODO we don't need to draw all the features now because they don't update every second
            //TODO Smooth transition of time selection

            //lets draw our rest alarms if enabled
            drawRestLines(canvas)
            drawTexts(canvas)

        }

        private fun drawTexts(canvas: Canvas) {

            staticLayout?.let {

                canvas.save()
                canvas.translate(mCenterX - textHalfWidth, textHeight)
                staticLayout!!.draw(canvas)
                canvas.restore()
            }
        }

        private fun drawRestLines(canvas: Canvas) {
            if (isRestAlarmEnabled) {

                canvas.save()

                //selected minute = 15
                canvas.rotate(mSelectedMinuteDegree , mCenterX,mCenterY)
                canvas.drawLine(
                        mCenterX,
                        mCenterY - middleSectionRadius,
                        mCenterX,
                        0f,
                        mBreakLinePaint)


                //now we rotate break interval amount
                for (i in 1..(lines - 1)) {
                    canvas.rotate(breakIntervalDegree , mCenterX,mCenterY)
                    canvas.drawLine(
                            mCenterX,
                            mCenterY - middleSectionRadius,
                            mCenterX,
                            0f,
                            mBreakLinePaint)
                }

                canvas.drawCircle(mCenterX,mCenterY,mCenterX*PERCENT_OF_RADIUS.toFloat(),mBreakLinePaint)
                canvas.restore()
            }

            if (isTimerEnabled) {
                timerView.onDraw(canvas,mCalendar.timeInMillis)
            }
        }

        private fun drawWatchFace(canvas: Canvas) {

            /*
             * Draw ticks. Usually you will want to bake this directly into the photo, but in
             * cases where you want to allow users to select their own photos, this dynamically
             * creates them on top of the photo.
             */
            val innerTickRadius = mCenterX - 10
            val outerTickRadius = mCenterX
            for (tickIndex in 0..11) {
                val tickRot = (tickIndex.toDouble() * Math.PI * 2.0 / 12).toFloat()
                val innerX = Math.sin(tickRot.toDouble()).toFloat() * innerTickRadius
                val innerY = (-Math.cos(tickRot.toDouble())).toFloat() * innerTickRadius
                val outerX = Math.sin(tickRot.toDouble()).toFloat() * outerTickRadius
                val outerY = (-Math.cos(tickRot.toDouble())).toFloat() * outerTickRadius
                canvas.drawLine(mCenterX + innerX, mCenterY + innerY,
                        mCenterX + outerX, mCenterY + outerY, mTickAndCirclePaint)
            }

            /*
             * These calculations reflect the rotation in degrees per unit of time, e.g.,
             * 360 / 60 = 6 and 360 / 12 = 30.
             */
            val seconds =
                    mCalendar.get(Calendar.SECOND) + mCalendar.get(Calendar.MILLISECOND) / 1000f
            val secondsRotation = seconds * 6f
            val minutesRotation = mCalendar.get(Calendar.MINUTE) * 6f

            val hourHandOffset = mCalendar.get(Calendar.MINUTE) / 2f
            val hoursRotation = mCalendar.get(Calendar.HOUR) * 30 + hourHandOffset

            /*
             * Save the canvas state before we can begin to rotate it.
             */
            canvas.save()

            canvas.rotate(hoursRotation, mCenterX, mCenterY)
            canvas.drawLine(
                    mCenterX,
                    mCenterY - middleSectionRadius,
                    mCenterX,
                    0.0f,
                    mHourPaint)

            canvas.rotate(minutesRotation - hoursRotation, mCenterX, mCenterY)
            canvas.drawLine(
                    mCenterX,
                    mCenterY - middleSectionRadius ,
                    mCenterX,
                    0.0f,
                    mMinutePaint)

            /*
             * Ensure the "seconds" hand is drawn only when we are in interactive mode.
             * Otherwise, we only update the watch face once a minute.
             */
            if (!mAmbient) {
                canvas.rotate(secondsRotation - minutesRotation, mCenterX, mCenterY)
                canvas.drawLine(
                        mCenterX,
                        mCenterY - middleSectionRadius,
                        mCenterX,
                        0.0f,
                        mSecondPaint)
            }
/*
            canvas.drawCircle(
                    mCenterX,
                    mCenterY,
                    CENTER_GAP_AND_CIRCLE_RADIUS,
                    mTickAndCirclePaint)
*/

            /* Restore the canvas' original orientation. */
            canvas.restore()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)

            if (visible) {
                registerReceiver()
                /* Update time zone in case it changed while we weren't visible. */
                mCalendar.timeZone = TimeZone.getDefault()
                invalidate()

            } else {
                unregisterReceiver()
            }

            /* Check and trigger whether or not timer should be running (only in active mode). */
            updateTimer()
        }

        private fun registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return
            }
            mRegisteredTimeZoneReceiver = true
            val filter = IntentFilter(Intent.ACTION_TIMEZONE_CHANGED)
            this@TaskWatchService.registerReceiver(mTimeZoneReceiver, filter)
        }

        private fun unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return
            }
            mRegisteredTimeZoneReceiver = false
            this@TaskWatchService.unregisterReceiver(mTimeZoneReceiver)
        }

        /**
         * Starts/stops the [.mUpdateTimeHandler] timer based on the state of the watch face.
         */
        private fun updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME)
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME)
            }
        }

        /**
         * Returns whether the [.mUpdateTimeHandler] timer should be running. The timer
         * should only run in active mode.
         */
        private fun shouldTimerBeRunning(): Boolean {
            return isVisible && !mAmbient
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        fun handleUpdateTimeMessage() {
            invalidate()
            if (shouldTimerBeRunning()) {
                val timeMs = System.currentTimeMillis()
                val delayMs = INTERACTIVE_UPDATE_RATE_MS - timeMs % INTERACTIVE_UPDATE_RATE_MS
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs)
            }
        }
    }
}



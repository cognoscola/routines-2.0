package com.gorillamoa.routines

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.*
import android.graphics.*
import android.os.*
import android.support.wearable.watchface.CanvasWatchFaceService
import android.support.wearable.watchface.WatchFaceService
import android.support.wearable.watchface.WatchFaceStyle
import android.util.Log
import android.view.SurfaceHolder
import com.gorillamoa.routines.data.Task
import com.gorillamoa.routines.extensions.*
import com.gorillamoa.routines.receiver.AlarmReceiver
import com.gorillamoa.routines.receiver.AlarmReceiver.Companion.ACTION_REST
import com.gorillamoa.routines.receiver.AlarmReceiver.Companion.ACTION_TIMER
import com.gorillamoa.routines.scheduler.TaskScheduler
import com.gorillamoa.routines.utils.CircularTimer
import com.gorillamoa.routines.views.*

import java.lang.ref.WeakReference
import java.util.*
import kotlin.math.roundToInt


/**
 * Updates rate in milliseconds for interactive mode. We update once a second to advance the
 * second hand.
 */
//private const val INTERACTIVE_UPDATE_RATE_MS = 67L //15fps
private const val INTERACTIVE_UPDATE_RATE_MS = 1000 // 1 fps


//

/**
 * Handler message id for updating the time periodically in interactive mode.
 */
private const val MSG_UPDATE_TIME = 0

private const val HOUR_STROKE_WIDTH = 5f
private const val MINUTE_STROKE_WIDTH = 3f
private const val SECOND_TICK_STROKE_WIDTH = 2f


private const val SHADOW_RADIUS = 6f

private const val PERCENT_OF_RADIUS = 0.7

//TODO give user option to approve today's task if there isn't any scheduled!
//TODO create Approve/No task/Done day UI

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

//TODO ADD a seconds timer
//TODO add a hour timer
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

        private var seconds = 0.0f
        private var secondsRotation = 0.0f

        /* Colors for all hands (hour, minutes, seconds, ticks) based on photo loaded. */
        private var mWatchHandColor: Int = 0
        private var mWatchHandHighlightColor: Int = 0
        private var mWatchHandShadowColor: Int = 0

        private lateinit var mHourPaint: Paint
        private lateinit var mMinutePaint: Paint
        private lateinit var mSecondPaint: Paint
        private lateinit var mTickAndCirclePaint: Paint

        private val livingBackground = LivingBackground()
        private val foreground=Foreground()

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

        //clean the timer stuff into its own class
        private lateinit var timerView:TimerView
        private var timingObject=CircularTimer()

        var wakeLock:PowerManager.WakeLock? = null


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
                    turnOnScreen()
                    livingBackground.enableAlarm()
                }
            }

            if (key == isBreakAlarmTriggered) {

                //we're just going to fire off
                if (sharedPreferences.getBoolean(key, false)) {

                    turnOnScreen()
                    livingBackground.enableAlarm()
                }
            }


        }

        private fun turnOnScreen(){
      /*      wakeLock =
                    (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                        newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "MyApp::MyWakelockTag").apply {
                            acquire(30000)
                        }
                    }*/
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

            TaskScheduler.getNextUncompletedTask(this@TaskWatchService) { task ->
                foreground.configureTaskUI(task, this@TaskWatchService)
                invalidate()
            }

            livingBackground.initializeBackground(getSystemService(VIBRATOR_SERVICE) as Vibrator){
                updateWatchHandStyle()
            }

            initializeWatchFace()
            initializeFeatures(selectedMinute)

            applicationContext.getLocalSettings().registerOnSharedPreferenceChangeListener(preferenceListener)
        }

        private fun measureFeatures(){
            timerView = TimerView(mCenterX.toInt(),mCenterY.toInt(),mCenterX.toInt() - 30)
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
         * Enable a timer to go off in specified selectedMinute
         * @param selectedMinute is the selected user value according to his view
         */
        private fun enableTimer(selectedMinute:Int){
            isTimerEnabled = true

            val currentMinute = mCalendar.get(Calendar.MINUTE)
            val minutes =
                    if (selectedMinute > currentMinute) {
                selectedMinute - currentMinute
            }else{
                (60 - currentMinute) + selectedMinute
            }


            val timeToTrigger = System.currentTimeMillis() + (minutes * 60 * 1000)
            timingObject.setSelectedMinute(System.currentTimeMillis(), timeToTrigger)


            //TODO MOVE THIS TO alarm extensions
            getAlarmService().set(
                    AlarmManager.RTC_WAKEUP,
                    timeToTrigger,
                    getTimerPendingIntent()
            )

            saveTimerTime(timeToTrigger)
        }

        private fun disableTimer(){
            timingObject.reset()
            //TODO move this to alarm extensions
            applicationContext.getAlarmService().cancel(getTimerPendingIntent())
            isTimerEnabled = false
            applicationContext.saveAlarmTimerTriggerStatus(false)

        }

        private fun disableRestAlarm(){
            applicationContext.saveAlarmRestTriggerStatus(false)
        }

        var currentTask:Task? = null

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
                //if current minutes is > latest inverval, alarm should go off in Min(intervals) + (60 - current) minutes
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
                        //set the alarm to go off on the minutes
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

                mHourPaint.color = livingBackground.getPalette().getLightVibrantColor(Color.WHITE)
                mMinutePaint.color = livingBackground.getPalette().getLightVibrantColor(Color.WHITE)
                mSecondPaint.color = livingBackground.getPalette().getVibrantColor(Color.RED)
                mTickAndCirclePaint.color = livingBackground.getPalette().getLightVibrantColor(Color.WHITE)
                mBreakLinePaint.color = Color.MAGENTA


                mHourPaint.isAntiAlias = true
                mMinutePaint.isAntiAlias = true
                mSecondPaint.isAntiAlias = true
                mTickAndCirclePaint.isAntiAlias = true
                mBreakLinePaint.isAntiAlias = true

                val mWatchHandShadowColor = livingBackground.getPalette().getDarkMutedColor(Color.BLACK)

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

            middleSectionRadius = (mCenterY * PERCENT_OF_RADIUS).toFloat()

            /*
             * Calculate lengths of different hands based on watch screen size.
             */
            mSecondHandLength = (mCenterX * 0.875).toFloat()
            sMinuteHandLength = (mCenterX * 0.75).toFloat()
            sHourHandLength = (mCenterX * 0.5).toFloat()

            /* Scale loaded background image (more efficient) if surface dimensions change. */
            livingBackground.scaleBackground(width, height)

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
                livingBackground.initGrayBackgroundBitmap()
            }

            measureFeatures()


            foreground.measureTouchables(this@TaskWatchService, width, height,
                    stateButtonCallback = { invalidate() },
                    leftButtonCallback = {
                        TaskScheduler.getPreviousOrderedTask(this@TaskWatchService, currentTask?.id
                                ?: 0) {
                            foreground.configureTaskUI(it,this@TaskWatchService)
                            currentTask = it
                            invalidate()
                        }
                    },
                    rightButtonCallback = {
                        TaskScheduler.getNextOrderedTask(this@TaskWatchService, currentTask?.id
                                ?: 0) {
                            foreground.configureTaskUI(it, this@TaskWatchService)
                            currentTask = it
                            invalidate()
                        }
                    },
                    centerButtonCallback = { isComplete ->
                        if (isComplete) {
                            TaskScheduler.completeTask(this@TaskWatchService,currentTask?.id?:-1)}
                        else{
                            TaskScheduler.uncompleteTask(this@TaskWatchService, currentTask?.id?:-1)
                        }
                        invalidate()}
            )
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

                    if (livingBackground.isAlarmEnabled()) {
                        livingBackground.disableAlarm()
                        //TODO disable corresponding alarm
                        wakeLock?.release()
                        wakeLock = null
                        disableTimer()
                        disableRestAlarm()
                    }else{


                        xLastTouch = x
                        yLastTouch = y
                        lastTimeTouch = eventTime

                        if (!foreground.processTouch(x, y)) {

                            //clean needs more depedency. See if you can store state somewhere else
                            when (foreground.getState()) {
                                Foreground.STATE_ALARM -> {}
                                Foreground.STATE_BREAKS -> { if(isTouchingCenter(x,y)) disableRestPeriods() else enableRestPeriods() }
                                Foreground.STATE_TIMER -> {if(isTouchingCenter(x,y)) disableTimer() else enableTimer(getSelectedMinute(x,y))}
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
                return  ((when (radians) { //convert radians to degrees

                    //if between 0 - PI angle is 0 - 180
                    in 0.0..Math.PI -> {
                        radians * 180 / Math.PI
                    }
                    //angle is >180 so add the angle to 180
                    else -> 180 * (1 + (1 - Math.abs(radians) / Math.PI))
                    //6 degrees per minutes
                }) / 6.0).roundToInt()
        }


        override fun onDraw(canvas: Canvas, bounds: Rect) {
            val now = System.currentTimeMillis()
            mCalendar.timeInMillis = now

            //Reset everything to black
            canvas.drawColor(Color.BLACK)

            //measure some things , aka second
            //TODO remove this and switch to minutes
            seconds = mCalendar.get(Calendar.SECOND) + mCalendar.get(Calendar.MILLISECOND) / 1000f
            secondsRotation = seconds * 6f


            timingObject.calculateAngles(mCalendar)


            //draw our bg
            //TODO CALCULATE THINGS WHILE NOT UPDATING!
            livingBackground.drawBackground(canvas, mAmbient,mLowBitAmbient,mBurnInProtection, bounds, timingObject)

            if (isTimerEnabled) {
                timerView.onDraw(canvas,timingObject)
            }

            drawWatchFace(canvas)
            drawFeatures(canvas)
            foreground.drawButtons(canvas)
        }



        private fun drawFeatures(canvas: Canvas) {

            //TODO we don't need to draw all the features now because they don't update every second
            //TODO Smooth transition of time selection

            //lets draw our rest alarms if enabled
            drawRestLines(canvas)
            foreground.drawTexts(mCenterX.toInt(),canvas)
        }


        private fun drawRestLines(canvas: Canvas) {
            if (isRestAlarmEnabled) {

                canvas.save()

                //selected minutes = 15
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
//                canvas.drawLine(mCenterX + innerX, mCenterY + innerY,
//                        mCenterX + outerX, mCenterY + outerY, mTickAndCirclePaint)
            }

            /*
             * These calculations reflect the rotation in degrees per unit of time, e.g.,
             * 360 / 60 = 6 and 360 / 12 = 30.
             */


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
             * Otherwise, we only update the watch face once a minutes.
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



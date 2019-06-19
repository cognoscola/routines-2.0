package com.gorillamoa.routines.services

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
import com.gorillamoa.routines.core.data.Task

import com.gorillamoa.routines.core.receiver.AlarmReceiver.Companion.ACTION_REST
import com.gorillamoa.routines.core.receiver.AlarmReceiver.Companion.ACTION_TIMER
import com.gorillamoa.routines.views.*

import java.lang.ref.WeakReference
import java.util.*
import kotlin.math.roundToInt
import android.content.Intent
import com.gorillamoa.routines.activity.AlarmActivity
import android.content.BroadcastReceiver
import com.gorillamoa.routines.core.extensions.*

import com.gorillamoa.routines.utils.*


/**
 * Updates rate in milliseconds for interactive mode. We update once a second to advance the
 * second hand.
 */
private const val INTERACTIVE_UPDATE_RATE_MS_15FPS = 67L //15fps
private const val INTERACTIVE_UPDATE_RATE_MS = 1000 // 1 fps


/**
 * Handler message id for updating the time periodically in interactive mode.
 */
private const val MSG_UPDATE_TIME = 0

private const val HOUR_STROKE_WIDTH = 5f
private const val MINUTE_STROKE_WIDTH = 3f
private const val SECOND_TICK_STROKE_WIDTH = 2f

private const val SHADOW_RADIUS = 6f

private const val CENTER_AREA_RADIUS = 0.7
private const val HAND_TRAVEL_CIRCLE_RADIUS = 0.85

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

//TODO erase Touchable UI in ambient mode
class TaskWatchService : CanvasWatchFaceService() {

    //TODO create pager like effect (or other animation) when switching tasks

    override fun onCreateEngine(): Engine {
        return Engine()
    }

    private class EngineHandler(reference: Engine) : Handler() {
        private val mWeakReference: WeakReference<Engine> = WeakReference(reference)

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

        private var seconds = 0.0f
        private var secondsRotationDegrees = 0.0f

        /* Colors for all hands (hour, minutes, seconds, ticks) based on photo loaded. */
        private var mWatchHandColor: Int = 0
        private var mWatchHandHighlightColor: Int = 0
        private var mWatchHandShadowColor: Int = 0

        private val secondHand = TimeHand(TimeHand.TYPE_SECOND)
        private val minuteHand = TimeHand(TimeHand.TYPE_MINUTE)
        private val hourHand  = TimeHand(TimeHand.TYPE_HOUR)

        private val breakIndicator = FloatingCrystal()

        private lateinit var mHourPaint: Paint
        private lateinit var mTickAndCirclePaint: Paint
        private val debugPaint = Paint().apply {
            style = Paint.Style.STROKE
            strokeWidth = 2.0f
            color = Color.RED
        }

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

        private var timeLinePath = Path().apply {
            fillType = Path.FillType.EVEN_ODD
        }

        private var secondLinePath1 = Path().apply {
            fillType = Path.FillType.EVEN_ODD
        }

        //todo save selected Minute and break Interval
        //clean breaks stuff into their own class
        private var selectedMinute = 0
        private var breakInterval:Int = 20

        private var lines = 0
        private var breakIntervalDegree = 0f
        private var mSelectedMinuteDegree = 0f
        private var middleSectionRadius = 0f //the radius of the circle on which a line is draw
        private var secondTravelPathRadius = 0f //the radius of the circle on which the seconds Hands travels on
        private var isRestAlarmEnabled = false
        private var isTimerEnabled = false

        //clean the timer stuff into its own class
        private lateinit var timerView:TimerView
        private var timingObject=CircularTimer()

        //TODO GIVING UP FOR NOW. We'll need this to learn when to reverse the display animation
        /*val displayManager: DisplayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val wakeLock: PowerManager.WakeLock =
                (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                    newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Routines:WatchFaceLockTag")}*/


        //create a shared preference listener so that we can update the watchface UI when
        //changes to preference variables occur
        //TODO this doesn't want to trigger sometimes, investigate why
         private val preferenceListener= SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->

            if (key == isRestAlarmActive) {

                if (!hasTouchedScreenRecently()) {
                    if(sharedPreferences.getBoolean(key,false)) enableRestPeriods() else disableRestPeriods()
                }
            }

            //TODO ADD A REST ALARM TRIGGER RESPONSE TO THE BREAKS (DIFFERENT FROM TIMER)

            if (key == isTimerAlarmTriggered) {

                //we're just going to fire off
                if (sharedPreferences.getBoolean(key, false)) {
                    turnOnScreen()
                    livingBackground.enableAlarm()
                }else{
                    if (livingBackground.isAlarmEnabled()) {
                        turnOffAlarms()
                    }
                }
            }

            if (key == com.gorillamoa.routines.core.extensions.isBreakAlarmTriggered) {

                //we're just going to fire off
                if (sharedPreferences.getBoolean(key, false)) {

                    Log.d("$tag ","Break Alarm trigger")
                    turnOnScreen()
                    livingBackground.enableAlarm()
                }else{
                    if (livingBackground.isAlarmEnabled()) {
                        turnOffAlarms()
                    }
                }
            }
        }

        private fun turnOnScreen(){

            val intent = Intent(this@TaskWatchService, AlarmActivity::class.java)
            // This boolean just makes it easier to check if the Activity has been started from
            // this class
            intent.putExtra("lock", true)
            // You need to add this to your intent if you want to start an Activity fromm a class
            // that is not an Activity itself
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            this@TaskWatchService.startActivity(intent)

        }

        private val mTimeZoneReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                mCalendar.timeZone = TimeZone.getDefault()
                invalidate()
            }
        }


/*
        val displayListener by lazy {
            return@lazy object : DisplayManager.DisplayListener {
                override fun onDisplayAdded(displayId: Int) {

                }

                override fun onDisplayRemoved(displayId: Int) {

                }

                override fun onDisplayChanged(displayId: Int) {
                    Log.d("$tag onDisplayChanged","Display State:${displayManager.getDisplay(displayId).state}")

                    try {
                        if (displayManager.getDisplay(displayId).state === Display.STATE_ON) {
//                            updateFaceDisplay(true)
                            Log.d("$tag onDisplayChanged","Acquired Lock")
                            wakeLock.acquire(10000)
                            Handler().postDelayed({

                                if (wakeLock.isHeld) {

                                    Log.d("$tag onDisplayChanged","Release Lock")
                                    wakeLock.release()
                                }else{
                                    Log.d("$tag onDisplayChanged","Lock not held!")
                                }
                            },8000)
                        } else {
//                            updateFaceDisplay(false)
                        }
                    } catch (exception: NullPointerException) {
                    }

                }
            }
        }
*/

        override fun onCreate(holder: SurfaceHolder) {
            super.onCreate(holder)


            setWatchFaceStyle(WatchFaceStyle.Builder(this@TaskWatchService)
                    .setAcceptsTapEvents(true)

                    .build())

            mCalendar = Calendar.getInstance()

            isRestAlarmEnabled = isRestAlarmActive()
            isTimerEnabled = isTimerAlarmActive()

            if (isRestAlarmEnabled or isTimerEnabled) {
                saveAlarmRestTriggerStatus(false)
                saveAlarmTimerTriggerStatus(false)
            }

            com.gorillamoa.routines.core.scheduler.TaskScheduler.getNextUncompletedTask(this@TaskWatchService) { task ->
                foreground.configureTaskUI(task, this@TaskWatchService)
                invalidate()
            }

            livingBackground.initializeBackground(getSystemService(VIBRATOR_SERVICE) as Vibrator){
                updateWatchHandStyle()
            }

            initializeWatchFace()
            initializeFeatures(selectedMinute)

            applicationContext.getLocalSettings().registerOnSharedPreferenceChangeListener(preferenceListener)


            /*Log.d("$tag onCreate","TimeOut at${            Settings.System.getInt(contentResolver,
                    Settings.System.SCREEN_OFF_TIMEOUT)
            }")*/

//            ClickableRectangle.enableDebug()
//            displayManager.registerDisplayListener(displayListener, null)
        }

        private fun measureFeatures(){
            timerView = TimerView(mCenterX.toInt(),mCenterY.toInt(),mCenterX.toInt() - THIRTY_INT)
        }


        /**
         * if the user touched the screen in the past 5 seconds
         */
        private fun hasTouchedScreenRecently():Boolean = (SystemClock.uptimeMillis() - lastTimeTouch) < 1500

        private fun enableRestPeriods(){
            isRestAlarmEnabled = true

            selectedMinute = if (hasTouchedScreenRecently()) {
                getSelectedMinute(xLastTouch,yLastTouch)
            }

            else{
                //activate in 18 minutes from now (it takes about 2 minutes to recognize the activity)
                (mCalendar.get(Calendar.MINUTE) + 18).rem(SIXTY_INT)
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
                (SIXTY_INT - currentMinute) + selectedMinute
            }

            val timeToTrigger = System.currentTimeMillis() + (minutes * ONE_MINUTE_MILLIS_LONG)
            timingObject.setSelectedMinute(System.currentTimeMillis(), timeToTrigger)


             //TODO MOVE THIS TO alarm extensions
            //clean clear up Allocation issues!
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

        }

        /**
         * Always disable any trigger when the user has interacted with the app
         */
        private fun disableAlarmTriggers(){
            applicationContext.saveAlarmTimerTriggerStatus(false)
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
                        SHADOW_RADIUS, ZERO_FLOAT, ZERO_FLOAT, mWatchHandShadowColor)
            }

            mTickAndCirclePaint = Paint().apply {
                color = mWatchHandColor
                strokeWidth = SECOND_TICK_STROKE_WIDTH
                isAntiAlias = true
                style = Paint.Style.STROKE
                setShadowLayer(
                        SHADOW_RADIUS, ZERO_FLOAT, ZERO_FLOAT, mWatchHandShadowColor)
            }

            //clean up
        }

        private fun initializeFeatures(minute:Int){

            if (isRestAlarmEnabled) {

                mSelectedMinuteDegree = minute.times(SIX_FLOAT)

                //todo figure out # of lines to on given arbitrary minutes
                //60 minutes divided by this interval
                lines = SIXTY_INT / breakInterval
                breakIntervalDegree = SIX_INT * breakInterval.toFloat()

                //Lets find out what our Alarm Intervals are

                val intervals= ArrayList<Int>()
                intervals.add(minute)

                for (i in ONE_INT..(lines - ONE_INT)) {
                    //TODO add more indicators
                    intervals.add((minute + i*breakInterval).rem(SIXTY_INT))
                }

                Log.d("initializeFeatures","Intervals: ${intervals.joinToString(",")}")


                //The first alarm should go off on the next available interval.
                //which one is the correct interval?
                //if current minutes is > latest inverval, alarm should go off in Min(intervals) + (60 - current) minutes
                //else alarm should go off at the lowest of (Intervali - current) that is possible

                //current minutes
                var minutesTilAlarm = SIXTY_INT
                val cMinutes = mCalendar.get(Calendar.MINUTE)
                if (cMinutes >= intervals.max()?: ZERO_INT) {
                    minutesTilAlarm = (intervals.min()?: ZERO_INT) + (SIXTY_INT - cMinutes)
                }
                else {
                    intervals.forEach {
                        val tdiff = it - cMinutes
                        if (tdiff > ZERO_INT) {
                            if(tdiff < minutesTilAlarm) {
                                minutesTilAlarm = tdiff
                            }
                        }
                    }
                }

                Log.d("$tag initializeFeatures","Next Alarm in $minutesTilAlarm minutes")

                //TODO MOVE THIS TO alarm extensions
                //clean CLEAR The allocation of memory
                val manager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                manager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + (minutesTilAlarm * ONE_MINUTE_MILLIS_LONG) - (mCalendar.get(Calendar.SECOND) * ONE_THOUSAND_INT),
                        breakInterval * ONE_MINUTE_MILLIS_LONG,
                        getRestPendingIntent()
                )
            }
        }

        private fun getTimerPendingIntent():PendingIntent{
            return Intent(this@TaskWatchService, com.gorillamoa.routines.core.receiver.AlarmReceiver::class.java).let {
                it.action = ACTION_TIMER
                PendingIntent.getBroadcast(this@TaskWatchService, ZERO_INT, it,PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }

        private fun getRestPendingIntent():PendingIntent{
            return Intent(this@TaskWatchService, com.gorillamoa.routines.core.receiver.AlarmReceiver::class.java).let {
                it.action = ACTION_REST
                PendingIntent.getBroadcast(this@TaskWatchService, ZERO_INT, it,PendingIntent.FLAG_UPDATE_CURRENT)
            }
        }

        override fun onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME)
            applicationContext.getLocalSettings().unregisterOnSharedPreferenceChangeListener(preferenceListener)
            //TODO for later to know when to display the Toward Ambient animation
//            displayManager.unregisterDisplayListener(displayListener)
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

            if (!inAmbientMode) {
                livingBackground.comeOutOfAmbient()
            }else{
                livingBackground.goIntoAmbient()
            }

            updateWatchHandStyle()

            // Check and trigger whether or not timer should be running (only
            // in active mode).
            updateTimer()
        }

        private fun updateWatchHandStyle() {
            if (mAmbient) {
                mHourPaint.color = Color.WHITE
                mTickAndCirclePaint.color = Color.WHITE

                mHourPaint.isAntiAlias = false
                mTickAndCirclePaint.isAntiAlias = false

                mHourPaint.clearShadowLayer()
                mTickAndCirclePaint.clearShadowLayer()


            } else {

//                mHourPaint.color = livingBackground.getPalette().getLightVibrantColor(Color.WHITE)
//                mTickAndCirclePaint.color = livingBackground.getPalette().getLightVibrantColor(Color.WHITE)


                mHourPaint.isAntiAlias = true
                mTickAndCirclePaint.isAntiAlias = true

//                val mWatchHandShadowColor = livingBackground.getPalette().getDarkMutedColor(Color.BLACK)

                mHourPaint.setShadowLayer(
                        SHADOW_RADIUS, 0f, 0f, mWatchHandShadowColor)
                mTickAndCirclePaint.setShadowLayer(
                        SHADOW_RADIUS, 0f, 0f, mWatchHandShadowColor) }
        }

        override fun onInterruptionFilterChanged(interruptionFilter: Int) {
            super.onInterruptionFilterChanged(interruptionFilter)
            val inMuteMode = interruptionFilter == WatchFaceService.INTERRUPTION_FILTER_NONE

            /* Dim display in mute mode. */
            if (mMuteMode != inMuteMode) {
                mMuteMode = inMuteMode
                mHourPaint.alpha = if (inMuteMode) 100 else 255
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

            middleSectionRadius = (mCenterY * HAND_TRAVEL_CIRCLE_RADIUS).toFloat()
            secondTravelPathRadius = (mCenterY * HAND_TRAVEL_CIRCLE_RADIUS - 10.0f).toFloat()

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

            FloatingCrystal.measureDiamond(width,height,middleSectionRadius - 30f)


            secondHand.measure(width,height,secondTravelPathRadius)
            minuteHand.measure(width,height, middleSectionRadius)
            hourHand.measure(width,height,middleSectionRadius)


            foreground.measureTouchables(this@TaskWatchService, width, height,
                    stateButtonCallback = { invalidate() },
                    leftButtonCallback = {
                        com.gorillamoa.routines.core.scheduler.TaskScheduler.getPreviousOrderedTask(this@TaskWatchService, currentTask?.id
                                ?: 0) {
                            foreground.configureTaskUI(it,this@TaskWatchService)
                            currentTask = it
                            invalidate()
                        }
                    },
                    rightButtonCallback = {
                        com.gorillamoa.routines.core.scheduler.TaskScheduler.getNextOrderedTask(this@TaskWatchService, currentTask?.id
                                ?: 0) {
                            foreground.configureTaskUI(it, this@TaskWatchService)
                            currentTask = it
                            invalidate()
                        }
                    },
                    centerButtonCallback = { isComplete ->
                        if (isComplete) {
                            com.gorillamoa.routines.core.scheduler.TaskScheduler.completeTask(this@TaskWatchService,currentTask?.id?:-1)}
                        else{
                            com.gorillamoa.routines.core.scheduler.TaskScheduler.uncompleteTask(this@TaskWatchService, currentTask?.id?:-1)
                        }
                        invalidate()
                    }
            )
        }

        /**
         * Turn off any alarm that may be going on (except the hour alarm)
         */
        private fun turnOffAlarms(){
            livingBackground.disableAlarm()
            disableTimer()
        }

        /**
         * Captures tap event (and tap type). The [WatchFaceService.TAP_TYPE_TAP] case can be
         * used for implementing specific logic to handle the gesture.
         */
        override fun onTapCommand(tapType: Int, x: Int, y: Int, eventTime: Long) {
            Log.d("$tag onTapCommand","TAP DETECTED")

            when (tapType) {
                WatchFaceService.TAP_TYPE_TOUCH -> {
                    // The user has started touching the screen.
                }
                WatchFaceService.TAP_TYPE_TOUCH_CANCEL -> {
                    // The user has started a different gesture or otherwise cancelled the tap.
                }
                WatchFaceService.TAP_TYPE_TAP ->{
                    // The user has completed the tap gesture.

                    Log.d("$tag onTapCommand","processEntity TAP COMMAND")

                    /**Disable any triggers any time the user interacts with the app
                    This prevents the app from being stuck in a state where the alarm is never fired
                    because the setting is always stuck on true, and yet we can't disable it
                    because the alarm is never firing. So just always disable it when we touch
                    the screen */
                    disableAlarmTriggers()

                    if (livingBackground.isAlarmEnabled()) {turnOffAlarms()
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
            val rSquare = (mCenterX * mCenterX * CENTER_AREA_RADIUS * CENTER_AREA_RADIUS)
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
            seconds = mCalendar.get(Calendar.SECOND) + mCalendar.get(Calendar.MILLISECOND) / ONE_THOUSAND_FLOAT
            secondsRotationDegrees = seconds * SIX_FLOAT


            timingObject.calculateAngles(mCalendar)


            //draw our bg
            //TODO CALCULATE THINGS WHILE NOT UPDATING!

            livingBackground.drawBackground(canvas, mAmbient, mLowBitAmbient, mBurnInProtection, bounds, timingObject)

            if (isTimerEnabled) {
                timerView.onDraw(canvas, timingObject)
            }

            drawWatchFace(canvas, bounds)
            drawFeatures(canvas, bounds)
            foreground.drawButtons(canvas)
        }

        private fun drawFeatures(canvas: Canvas,bounds: Rect) {

            //TODO we don't need to draw all the features now because they don't update every second
            //TODO Smooth transition of time selection

            //lets draw our rest alarms if enabled
            drawRestLines(canvas)
            /**
             * Make sure we only update the time once per minute!
             */
            mCalendar.get(Calendar.MINUTE).let {
                if (it != lastMinute) {
                    lastMinute = it
                    foreground.updateTimeText(mCalendar)
                }
            }
            foreground.drawTexts(mCenterX.toInt(),canvas,mCalendar)
        }

        var lastMinute = 0


        private fun drawRestLines(canvas: Canvas) {
            if (isRestAlarmEnabled) {

                canvas.save()
                //selected minutes = 15
                canvas.rotate(mSelectedMinuteDegree , mCenterX,mCenterY)
                var currentDegree:Float = mSelectedMinuteDegree

                //calculate the X, Y position of the indicated triangle so we can find out the correct colod
                breakIndicator.draw(canvas,currentDegree,livingBackground)
                //now we rotate break interval amount
                for (i in ONE_INT..(lines - ONE_INT)) {
                    canvas.rotate(breakIntervalDegree , mCenterX,mCenterY)
                    currentDegree +=breakIntervalDegree
                    breakIndicator.draw(canvas,currentDegree,livingBackground)
                }
                canvas.restore()
            }
        }

        private fun drawWatchFace(canvas: Canvas, bounds:Rect) {

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
            val minutesRotation = mCalendar.get(Calendar.MINUTE) * SIX_FLOAT
            val hourHandOffset = mCalendar.get(Calendar.MINUTE) / TWO_FLOAT
            val hoursRotation = mCalendar.get(Calendar.HOUR) * THIRTY_INT + hourHandOffset

            /*
             * Save the canvas state before we can begin to rotate it.
             */
            canvas.save()
            canvas.rotate(hoursRotation, mCenterX, mCenterY)

//            canvas.drawPath(timeLinePath,mHourPaint)
            hourHand.draw(canvas,mCenterX,mCenterY,bounds,livingBackground,hoursRotation)

            canvas.rotate(minutesRotation - hoursRotation, mCenterX, mCenterY)

            minuteHand.draw(canvas, mCenterX,mCenterY, bounds,livingBackground, minutesRotation)

            /*
             * Ensure the "seconds" hand is drawn only when we are in interactive mode.
             * Otherwise, we only update the watch face once a minutes.
             */

            if (!mAmbient) {
                canvas.rotate(secondsRotationDegrees - minutesRotation, mCenterX, mCenterY)
                secondHand.draw(canvas,mCenterX,mCenterY,bounds,livingBackground,secondsRotationDegrees)
            }

            /* Restore the canvas' original orientation. */
            canvas.restore()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)

            Log.d("$tag onVisibilityChanged","Visible $visible")
            if (visible) {
                registerReceiver()
                /* Update time zone in case it changed while we weren't visible. */
                mCalendar.timeZone = TimeZone.getDefault()
                livingBackground.comeOutOfAmbient()
                invalidate()

            } else {
                unregisterReceiver()
                livingBackground.setPresetstoAmbientMode()
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

            return true
            //TODO CHANGE THIS BACK
            //TODO ADD A CALLBACK TO LET US KNOW WHEN ANIMATION IS FINISHED!
//            return isVisible && !mAmbient
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        fun handleUpdateTimeMessage() {
            invalidate()
            if (shouldTimerBeRunning()) {
                val timeMs = System.currentTimeMillis()
                val delayMs =/*if(livingBackground.isAlarmEnabled()){
                    INTERACTIVE_UPDATE_RATE_MS_15FPS - timeMs % INTERACTIVE_UPDATE_RATE_MS_15FPS
                } else{ INTERACTIVE_UPDATE_RATE_MS - timeMs % INTERACTIVE_UPDATE_RATE_MS}*/
                        INTERACTIVE_UPDATE_RATE_MS_15FPS - timeMs % INTERACTIVE_UPDATE_RATE_MS_15FPS
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs)
            }
        }


    }
}



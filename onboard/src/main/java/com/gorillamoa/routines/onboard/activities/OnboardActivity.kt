package com.gorillamoa.routines.onboard.activities

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle

import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.gorillamoa.routines.core.coroutines.Coroutines
import com.gorillamoa.routines.core.data.Task
import com.gorillamoa.routines.core.extensions.*
import com.gorillamoa.routines.core.receiver.NotificationActionReceiver.Companion.ACTION_WAKE_START_DAY
import com.gorillamoa.routines.notifications.*
import com.gorillamoa.routines.onboard.R
import com.gorillamoa.routines.onboard.fragments.InformationFragment
import com.gorillamoa.routines.onboard.fragments.SplashFragment
import com.gorillamoa.routines.onboard.fragments.TimePickerFragment
import kotlinx.android.synthetic.main.activity_onboard.fragmentContainer
import kotlinx.coroutines.*
import java.util.*

/**
 * Created by Guillermo Alvarez Colman 15/02/2019
 *
 * This handles "On boarding" of users when they first enter the app.
 * We need to collect some information so that we can configure the app for them.
 * E.g. What time they wake up - in order to program the alarm
 * E.g. What they they sleep - in order to program the sleep alarm
 *
 * In this context, alarm refers to device wakeup alarm that shows a notification
 * to the user to let them know its time to plan their tasks. It is not necessarily a
 * "Wake up from sleep" alarm.
 *
 */
const val ACTION_WAKE_UP_PRACTISE = "action.wakeup.practise"

class OnboardActivity:FragmentActivity(){

    //TODO change the activity launcher name (what the user sees)
    private val TEXT_FRAGMENT_TAG = "textfrag"

    enum class OnboardState{
        Splash,
        TEXT1,
        TEXT2,
        PickTime,
        TEXT3,
        TEXT4,
        Text6
    }

    private var state:OnboardState = OnboardState.Splash


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(if(isWatch())R.layout.activity_onboard_wear else R.layout.activity_onboard)

        supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, SplashFragment())
                .commit()

        //Supposedly the wake up notification should not be on right now, so we'll
        //just dismiss incase it is!
        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
            Log.d("NotificationActionRecei","Dismissing Wake up ${intent.getIntExtra(TASK_ID,-1)}")
            cancel(NOTIFICATION_TAG,intent.getIntExtra(TASK_ID,1))
        }

        GlobalScope.launch {

            delay(3000)

            //TODO check if intent is null
            if (intent?.action == ACTION_WAKE_UP_PRACTISE) {

                //dismiss the notification
                //dismiss the wake up notification
                state = OnboardState.TEXT3
            }

            setNextFragment(state)
        }
    }

    /**
     * Determine which fragment to show next depending on the view state.
     * @param currentState is the current state which is visible right now.
     */
    private fun setNextFragment(currentState:OnboardState){
        when (currentState) {
            OnboardState.Splash -> {
                setTextFragment(
                        getString(R.string.onboard_welcome_title_00),
                        getString(R.string.onboard_welcome_01),
                        getString(R.string.onboard_continue), null
                )
                state = OnboardState.TEXT1
            }
            OnboardState.TEXT1 -> {
                setTextFragment(
                        getString(R.string.onboard_welcome_title_00),
                        getString(R.string.onboard_welcome_02),
                        getString(R.string.onboard_ready), null
                )
                state = OnboardState.TEXT2
            }
            OnboardState.TEXT2 -> {

                (fragmentContainer as View).setOnClickListener(null)
                supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, TimePickerFragment.newInstance(getString(R.string.onboard_wake_up_text)).apply {

                            setCallbackFunction { hour, minute, phase ->
                                getForwardFunction().invoke(0)
                                val cal = Calendar.getInstance()
                                setWakeTimeToCalendarAndStore(cal, hour, minute, phase)
                                alarmSetRepeatWithCal(cal, true)

                                setNextFragment(OnboardState.PickTime)
                            }
                        })
                        .commit()
                state = OnboardState.PickTime

            }
            OnboardState.PickTime -> {

                setTextFragment(
                        getString(R.string.onboard_title_03),
                        getString(R.string.onboard_welcome_03),
                        "Send me a notification", null
                )

                state = OnboardState.TEXT3
            }

            OnboardState.TEXT3 -> {

                setTextFragment(
                        getString(R.string.onboard_title_03),
                        getString(R.string.onboard_welcome_04),
                        getString(R.string.onboard_choice_01_04),
                        getString(R.string.onboard_choice_02_04)
                )
                state = OnboardState.TEXT4


                //TODO save state
            }
        }
    }

    /**
     * Show a fragment that contains a single text
     * @param textAddress is the ID of the string of text to display
     */
    private fun setTextFragment(title:String, description:String, actionOne:String?, actionTwo:String?){

        /** check if we're not already displaying the fragment, if yes just update the text,
         * otherwise pass the address to its arguments**/
        val textFrag = supportFragmentManager.findFragmentByTag(TEXT_FRAGMENT_TAG)
        Log.d("setTextFragment","checking existence")
        if ((textFrag != null)) {
            if (textFrag.isVisible) {
                Log.d("setTextFragment","Fragment was found")
                (textFrag as InformationFragment).updateText(title,description,actionOne,actionTwo)
            }
        }else{

            Log.d("setTextFragment","making new fragment")
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer,InformationFragment.newInstance(
                            title,description,actionOne,actionTwo
                    ) ,TEXT_FRAGMENT_TAG)
                    .commit()
        }
    }

    private fun nextState(){
        setNextFragment(state)
    }

    fun getForwardFunction() = { argument:Int ->

        if (state == OnboardState.TEXT4) {
            //capture the argument
        }

        if(state == OnboardState.TEXT3){

            val dummyArrray = ArrayList<Task>()
            dummyArrray.add(Task(
                    name = "This is my first task in the future!"
            ))
            dummyArrray.add(Task(
                    name = "2nd task in the future!"
            ))
            dummyArrray.add(Task(
                    name = "And so on..."
            ))
            dummyArrray.add(Task(
                    name = "<b>Click Start to Finish!</b>"
            ))

            /**
             * We are going to create a new notification building method, one that will
             * allow more flexibility when creating a new notification. Instead of creating
             * the notification from a single method, we'll split the methods into many
             * little methods in order to accept for inputs.
             * E.g. the remote view isn't always accepted, so we don't need to worry about it.
             */

            val smallWakeupView = createWakeUpRemoteView(dummyArrray.size)
            smallWakeupView.setIntentToStartButton(createNotificationActionPendingIntentForWakeUp(ACTION_WAKE_START_DAY, WAKE_UP_NOTIFICATION_ID))

            val largeWakeupView = createLargeWakeUpRemoteView(StringBuilder().stringifyTasks(dummyArrray, if(isWatch())25 else 40))
            largeWakeupView.setIntentToStartButton(createNotificationActionPendingIntentForWakeUp(ACTION_WAKE_START_DAY, WAKE_UP_NOTIFICATION_ID))

            notificationShowWakeUp(
                    StringBuilder().stringifyTasks(dummyArrray,if(isWatch())25 else 40),
                    mainPendingIntent = null,
                    dismissPendingIntent = null,
//                            dismissPendingIntent = createNotificationDeleteIntentForWakeUp(),
                    //TODO CHECK IF WE SHOULD ALLOW DISMISSAL with stubborn settings
                    dismissable = false,
                    //TODO get the actual task length
                    smallRemoteView = smallWakeupView,
//                            TODO Get stringbuilder from dagger singleton
                    bigRemoteView = largeWakeupView
            )

            saveOnboardStatus(true)
            this@OnboardActivity.finish()

        }

        nextState()
    }
}
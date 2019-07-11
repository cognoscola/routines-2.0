package com.gorillamoa.routines.onboard.activities

import android.os.Bundle

import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.gorillamoa.routines.core.coroutines.Coroutines
import com.gorillamoa.routines.core.data.Task
import com.gorillamoa.routines.core.extensions.*
import com.gorillamoa.routines.notifications.notificationShowWakeUp
import com.gorillamoa.routines.notifications.remoteGetSmallWakeUpView
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


        GlobalScope.launch {

            delay(3000)

            //TODO check if intent is null
            /*if (intent?.action == ACTION_TEST_WAKE_UP) {
                state = OnboardState.TEXT3
            }*/
            state = OnboardState.PickTime
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

                                setNextFragment(state)
                            }
                        })
                        .commit()
                state = OnboardState.PickTime

            }
            OnboardState.PickTime -> {

                setTextFragment(
                        getString(R.string.onboard_title_03),
                        getString(R.string.onboard_welcome_03),
                        null, null
                )

                CoroutineScope(Dispatchers.Main).launch {

                }
                Coroutines.ioThenMain({
                    delay(1000)
                }){
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


                    notificationShowWakeUp(
                            dummyArrray,
                            mainPendingIntent = null,
                            dismissPendingIntent = null,
                            //TODO dismissing the onboard should go back to the information fragment to displau to user to try again
//                            dismissPendingIntent = createNotificationDeleteIntentForWakeUp(),
                            //TODO CHECK IF WE SHOULD ALLOW DISMISSAL with stubborn settings
                            dismissable = false,
                            //TODO get the actual task length
                            smallRemoteView = null,
//                            smallRemoteView = if (!isWatch()) remoteGetSmallWakeUpView(dummyArrray.size) else null,
                            //TODO Get stringbuilder from dagger singleton
                            bigRemoteView =  null
//                            bigRemoteView = if (!isWatch()) remoteGetLargeWakeUpView(StringBuilder().stringifyTasks(dummyArrray)) else null
                    )

                    saveOnboardStatus(true)
                }

                GlobalScope.launch {
                    delay(1000)


                    //TODO FIX THIS
                    //TODO bring th string builder from dagger

                    //create an onboard task

                }

//                setTextFragment(R.string.onboard_welcome_text_04)
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

        nextState()
    }
}
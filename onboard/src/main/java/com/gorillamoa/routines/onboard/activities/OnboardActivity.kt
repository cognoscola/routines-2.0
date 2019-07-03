package com.gorillamoa.routines.onboard.activities

import android.os.Bundle

import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.gorillamoa.routines.core.extensions.*
import com.gorillamoa.routines.onboard.R
import com.gorillamoa.routines.onboard.fragments.InformationFragment
import com.gorillamoa.routines.onboard.fragments.SplashFragment
import com.gorillamoa.routines.onboard.fragments.TimePickerFragment
import kotlinx.android.synthetic.main.activity_onboard.fragmentContainer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
        TEXT4,
        TEXT5,
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
            if (intent?.action == ACTION_TEST_WAKE_UP) {
                state = OnboardState.TEXT5
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
            OnboardState.Splash ->{
                setTextFragment(
                        getString(R.string.onboard_welcome_title_00),
                        getString(R.string.onboard_welcome_01),
                        getString(R.string.onboard_continue)
                        )
                state = OnboardState.TEXT1
            }
            OnboardState.TEXT1 -> {
                setTextFragment(
                        getString(R.string.onboard_welcome_title_00),
                        getString(R.string.onboard_welcome_02),
                        getString(R.string.onboard_ready)
                )
                state = OnboardState.TEXT2
            }
            OnboardState.TEXT2 -> {

                (fragmentContainer as View).setOnClickListener(null)
                supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, TimePickerFragment().apply {

                            arguments?.putString(TimePickerFragment.DISPLAY_TEXT,getString(R.string.onboard_wake_up_text))
                            setCallbackFunction { hour, minute,phase ->

                                getForwardFunction().invoke()
                                val cal = Calendar.getInstance()
                                setWakeTimeToCalendarAndStore(cal, hour, minute,phase)
                                alarmSetRepeatWithCal(cal,true)
                            }
                        })
                        .commit()
                state = OnboardState.PickTime

            }
            OnboardState.PickTime -> {

                GlobalScope.launch {
                    delay(1000)
                    broadcastShowWakeUpTest()
                }
//                setTextFragment(R.string.onboard_welcome_text_04)
                state = OnboardState.TEXT5
            }

            OnboardState.TEXT5 -> {

//                setTextFragment(R.string.onboard_welcome_text_05)
                state = OnboardState.Text6
                //TODO save state
            }
        }
    }

    /**
     * Show a fragment that contains a single text
     * @param textAddress is the ID of the string of text to display
     */
    private fun setTextFragment(title:String, description:String, action:String){

        /** check if we're not already displaying the fragment, if yes just update the text,
         * otherwise pass the address to its arguments**/
        val textFrag = supportFragmentManager.findFragmentByTag(TEXT_FRAGMENT_TAG)
        Log.d("setTextFragment","checking existence")
        if ((textFrag != null)) {
            if (textFrag.isVisible) {
                Log.d("setTextFragment","Fragment was found")
                (textFrag as InformationFragment).updateText(title,description,action)
            }
        }else{

            Log.d("setTextFragment","making new fragment")
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer,InformationFragment.newInstance(
                            title,description,action
                    ) ,TEXT_FRAGMENT_TAG)
                    .commit()
        }
    }

    private fun nextState(){
        setNextFragment(state)
    }

    fun getForwardFunction() = { nextState() }
}
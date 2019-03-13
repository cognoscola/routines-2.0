package com.gorillamoa.routines.activity

import android.content.Intent
import android.os.Bundle

import android.support.wearable.activity.WearableActivity
import android.util.Log
import com.gorillamoa.routines.R
import com.gorillamoa.routines.fragment.InformationFragment
import com.gorillamoa.routines.fragment.SplashFragment
import com.gorillamoa.routines.fragment.TimePickerFragment
import com.gorillamoa.routines.receiver.WakeUpReceiver
import kotlinx.android.synthetic.main.activity_onboard.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


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
class OnboardActivity:WearableActivity(){

    //TODO change the activity launcher name (what the user sees)

    private val TEXT_FRAGMENT_TAG = "textfrag"

    enum class OnboardState{
        Other,
        text_1,
        text_2,
        text_3,
        PickTime,
        text_4,
        Text_5,
        Text_6
    }

    private var state:OnboardState = OnboardState.Other

    companion object {
        /**
         * The user is coming from a notification. This notification is a wake-up attempt from
         * the onboard process.
         */
        const val ACTION_TEST_WAKE_UP="N0"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_onboard)

        fragmentManager.beginTransaction()
                .add(R.id.fragmentContainerInsetLayout, SplashFragment())
                .commit()

        GlobalScope.launch {

            delay(2000)

            //TODO check if intent is null
            if (intent?.action == ACTION_TEST_WAKE_UP) {
                state = OnboardState.Text_5
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
            OnboardState.Other ->{
                setTextFragment(R.string.onboard_welcome_text_01)
                state = OnboardState.text_1
            }
            OnboardState.text_1 -> {
                setTextFragment(R.string.onboard_welcome_text_02)
                state = OnboardState.text_2
            }
            OnboardState.text_2 -> {
                setTextFragment(R.string.onboard_welcome_text_03)
                state = OnboardState.text_3
            }
            OnboardState.text_3 -> {

                fragmentContainerInsetLayout.setOnClickListener(null)
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerInsetLayout, TimePickerFragment())
                        .commit()
                state = OnboardState.PickTime

            }

            OnboardState.PickTime -> {

                GlobalScope.launch {

                    delay(1000)
                    sendBroadcast(Intent(this@OnboardActivity, WakeUpReceiver::class.java)


                            /** We indicate that the receiver should treat the intent as
                             * part of the on-board process
                             */
                            .putExtra(WakeUpReceiver.WAKE_UP_KEY, WakeUpReceiver.TYPE_ON_BOARD))

                }
                setTextFragment(R.string.onboard_welcome_text_04)
                state = OnboardState.Text_5
            }

            OnboardState.Text_5 -> {

                setTextFragment(R.string.onboard_welcome_text_06)
                state = OnboardState.Text_6
                //TODO save state
            }
        }
    }

    /**
     * Show a fragment that contains a single text
     * @param textAddress is the ID of the string of text to display
     */
    private fun setTextFragment(textAddress:Int){

        /** check if we're not already displaying the fragment, if yes just update the text,
         * otherwise pass the address to its arguments**/
        val textFrag = fragmentManager.findFragmentByTag(TEXT_FRAGMENT_TAG)
        Log.d("setTextFragment","checking existence")
        if ((textFrag != null)) {
            if (textFrag.isVisible) {
                Log.d("setTextFragment","Fragment was found")
                (textFrag as InformationFragment).updateText(textAddress)
            }
        }else{

            Log.d("setTextFragment","making new fragment")
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerInsetLayout,InformationFragment.newInstance(textAddress,this) ,TEXT_FRAGMENT_TAG)
                    .commit()
        }
    }

    private fun nextState(){
        setNextFragment(state)
    }

    fun getForwardFunction() = { nextState() }
}
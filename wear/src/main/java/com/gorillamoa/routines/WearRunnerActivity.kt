package com.gorillamoa.routines

import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.widget.TextView
import com.gorillamoa.routines.views.TimerView

class WearRunnerActivity : WearableActivity() {

    private lateinit var timerView:TimerView
    private lateinit var timeCounter:TextView
    private lateinit var statusTextView:TextView
    private lateinit var exerciseRunner: ExerciseRunner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wear_runner)

        timerView = findViewById(R.id.timerView)
        timeCounter = findViewById(R.id.timeCounterTextView)
        statusTextView = findViewById(R.id.statusTextView)

        //run the exercise runner when the user clicks on start
        statusTextView.setOnClickListener { exerciseRunner.run() }

        timerView.setTextUpdateCallback { text ->
            runOnUiThread {
                timeCounter.text = text
            }
        }

        exerciseRunner = ExerciseRunner(Exersize(
                "W's",
                reps= 5,
                sets= 1,
                holdSeconds = 4,
                restSeconds = 4,
                automatic = true
        ),timerView,statusTextView)


        // Enables Always-on
        setAmbientEnabled()
    }

    override fun onResume() {
        super.onResume()
//        findViewById<TimerView>(R.id.timerView).elapsedTimeSecond = 15

    }



    /**
     * indicates to the time how
     */
    data class Exersize(
            var name:String,
            var sets:Int,
            var reps:Int,
            var holdSeconds:Int,
            var restSeconds:Int,
            var automatic:Boolean //if the exercise should progress automatically without user input
    )


    /**
     * The entity in charge of runnning an exercise
     * @param exercise the exercise details such as holds, reps and sets
     * @param the view that will display the exercise details
     */
    class ExerciseRunner(var exercise:Exersize, var timerView: TimerView, var statusTextView: TextView){

        enum class RunnerState{
            unprepared,
            prepared,
            runnning,
            finished
        }

        private var state:RunnerState = RunnerState.unprepared

        //progress
        var currentSet:Int = 0
        var currentRep: Int = 0

        public fun getState():RunnerState = state


        var finishCallback:()->Unit ={

            while(currentSet < exercise.sets){
                currentSet++
            }

        }


        /**
         * the runner will prepare the view for start
         */
        init{

            try {
                statusTextView.text = "Click to Start"

                //make sure to set to prepared, ready to go state
                state = RunnerState.prepared
            } catch (e: Exception) {
                state = RunnerState.unprepared
            }

        }


        fun runRep(){
            timerView.setCountdownSeconds(5)
            timerView.start(finishCallback)
        }

        fun runHold(){

        }

        /**
         * Will execute the timed exercises if it isn't already running and
         * it is prepared
         */
        fun run(){

            if (state == RunnerState.prepared) {

                //insert a start-countdown
                statusTextView.text = "Next: ${exercise.name} in"
                timerView.setCountdownSeconds(5)
                timerView.start(finishCallback)

                //for each set, run the timer reps number of times.
                //if automatic is enabled, don't wait for user input
                //if hold is on, insert an extra timer run in between reps


            }else{

            }
        }
    }


}

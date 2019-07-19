package com.gorillamoa.routines.onboard.fragments

import android.content.ComponentCallbacks
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gorillamoa.routines.onboard.R
import com.gorillamoa.routines.onboard.activities.OnboardActivity
import kotlinx.android.synthetic.main.fragment_task_suggestion.*

class TaskChooseFragment:Fragment(){

    companion object{

        private const val option1 = "one"
        private const val option2 = "two"
        private const val option3 = "three"
        private const val option4 = "add"

        fun newInstance(one:String,two:String,three:String) = TaskChooseFragment().apply {
            this.arguments = Bundle(1).apply {
                putString(option1, one)
                putString(option2,two)
                putString(option3,three)
            }
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_task_suggestion,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        choiceButtonOne.apply {
            text = arguments?.getString(option1)
            setOnClickListener {
                callbacks.invoke(arguments?.getString(option1)?:"")
            }
        }

        choiceButtonTwo.apply {
            text = arguments?.getString(option2)
            setOnClickListener {
                callbacks.invoke(arguments?.getString(option2)?:"")
            }
        }

        choiceButtonThree.apply {
            text = arguments?.getString(option3)
            setOnClickListener {
                callbacks.invoke(arguments?.getString(option3)?:"")
            }
        }

        addButton?.apply {
            text = "Add +"
            setOnClickListener {
                callbacks.invoke("add")
            }
        }

    }

    lateinit var callbacks:(String)->Any

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        callbacks = (context as OnboardActivity).getTaskChooseCallback()

    }

}
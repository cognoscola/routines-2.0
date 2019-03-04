package com.gorillamoa.routines.fragment

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gorillamoa.routines.R
import kotlinx.android.synthetic.main.fragment_information.*

class InformationFragment: Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_information,container,false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        infoTextView.text = arguments?.getString(resources.getString(R.string.info_argument_key))?:
                resources.getString(R.string.info_oops_string)
        Log.d("updateText","while resuming")
    }

    public  fun updateText(IDofValue: Int){
        infoTextView.text = resources.getString(IDofValue)?:
                resources.getString(R.string.info_oops_string)

        Log.d("updateText","while updating")
    }



    companion object {

        private val instance by lazy { InformationFragment() }

        /**
         * Get a new instance of this fragment, passing the address of
         * the text we'd like to show.
         * @param IDofValue of the text to show
         */
        fun newInstance(IDofValue:Int, context: Context) = instance.apply {

            this.arguments = Bundle(1).apply {
                putString(context.resources.getString(R.string.info_argument_key),
                        context.resources.getString(IDofValue))
            }
        }
    }

}
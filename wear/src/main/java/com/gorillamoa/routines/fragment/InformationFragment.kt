package com.gorillamoa.routines.fragment

import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.gorillamoa.routines.R
import kotlinx.android.synthetic.main.fragment_information.*

class InformationFragment: Fragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_information,container,false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            Toast.makeText(context, "bundle is null",Toast.LENGTH_SHORT).show()
        }

        infoTextView.text = arguments?.getString(resources.getString(R.string.info_argument_key))?:
                resources.getString(R.string.info_oops_string)
    }

}
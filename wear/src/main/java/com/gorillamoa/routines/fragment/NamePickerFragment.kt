package com.gorillamoa.routines.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gorillamoa.routines.R
import kotlinx.android.synthetic.main.fragment_name_picker.*

class NamePickerFragment : Fragment() {

    var submit:((String)->Any?)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_name_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        submitButton.setOnClickListener {

            val string= nameEditText.text.toString()
            if (string.isNotEmpty()) {

                submit?.invoke(nameEditText.text.toString())
            } else {
                nameEditText.error = context?.getString(R.string.Empty_error)
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(callback:((String)->Unit)?) = NamePickerFragment().apply {
            submit  = callback
        }
    }
}

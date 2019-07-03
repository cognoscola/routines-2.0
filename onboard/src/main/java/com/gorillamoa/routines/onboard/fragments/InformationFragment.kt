package com.gorillamoa.routines.onboard.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gorillamoa.routines.core.extensions.getHtml
import com.gorillamoa.routines.onboard.R
import kotlinx.android.synthetic.main.fragment_information.*

class InformationFragment: OnboardFragment(){

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_information,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        infoTextView.text = arguments?.getString(KEY_CONTENT)?:
                resources.getString(R.string.info_oops_string)
        welcomeTitleTextView?.text = getHtml(arguments?.getString(KEY_TITLE)?:resources.getString(R.string.info_oops_string))
        actionButton?.setText(arguments?.getString(KEY_ACTION))
        actionButton?.setOnClickListener {
            forwardFunction?.invoke()
        }
    }

    fun updateText(title:String, content:String, action:String){
        infoTextView.text = content
        welcomeTitleTextView.text = title
        actionButton.text = action
    }

    companion object {

        const val KEY_TITLE = "key_title"
        const val KEY_CONTENT = "key_content"
        const val KEY_ACTION = "key_action"
        private val instance by lazy { InformationFragment() }

        /**
         * Get a new instance of this fragment, passing the address of
         * the text we'd like to show.
         * @param IDofValue of the text to show
         */
        fun newInstance(title:String,content:String, action:String) = instance.apply {

            this.arguments = Bundle(1).apply {
                putString(KEY_TITLE, title)
                putString(KEY_CONTENT, content)
                putString(KEY_ACTION, action)
            }
        }
    }

}
package com.gorillamoa.routines.onboard.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gorillamoa.routines.onboard.R
import com.gorillamoa.routines.tools.getHtml
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

        val actionOneString = arguments?.getString(KEY_ACTION_ONE)
        if (actionOneString.isNullOrBlank()) {
            actionButtonOne.visibility = View.GONE
        } else{
            actionButtonOne?.text = actionOneString
            actionButtonOne?.setOnClickListener {
                forwardFunction?.invoke(KEY_STUBBORN)
            }
        }

        val actionTwoString = arguments?.getString(KEY_ACTION_TWO)
        if (actionTwoString.isNullOrBlank()) {
            actionButtonTwo.visibility = View.GONE
        } else{
            actionButtonTwo?.text = actionTwoString
            actionButtonTwo?.setOnClickListener {
                forwardFunction?.invoke(KEY_STUBBORN_NOT)
            }
        }

    }

    fun updateText(title:String, content:String, actionOne:String?, actionTwo:String?){
        infoTextView.text = getHtml(content)
        welcomeTitleTextView.text = title
        actionOne?.apply { actionButtonOne.text = this@apply }?:run{actionButtonOne.visibility = View.GONE}
        actionTwo?.apply { actionButtonOne.text = this@apply }?:run{actionButtonTwo.visibility = View.GONE}
    }

    companion object {

        const val KEY_TITLE = "key_title"
        const val KEY_CONTENT = "key_content"
        const val KEY_ACTION_ONE = "key_action_one"
        const val KEY_ACTION_TWO = "key_action_two"

        const val KEY_STUBBORN = 0
        const val KEY_STUBBORN_NOT = 1
        private val instance by lazy { InformationFragment() }

        /**
         * Get a new instance of this fragment, passing the address of
         * the text we'd like to show.
         * @param IDofValue of the text to show
         */
        fun newInstance(title:String, content:String, actionOne:String?,actionTwo: String?) = instance.apply {

            this.arguments = Bundle(1).apply {
                putString(KEY_TITLE, title)
                putString(KEY_CONTENT, content)
                putString(KEY_ACTION_ONE, actionOne)
                putString(KEY_ACTION_TWO, actionTwo)
            }
        }
    }


}

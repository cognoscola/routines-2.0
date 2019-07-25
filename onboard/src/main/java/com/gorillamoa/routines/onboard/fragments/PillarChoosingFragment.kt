package com.gorillamoa.routines.onboard.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gorillamoa.routines.onboard.R
import com.gorillamoa.routines.onboard.activities.OnboardActivity
import kotlinx.android.synthetic.main.fragment_pillar.*

class PillarChoosingFragment:Fragment(){

    lateinit var callback:(Pillar)->Any

    val revealHandler = Handler()

    enum class Pillar{

        Mental,
        Physical,
        Relationships,
        Play,
        Work

    }

    companion object{

        fun newInstance():PillarChoosingFragment =  PillarChoosingFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pillar,container,false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)





        mentalButton.setOnClickListener { callback.invoke(Pillar.Mental) }
        physicalButton.setOnClickListener { callback.invoke(Pillar.Physical) }
        relationshipButton.setOnClickListener { callback.invoke(Pillar.Relationships) }
        playButton.setOnClickListener { callback.invoke(Pillar.Play) }
        worklifeButton.setOnClickListener { callback.invoke(Pillar.Work) }

        //immediately show the first option
        mentalButton.visibility = View.VISIBLE

        revealHandler.postDelayed({
            //after 200 milliseconds, show the next button
            physicalButton.visibility = View.VISIBLE
            revealHandler.postDelayed({

                relationshipButton.visibility = View.VISIBLE
                revealHandler.postDelayed({

                    playButton.visibility = View.VISIBLE
                    revealHandler.postDelayed({

                        worklifeButton.visibility = View.VISIBLE
                    },
                            500)
                },
                        500)
            },
                    500)
        },500)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

       callback =  (context as OnboardActivity).getPillarChooseCallback()

    }


}



package com.gorillamoa.routines.onboard.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.gorillamoa.routines.onboard.R
import com.gorillamoa.routines.onboard.activities.OnboardActivity
import kotlinx.android.synthetic.main.fragment_pillar.*

class PillarChoosingFragment:Fragment(){

    lateinit var callback:(Pillar)->Any

    enum class Pillar{

        Mental,
        Physical,
        Relationships,
        Play,
        Work,
        Other

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
        otherButton.setOnClickListener { callback.invoke(Pillar.Other) }

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

       callback =  (context as OnboardActivity).getPillarChooseCallback()

    }


}



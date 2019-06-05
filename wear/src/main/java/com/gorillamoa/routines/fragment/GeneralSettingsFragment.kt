package com.gorillamoa.routines.fragment

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.gorillamoa.routines.R

class GeneralSettingsFragment: PreferenceFragmentCompat(){

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences_general, rootKey)
    }
}
package com.gorillamoa.routines.services

import android.util.Log
import android.widget.Toast
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.WearableListenerService

/**
 * Listens for data changes (in case we are synchronized with the mobile)
 */
class DataLayerListenerService:WearableListenerService(){

    @Suppress("unused")
    private val tag:String = DataLayerListenerService::class.java.name

    override fun onDataChanged(dataEvents: DataEventBuffer) {
       Log.d("$tag onDataChanged","$dataEvents")

        Toast.makeText(applicationContext,"Received",Toast.LENGTH_SHORT).show()
//        dataEvents.map { it.dataItem.uri}
//                .forEach {
//
//                }

    }

}
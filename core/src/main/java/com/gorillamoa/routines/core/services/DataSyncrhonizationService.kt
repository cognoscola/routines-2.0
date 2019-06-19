package com.gorillamoa.routines.core.services

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread

import com.google.android.gms.wearable.Wearable
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Node
import com.gorillamoa.routines.core.coroutines.Coroutines
import java.util.concurrent.ExecutionException


class DataSyncrhonizationService{

    companion object {

        @Suppress("unused")
        private val tag:String = DataSyncrhonizationService::class.java.name

        /**
         * Let the device know it should iniate the wake up event
         */
        const val EVENT_WAKEUP = "/event/wakeup"

        /**
         * Get a list of all wearable nodes that are connected synchronously.
         * Only call this method from a background thread (it should never be
         * called from the main/UI thread as it blocks).
         */
        @WorkerThread
        fun getNodes(context: Context):Collection<String>{
            val results = java.util.HashSet<String>()

            val nodeListTask = Wearable.getNodeClient(context.applicationContext).connectedNodes
            try {
                // Block on a task and get the result synchronously (because this is on a background
                // thread).
                val nodes = Tasks.await<List<Node>>(nodeListTask)

                for (node in nodes) {
                    results.add(node.getId())
                }

            } catch (exception: ExecutionException) {
                Log.e(tag, "Task failed: $exception")

            } catch (exception: InterruptedException) {
                Log.e(tag, "Interrupt occurred: $exception")
            }
            return results
        }

        @WorkerThread
        fun sendWakeUpEvent(node:String,context: Context){

            val sendMessageTask = Wearable.getMessageClient(context).sendMessage(node, EVENT_WAKEUP, ByteArray(0))

            try {
                // Block on a task and get the result synchronously (because this is on a background
                // thread).
                val result = Tasks.await(sendMessageTask)
                Log.d(tag, "Message sent: " + result!!)

            } catch (exception: ExecutionException) {
                Log.d(tag, "Task failed: $exception")

            } catch (exception: InterruptedException) {
                Log.d(tag, "Interrupt occurred: $exception")
            }

        }
    }
}


fun Context.remoteWakeUp(){

    Coroutines.ioThenMain({DataSyncrhonizationService.getNodes(this)}){
        it?.forEach {
            DataSyncrhonizationService.sendWakeUpEvent(it,this)
        }
    }
}
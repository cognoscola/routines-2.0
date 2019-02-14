package com.gorillamoa.routines.service

import android.app.Service
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.res.Configuration
import android.os.Binder
import android.os.IBinder
import android.util.Log
import java.io.FileDescriptor
import java.io.PrintWriter

class TaskSchedulerService: Service(),ScheduleServiceInterface{


    override fun onBind(intent: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
    }

    override fun dump(fd: FileDescriptor?, writer: PrintWriter?, args: Array<out String>?) {
        super.dump(fd, writer, args)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_NOT_STICKY


    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }


    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
    }


    /**
     * Provides a binding activityContex with Access to this Service'uid methods
     */
    inner class LocalBinder : Binder() {
        fun getService(): ScheduleServiceInterface {
            Log.d(TAG, "getService()")
            return this@TaskSchedulerService
        }
    }


    /****************************************************
     * INTERFACING
     *****************************************************/





}

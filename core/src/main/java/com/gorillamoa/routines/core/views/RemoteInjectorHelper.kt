package com.gorillamoa.routines.core.views

import android.app.Application
import com.google.gson.Gson

class RemoteInjectorHelper{

    companion object {

        @JvmStatic fun getRemoteViewGraphs(app:Application):RemoteViewGraph{
            if (app is RemoteGraphProvider) {
                return app.remoteViewGraph
            }else{
                throw IllegalStateException("The Application is not implementing RemoteGraphProvider")
            }
        }
    }

    interface RemoteGraphProvider{
        val remoteViewGraph:RemoteViewGraph
    }

    interface RemoteGsonProvider{
        fun getGson():Gson
    }
}
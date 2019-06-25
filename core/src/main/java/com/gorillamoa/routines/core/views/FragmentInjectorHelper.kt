package com.gorillamoa.routines.core.views

import android.app.Application
import java.lang.IllegalStateException

class FragmentInjectorHelper{
    companion object{

        @JvmStatic fun getFragmentGraph(app: Application):FragmentGraph{
            if(app is FragmentGraphProvider){
                return app.fragmentGraph
            }else{
                throw IllegalStateException("The Application is not implementing FragmentGraphProvider")
            }
        }
    }

    interface FragmentGraphProvider{
        val fragmentGraph:FragmentGraph
    }

}

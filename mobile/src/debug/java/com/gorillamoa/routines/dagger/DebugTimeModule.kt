package com.gorillamoa.routines.dagger

import dagger.Module
import dagger.Provides
import org.mockito.Mockito
import javax.inject.Singleton

@Module
class DebugTimeModule (private val mockMode:Boolean){

   /* @Provides
    @Singleton
    internal fun provideClock(){
        if(mockMode){

//            Mockito.mock()
        }
    }*/

}
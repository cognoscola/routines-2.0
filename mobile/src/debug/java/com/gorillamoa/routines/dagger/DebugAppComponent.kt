package com.gorillamoa.routines.dagger

import android.content.Context
import com.gorillamoa.routines.Utilities
import com.gorillamoa.routines.app.App
import javax.inject.Singleton


import dagger.Component

@Singleton
@Component
interface DebugAppComponent{

    fun inject(utilities: Utilities)
    fun inject(app:App)

   object Initializer {

       /*    fun init(mockMode: Boolean, context: Context): DebugAppComponent {

              return DaggerDebugAppComponent.builder()
                      .debugDataModule(DebugDataModule(mockMode))
                      .appModule(live.cya.android.dagger.AppModule(context))
                      .debugServiceModule(DebugServiceModule(mockMode, context))
                      .debugPaymentModule(DebugPaymentModule(mockMode, context))
                      .debugToolsProviderModule(DebugToolsProviderModule(mockMode))
                      .debugWebServicesModules(DebugWebServicesModules(mockMode))
                      .build()
          }*/
    }
}
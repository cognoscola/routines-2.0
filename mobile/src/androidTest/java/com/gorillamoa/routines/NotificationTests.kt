package com.gorillamoa.routines

import android.app.PendingIntent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry

import com.gorillamoa.routines.onboard.activities.OnboardActivity
import org.hamcrest.Matchers.startsWith

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Rule
import androidx.test.uiautomator.UiDevice
import org.junit.Before


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class NotificationTests {
    var mDevice: UiDevice? = null

    @get:Rule
    public val mActivityRule: InjectedActivityBaseTest<OnboardActivity> = InjectedActivityBaseTest(
            OnboardActivity::class.java,
            true, //initial touch mode
            false) //se we can configure an intent before launching

    @Before
    @Throws(Exception::class)
    fun setUp() {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @Test
    fun verifyWakeUpAlarmIsSet() {

        //Run the App Until we get to the Time Choose
    }

}

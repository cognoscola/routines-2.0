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
import com.gorillamoa.routines.core.extensions.createAlarmIntent
import com.gorillamoa.routines.core.extensions.isWakeAlarmSet
import com.gorillamoa.routines.core.receiver.AlarmReceiver
import com.gorillamoa.routines.core.receiver.AlarmReceiver.Companion.EVENT_WAKEUP
import org.junit.Assert.fail
import org.junit.Before


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class AlarmTests {


    var mDevice: UiDevice? = null

    @get:Rule
    public val mActivityRule: InjectedActivityBaseTest<BaseConfigActivity> = InjectedActivityBaseTest(
            BaseConfigActivity::class.java,
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
        mActivityRule.launchActivity(null)

        mDevice!!.pressHome()

        Thread.sleep(1000)

        mActivityRule.activity.sendBroadcast(mActivityRule.activity.application.createAlarmIntent().apply {
            action = EVENT_WAKEUP
        })

        //Verify alarm object got called
    }

}

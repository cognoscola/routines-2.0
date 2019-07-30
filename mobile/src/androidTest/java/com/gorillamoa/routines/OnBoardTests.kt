package com.gorillamoa.routines

import android.app.PendingIntent
import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import androidx.test.uiautomator.By
import com.gorillamoa.routines.onboard.activities.OnboardActivity
import org.hamcrest.Matchers.startsWith

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Rule
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import com.gorillamoa.routines.core.receiver.AlarmReceiver
import org.hamcrest.Matchers.containsString
import org.junit.Assert.fail
import org.junit.Before
import java.util.logging.Logger


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class OnBoardTests {


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
        mActivityRule.launchActivity(null)


        Thread.sleep(5000)

        onView(withText(startsWith("YES"))).perform(click())
        onView(withText(startsWith("I'M"))).perform(click())

        Thread.sleep(2000)
        onView(withText(startsWith("6"))).perform(click())
        onView(withText(startsWith("6 am"))).perform(click())
        Thread.sleep(1000)

        mDevice!!.pressHome()


        val alarmUp: Boolean = (PendingIntent.getBroadcast(mActivityRule.activity.applicationContext, 0,
                Intent(AlarmReceiver.EVENT_WAKEUP),
                PendingIntent.FLAG_NO_CREATE) != null)

        if(!alarmUp){
            fail("alarm was not up!")
        }


        /*onView(withText(startsWith("SEND"))).perform(click())

        Thread.sleep(1000)

        mDevice!!.openNotification()
        mDevice!!.wait(Until.hasObject(By.textStartsWith("Start")), 5000)
        val button: UiObject2 = mDevice!!.findObject(By.text("Start"))
        button.click()

        Thread.sleep(2000)

        onView(withText(containsString("M STUBBORN"))).perform(click())*/
    }
}

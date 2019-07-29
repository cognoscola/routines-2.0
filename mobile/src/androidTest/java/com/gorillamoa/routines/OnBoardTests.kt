package com.gorillamoa.routines

import androidx.test.runner.AndroidJUnit4
import com.gorillamoa.routines.onboard.activities.OnboardActivity

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class OnBoardTests {

    @get:Rule
    public val mActivityRule:InjectedActivityBaseTest<OnboardActivity> = InjectedActivityBaseTest(
            OnboardActivity::class.java,
            true, //initial touch mode
            false) //se we can configure an intent before launching
    @Test
    fun verifyWakeUpNotificationSet() {

        //Run the App Until we get to the Time Choose
        mActivityRule.launchActivity(null)



    }
}

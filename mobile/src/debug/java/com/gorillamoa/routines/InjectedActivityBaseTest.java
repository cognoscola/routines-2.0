package com.gorillamoa.routines;

import android.app.Activity;
import android.app.Instrumentation;

import androidx.fragment.app.FragmentActivity;
import androidx.test.rule.ActivityTestRule;

import com.gorillamoa.routines.app.App;

import org.junit.After;
import org.junit.Before;

import static androidx.test.InstrumentationRegistry.getInstrumentation;

public class InjectedActivityBaseTest<T extends FragmentActivity> extends ActivityTestRule {


    public InjectedActivityBaseTest(Class<T> activityClass, boolean inittialTouchMode, boolean launcActivity) {
        super(activityClass, inittialTouchMode, launcActivity);
    }


    private Utilities utilities;

    public Utilities getUtilities() {
        return this.utilities;
    }

    public interface BeforeOnCreateCallback {
        void beforeOnCreate();
    }

    public BeforeOnCreateCallback beforeOnCreateCallback;

    @Override
    protected void beforeActivityLaunched() {
        super.beforeActivityLaunched();

        utilities = new Utilities();
        Instrumentation instrumentation = getInstrumentation();
        App app = (App) instrumentation.getTargetContext().getApplicationContext();
        app.setMockMode(true);

        System.out.println("injecting utitlies");
        //Don't forget to inject!
//        app.graph.inject(utilities);

        if (beforeOnCreateCallback != null) {
            beforeOnCreateCallback.beforeOnCreate();

        }
    }

    @Before
    public void setup() {

    }

    @After
    public void tearDown() throws Exception{
        //TODO don't forget!
//        App.Companion.getInstance().setMockMode(false);

    }

}
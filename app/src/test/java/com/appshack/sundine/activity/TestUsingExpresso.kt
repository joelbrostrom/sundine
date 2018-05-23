package com.appshack.sundine.activity

import android.support.test.espresso.Espresso.*
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.appshack.sundine.main.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Created by joelbrostrom on 2018-04-18
 * Developed by App Shack
 */
@RunWith(AndroidJUnit4::class)

class HelloWorldEspressoTest {

    @Rule
    var mActivityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Test
    fun listGoesOverTheFold() {
        onView(withText("Hello world!")).check(matches(isDisplayed()))
    }
}

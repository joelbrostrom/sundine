package com.appshack.sundine

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test


/**
 * Created by joelbrostrom on 2018-04-19
 * Developed by App Shack
 */
class MainActivityUITest {

    @Rule
    @JvmField
    val activity = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Test
    fun shouldInflateLayout() {
        onView(withId(R.layout.activity_main)).check(matches(isDisplayed()))
        onView(withId(R.id.username_input_field)).check(matches(isDisplayed()))
        onView(withId(R.id.password_input_field)).check(matches(isDisplayed()))
        onView(withId(R.id.login_button)).check(matches(isDisplayed()))

    }
}
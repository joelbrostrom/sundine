package com.appshack.sundine

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.widget.TextView
import org.junit.Rule
import org.junit.Test

/**
 * Created by joelbrostrom on 2018-04-18
 * Developed by App Shack
 */
class LoginActivityUITest {

    @Rule
    @JvmField
    val activity = ActivityTestRule<LoginActivity>(LoginActivity::class.java)

    @Test
    fun checkVisibleViews() {
        onView(withId(R.id.activity_login)).check(matches(isDisplayed()))
        onView(withId(R.id.username_input_field)).check(matches(isDisplayed()))
        onView(withId(R.id.password_input_field)).check(matches(isDisplayed()))
        onView(withId(R.id.login_button)).check(matches(isDisplayed()))

    }

    @Test
    fun testInputFields() {
        onView(withId(R.id.username_input_field)).check { view, noViewFoundException ->
            val mTextView = view as TextView
            mTextView.text = "mEmail@mail.com"
        }

        onView(withId(R.id.password_input_field)).check { view, noViewFoundException ->
            val mTextView = view as TextView
            mTextView.text = "12345"

        }

        onView(withId(R.id.username_input_field)).check(matches(withText("mEmail@mail.com")))
        onView(withId(R.id.password_input_field)).check(matches(withText("12345")))

    }

    @Test
    fun testLoginButtonClick() {
        onView(withId(R.id.login_button)).perform(click())
    }

}
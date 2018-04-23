package com.appshack.sundine.activity

import com.appshack.sundine.LoginActivity
import org.junit.Test

/**
 * Created by joelbrostrom on 2018-04-18
 * Developed by App Shack
 */
class LoginActivityUnitTest {
    private val loginActivity = LoginActivity()

    @Test
    fun usernameValidation() {
        assert(loginActivity.validateUsername("test123"))
    }

    @Test
    fun passwordValidation() {
        assert(loginActivity.validatePassword("test123"))
    }

}
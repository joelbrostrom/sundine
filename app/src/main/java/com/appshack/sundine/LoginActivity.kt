package com.appshack.sundine

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*


/**
 * Created by joelbrostrom on 2018-04-18
 * Developed by App Shack
 */
class LoginActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupClickable()
    }

    private fun setupClickable() {
        login_button.setOnClickListener {
            validateCredentials()
        }
    }

    private fun validateCredentials(){

        if (email_input_field.text.toString() == "user@email.com"
            && password_input_field.text.toString() == "12345") {

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

        } else {
            Toast.makeText(this, "No match found", Toast.LENGTH_LONG).show()
        }
    }
}
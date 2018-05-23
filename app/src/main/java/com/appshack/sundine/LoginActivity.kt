package com.appshack.sundine

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.appshack.sundine.main.MainActivity
import com.appshack.sundine.suncanvas.SunCanvasActivity
import kotlinx.android.synthetic.main.activity_login.*


/**
 * Created by joelbrostrom on 2018-04-18
 * Developed by App Shack
 */
class LoginActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setupClickListeners()
    }

    private fun setupClickListeners() {
        login_button.setOnClickListener {
            validateCredentials()
        }

        canvas_test_view.setOnClickListener {
            val intent = Intent(this, SunCanvasActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun validateCredentials() {

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
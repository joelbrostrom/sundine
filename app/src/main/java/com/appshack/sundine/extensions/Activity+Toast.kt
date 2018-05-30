package com.appshack.sundine.extensions

import android.app.Activity
import android.widget.Toast


/**
 * Created by joelbrostrom on 2018-05-21
 * Developed by App Shack
 */

fun Activity.toast(message: String){
    runOnUiThread {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
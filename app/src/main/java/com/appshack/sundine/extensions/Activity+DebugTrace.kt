package com.appshack.sundine.extensions

import android.app.Activity


/**
 * Created by joelbrostrom on 2018-05-02
 * Developed by App Shack
 */

fun Activity.debugTrace(): String {
    val stackTrace = Thread.currentThread().stackTrace[3]
    val fullClassName = stackTrace.className
    val classname = fullClassName.substring(fullClassName.lastIndexOf(".") + 1)
    val functionName = stackTrace.methodName
    val lineNumber = stackTrace.className //add if needed

    return "@dev $classname::$functionName"
}
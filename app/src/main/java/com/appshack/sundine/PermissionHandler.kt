package com.appshack.sundine

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.support.v7.app.AlertDialog
import com.appshack.sundine.enums.PermissionCodes
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener


/**
 * Created by joelbrostrom on 2018-04-20
 * Developed by App Shack
 */

class PermissionHandler(private val activity: Activity,private val requiredPermissions: List<String>, val callBack: ((Unit) -> Unit)) {

    fun checkPermission() {
        Dexter.withActivity(activity)
                .withPermissions(requiredPermissions)
                .withListener(object : MultiplePermissionsListener {

                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {
                            if (it.areAllPermissionsGranted()) {
                                callBack.invoke(Unit)
                            } else {
                                openSettingsDialog("Permission Needed", "In order to use this feature you have to give the app permission. Since you have denied the app permission in the past, you must now give permission via the phones settings.")

                            }

                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                        token?.continuePermissionRequest()
                    }

                })
                .check()
    }

   private fun openSettingsDialog(title: String,
                          message: String,
                          positiveButton: String = "Open Settings",
                          negativeButton: String = "Dismiss") {


        AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveButton) { dialog, which ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", activity.packageName, null)
                    intent.data = uri
                    activity.startActivityForResult(intent, PermissionCodes.OPEN_SETTINGS.id)
                    dialog.cancel()
                }

                .setNegativeButton(negativeButton) { dialog, which -> dialog.cancel() }
                .show()

    }
}
package net.dublin.bus.ui.utilities


import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.support.v7.app.AlertDialog

import net.dublin.bus.R

object DialogUtil {

    fun showDialogPermission(context: Context, title: String, message: String) {
        showDialogPermission(context, title, message, null)
    }

    private fun showDialogPermission(context: Context?, title: String, message: String,
                                     listenerCancel: DialogInterface.OnClickListener?) {
        if (context == null) return

        val mCustomAlertDialog = AlertDialog.Builder(context)
        mCustomAlertDialog.setTitle(title)
        mCustomAlertDialog.setMessage(message)
        mCustomAlertDialog.setNegativeButton(R.string.permission_after, listenerCancel)
        mCustomAlertDialog.setPositiveButton(R.string.permission_settings) { dialog, _ ->
            dialog.dismiss()
            val intent = Intent()
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            val uri = Uri.fromParts("package", context.packageName, null)
            intent.data = uri
            context.startActivity(intent)
        }

        mCustomAlertDialog.show()
    }

    internal fun showAlertLocationDialog(context: Context?) {
        if (context == null) return

        val customAlertDialog = AlertDialog.Builder(context)
        customAlertDialog.setTitle(R.string.location_warning)
        customAlertDialog.setMessage(R.string.location_continue)
        customAlertDialog.setNegativeButton("Agora não") { dialog, _ -> dialog.dismiss() }
        customAlertDialog.setPositiveButton("Ativar") { dialog, _ ->
            dialog.dismiss()
            context.startActivity(Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
            ))
            dialog.dismiss()
        }
        customAlertDialog.show()
    }
}

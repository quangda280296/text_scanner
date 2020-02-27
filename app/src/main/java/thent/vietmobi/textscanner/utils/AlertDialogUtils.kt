package thent.vietmobi.textscanner.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface

object AlertDialogUtils {

    private var alertDialog: AlertDialog? = null

    fun showAlertDialog1(context: Context, strMsg: Int) {
        AlertDialog.Builder(context)
            .setCancelable(false)
            .setTitle(null)
            .setMessage(strMsg)
            .setPositiveButton("OK", null)
            .show()
    }

    fun showAlertDialog1(context: Context, title: Int, strMsg: Int) {
        AlertDialog.Builder(context)
            .setCancelable(false)
            .setTitle(title)
            .setMessage(strMsg)
            .setPositiveButton("OK", null)
            .show()
    }

    fun showAlertDialog1(context: Context, strMsg: String) {
        AlertDialog.Builder(context)
            .setCancelable(false)
            .setTitle(null)
            .setMessage(strMsg)
            .setPositiveButton("OK", null)
            .show()
    }

    fun showAlertDialog1(
        context: Context, title: String, ms: String, strTextOne: String,
        strTextTwo: String, okListener: DialogInterface.OnClickListener
    ) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setTitle(title)
        alertDialogBuilder.setMessage(ms)
        alertDialogBuilder.setPositiveButton(strTextOne, okListener)
        alertDialogBuilder.setNegativeButton(strTextTwo, null)
        alertDialogBuilder.setCancelable(false)
        alertDialog = alertDialogBuilder.create()
        alertDialog!!.show()
    }

    fun showAlertDialog2(
        context: Context, ms: String, strTextOne: String,
        strTextTwo: String, oklistener: DialogInterface.OnClickListener
    ) {
        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setMessage(ms)
        alertDialogBuilder.setPositiveButton(strTextTwo, oklistener)
        alertDialogBuilder.setNegativeButton(strTextOne, null)
        alertDialogBuilder.setCancelable(false)
        alertDialog = alertDialogBuilder.create()
        alertDialog!!.show()
    }

    fun closeAlertDialog() {
        try {
            alertDialog!!.dismiss()
        } catch (e: Exception) {
            // nothing
        }

    }
}
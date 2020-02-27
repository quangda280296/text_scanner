package thent.vietmobi.textscanner.utils

import android.R.attr.label
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager


object KeyboardUtils {
    fun hideSoftKeyboard(activity: Activity) {
        try {
            val inputMethodManager: InputMethodManager = activity.getSystemService(
                Activity.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activity.currentFocus?.windowToken, 0)
        } catch (e: Exception) {
        }
    }

    fun showSoftKeyboard(view: View) {
        try {
            if (view.requestFocus()) {
                val imm =
                    view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm?.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            }
        } catch (e: Exception) {
        }
    }

    fun handlerCopy(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label.toString(), text)
        clipboard.setPrimaryClip(clip)
    }
}
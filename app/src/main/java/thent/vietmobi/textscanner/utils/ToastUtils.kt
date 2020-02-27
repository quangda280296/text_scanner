package thent.vietmobi.textscanner.utils

import android.content.Context
import android.widget.Toast

object ToastUtils {
    fun toastShort(context: Context, content: String) {
        Toast.makeText(context, content, Toast.LENGTH_SHORT).show()
    }

    fun toastLong(context: Context, content: String) {
        Toast.makeText(context, content, Toast.LENGTH_LONG).show()
    }

    fun initToast(context: Context, content: String): Toast {
        return Toast.makeText(context, content, Toast.LENGTH_SHORT)
    }
}
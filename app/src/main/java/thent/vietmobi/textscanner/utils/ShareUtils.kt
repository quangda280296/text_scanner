package thent.vietmobi.textscanner.utils

import android.content.Context
import android.preference.PreferenceManager
import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ShareUtils {
    fun <T> put(context: Context?, key: String?, value: T?) {
        if (value == null || context == null) return
        val type = object : TypeToken<T>() {}.type
        val json = Gson().toJson(value, type)
        val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
        editor.putString(key, json)
        editor.apply()
    }

    operator fun <T> get(context: Context?, key: String?, type: Class<T>?): T? {
        if (context == null) return null
        val json = PreferenceManager.getDefaultSharedPreferences(context).getString(key, "")
        return if (!TextUtils.isEmpty(json)) Gson().fromJson(json, type)
        else null
    }
}
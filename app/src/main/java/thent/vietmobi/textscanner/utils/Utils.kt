package thent.vietmobi.textscanner.utils

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Context.WIFI_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.io.File
import java.util.concurrent.atomic.AtomicInteger


object Utils {

    private const val ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"

    fun postDelayed(runnable: Runnable, delayMillis: Long) {
        Handler(Looper.getMainLooper()).postDelayed(runnable, delayMillis)
    }

    var blnProcessDismiss = false

    fun getID(): Int {
        return AtomicInteger(0).incrementAndGet()
    }

    fun isImageFileCheck(mContext: Context, imageName: String): Boolean {
        val filePath = "/data/data/" + mContext.packageName + "/files/" + imageName
        val file = File(filePath)

        return file.exists()
    }


    fun isValidEmail(target: CharSequence?): Boolean {
        return if (target == null) {
            false
        } else {
            android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }
    }

    fun dip2px(context: Context, dipValue: Float): Int {
        val m = context.resources.displayMetrics.density
        return (dipValue * m + 0.5f).toInt()

    }

    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    fun randomAlphaNumeric(count: Int): String {
        var count = count
        val builder = StringBuilder()
        while (count-- != 0) {
            val character = (Math.random() * ALPHA_NUMERIC_STRING.length).toInt()
            builder.append(ALPHA_NUMERIC_STRING[character])
        }
        return builder.toString()
    }

    fun resizeBitmap(bitmap: Bitmap, resizeWidth: Float, resizeHeight: Float): Bitmap {

        val matrix = Matrix()
        val resizeScaleWidth: Float = resizeWidth / bitmap.width
        val resizeScaleHeight: Float = resizeHeight / bitmap.height
        matrix.postScale(resizeScaleWidth, resizeScaleHeight)

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun setBadge(context: Context, count: Int) {
        val launcherClassName = getLauncherClassName(context) ?: return
        val intent = Intent("android.intent.action.COUNTER_CHANGED")
        intent.putExtra("count", count)
        intent.putExtra("package", context.packageName)
        intent.putExtra("class", launcherClassName)
        context.sendBroadcast(intent)
    }

    private fun getLauncherClassName(context: Context): String? {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)

        val resolveInfo = pm.queryIntentActivities(intent, 0)
        for (resolveInfo in resolveInfo) {
            val pkgName = resolveInfo.activityInfo.applicationInfo.packageName
            if (pkgName.equals(context.packageName, ignoreCase = true)) {
                return resolveInfo.activityInfo.name
            }
        }
        return null
    }

    fun checkString(strData: String?): String {
        var strData = strData
        if (strData == null) {
            strData = ""
        }
        if (strData == "" || strData.isEmpty() || strData == "null") {
            strData = ""
        }

        return strData
    }

    fun stringToInt(strData: String?): Int {
        var strData = strData
        if (strData == null) {
            strData = "0"
        }
        if (strData == "" || strData.isEmpty() || strData == "null") {
            strData = "0"
        }

        return Integer.valueOf(strData)
    }

    fun telCheck(str: String): Boolean? {
        return str.matches("^0\\d{1,4}-\\d{1,4}-\\d{4}$".toRegex())
    }

    fun mailCheck(str: String): Boolean? {
        return str.matches("[\\w\\.\\-]+@(?:[\\w\\-]+\\.)+[\\w\\-]+".toRegex())
    }

    fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    fun isNumeric(str: String): Boolean {
        try {
            val d = java.lang.Double.parseDouble(str)
        } catch (nfe: NumberFormatException) {
            return false
        }

        return true
    }

    @SuppressLint("DefaultLocale")
    fun ipWifiAddress(context: Context): String {
        val ipString: String
        val wifiMgr = context.applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiMgr.connectionInfo
        val ip = wifiInfo.ipAddress
        ipString = String.format(
            "%d.%d.%d.%d",
            ip and 0xff,
            ip shr 8 and 0xff,
            ip shr 16 and 0xff,
            ip shr 24 and 0xff
        )
        return ipString
    }

    private fun addAutoStartup(context: Context) {
        try {
            val intent = Intent()
            val manufacturer = Build.MANUFACTURER
            if ("xiaomi".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
            } else if ("oppo".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.permission.startup.StartupAppListActivity"
                )
            } else if ("vivo".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName(
                    "com.vivo.permissionmanager",
                    "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                )
            } else if ("Letv".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName(
                    "com.letv.android.letvsafe",
                    "com.letv.android.letvsafe.AutobootManageActivity"
                )
            } else if ("Honor".equals(manufacturer, ignoreCase = true)) {
                intent.component = ComponentName(
                    "com.huawei.systemmanager",
                    "com.huawei.systemmanager.optimize.process.ProtectActivity"
                )
            }
            val list: List<ResolveInfo> =
                context.packageManager.queryIntentActivities(
                    intent,
                    PackageManager.MATCH_DEFAULT_ONLY
                )
            if (list.isNotEmpty()) {
                context.startActivity(intent)
            }
        } catch (e: Exception) {
            Log.e("exc", e.toString())
        }
    }

    fun checkXaiomi(): Boolean {
        val manufacturer = Build.MANUFACTURER
        if ("xiaomi".equals(manufacturer, ignoreCase = true)) {
            return true
        }
        return false
    }
}
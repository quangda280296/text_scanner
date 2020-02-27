package thent.vietmobi.textscanner.constant

import android.os.Environment
import thent.vietmobi.textscanner.R
import java.io.File

object Constant {
    var PACKAGE_PREFIX = "ImageConverter"
    val IMAGE_FILE_PATH =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + File.separator
    const val LINK = "LINK"
    const val MAIN = "MAIN"
    const val jpgExtension = ".jpg"
    const val pdfExtension = ".pdf"
    const val txtExtension = ".txt"
    const val dayFormat = "yyyyMMdd_HHmmss"
    var LINK_PRIVACY_POLiCY =
        "http://gamemobileglobal.com/api/apps/policy/text-scanner/privacy-policy.html"
    var IMAGE_FOLDER =
        Environment.getExternalStorageDirectory().toString() + File.separator + PACKAGE_PREFIX
    var CAMERA = "CAMERA"
    var SELECT = "SELECT"
    var DOCUMENT = "DOCUMENT"
    var ROTATE_0 = "ROTATE_0"
    var ROTATE_90 = "ROTATE_90"
    var ROTATE_180 = "ROTATE_180"
    var ROTATE_270 = "ROTATE_270"
    const val TITLE = "TITLE"
    const val PASSWORD = "PASSWORD"
    const val BOOLEAN = "BOOLEAN"
}
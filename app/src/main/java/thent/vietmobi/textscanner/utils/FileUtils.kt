package thent.vietmobi.textscanner.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.util.Log
import androidx.core.content.FileProvider
import com.blankj.utilcode.util.FileUtils
import com.itextpdf.text.pdf.PdfReader
import thent.vietmobi.textscanner.R
import thent.vietmobi.textscanner.constant.Constant
import thent.vietmobi.textscanner.model.ItemMediaModel
import thent.vietmobi.textscanner.model.ItemPDF
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.net.URLConnection
import java.text.DecimalFormat


object FileUtils {
    @SuppressLint("NewApi")
    fun getPath(uri: Uri, context: Context): String? {
        var uri = uri
        val needToCheckUri = Build.VERSION.SDK_INT >= 19
        var selection: String? = null
        var selectionArgs: Array<String>? = null
        if (needToCheckUri && DocumentsContract.isDocumentUri(context, uri)) {
            when {
                isExternalStorageDocument(uri) -> {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split =
                        docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    return Constant.IMAGE_FILE_PATH + "/" + split[1]
                }
                isDownloadsDocument(uri) -> {
                    val id = DocumentsContract.getDocumentId(uri)
                    if (id.startsWith("raw:")) {
                        return id.replaceFirst("raw:".toRegex(), "")
                    }
                    uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(id)
                    )
                }
                isMediaDocument(uri) -> {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split =
                        docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    when (split[0]) {
                        "image" -> uri = Images.Media.EXTERNAL_CONTENT_URI
                        "video" -> uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        "audio" -> uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    selection = "_id=?"
                    selectionArgs = arrayOf(split[1])
                }
            }
        }
        if ("content".equals(uri.scheme!!, ignoreCase = true)) {
            val projection = arrayOf(Images.Media.DATA)
            try {
                context.contentResolver.query(
                    uri, projection, selection, selectionArgs, null
                )!!.use { cursor ->
                    if (cursor != null && cursor.moveToFirst()) {
                        val columnIndex =
                            cursor.getColumnIndexOrThrow(Images.Media.DATA)
                        return cursor.getString(columnIndex)
                    }
                }
            } catch (e: Exception) {
                Log.e("on getPath", "Exception", e)
            }

        } else if ("file".equals(uri.scheme!!, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    @SuppressLint("InlinedApi")
    fun addImageToGallery(filePath: String, activity: Activity) {
        try {
            val values = ContentValues()
            values.put(Images.Media.DATE_TAKEN, System.currentTimeMillis())
            values.put(Images.Media.MIME_TYPE, "image/jpeg")
            values.put(MediaStore.MediaColumns.DATA, filePath)
            activity.contentResolver.insert(
                Images.Media.EXTERNAL_CONTENT_URI, values
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getImageUri(context: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = Images.Media.insertImage(context.contentResolver, inImage, "Title", null)
        return Uri.parse(path)
    }

    fun shareFile(myFilePath: String, context: Context) {
        val intentShareFile = Intent(Intent.ACTION_SEND)
        val fileWithinMyDir = File(myFilePath)
        if (fileWithinMyDir.exists()) {
            val uriShare: Uri = FileProvider.getUriForFile(
                context, context.getString(
                    R.string.file_provider, context.applicationContext.packageName.toString()
                ), fileWithinMyDir
            )
            intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intentShareFile.type = URLConnection.guessContentTypeFromName(fileWithinMyDir.name)
            intentShareFile.putExtra(Intent.EXTRA_STREAM, uriShare)
            intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Sharing File...")
            intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File...")
            context.startActivity(Intent.createChooser(intentShareFile, "Share File"))
        }
    }

    fun shareListFile(myFilePath: ArrayList<ItemPDF>, context: Context) {
        val intentShareFile = Intent(Intent.ACTION_SEND_MULTIPLE)
        val arrayUri = ArrayList<Uri>()
        for (i in 0 until myFilePath.size) {
            val fileWithinMyDir = File(myFilePath[i].path!!)
            if (fileWithinMyDir.exists()) {
                val uriShare: Uri = FileProvider.getUriForFile(
                    context, context.getString(
                        R.string.file_provider, context.applicationContext.packageName.toString()
                    ), fileWithinMyDir
                )
                arrayUri.add(uriShare)
            }
        }
        intentShareFile.type = "application/pdf"
        intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intentShareFile.putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayUri)
        intentShareFile.putExtra(Intent.EXTRA_SUBJECT, "Sharing Multiple File...")
        intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing Multiple File...")
        context.startActivity(Intent.createChooser(intentShareFile, "Share Multiple File"))
    }

    @SuppressLint("InlinedApi")
    fun loadImage(activity: Activity): ArrayList<ItemMediaModel> {
        var imageCursor: Cursor? = null
        var list = ArrayList<ItemMediaModel>()
        try {
            val columns = arrayOf(
                Images.Media.DATA,
                Images.Media.BUCKET_ID,
                Images.Media.BUCKET_DISPLAY_NAME,
                Images.Media.DISPLAY_NAME
            )
            val orderBy = Images.Media.DATE_ADDED + " DESC"

            val selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                    + " AND "
                    + Images.Media.DATA + " LIKE "
                    + "' '")

            imageCursor = activity.contentResolver.query(
                MediaStore.Files.getContentUri("external"), columns, selection, null, orderBy
            )
            if (imageCursor != null) {
                val maxIndex: Int = imageCursor.count
                val itemMediaList = ArrayList<ItemMediaModel>()
                for (i in 0 until maxIndex) {
                    imageCursor.moveToPosition(i)
                    val imageLocation =
                        imageCursor.getString(imageCursor.getColumnIndex(Images.Media.DATA))
                    val imageFile = File(imageLocation)
                    if (imageFile.exists()) {
                        itemMediaList.add(ItemMediaModel(Uri.fromFile(imageFile).toString()))
                    }
                }
                list.addAll(itemMediaList)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            imageCursor?.close()
        }
        return list
    }

    fun openPDFFile(path: String?, isPdf: Boolean, context: Context) {
        val file = File(path!!)
        val target = Intent(Intent.ACTION_VIEW)
        target.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        try {
            val uri = FileProvider.getUriForFile(
                context, context.getString(
                    R.string.file_provider, context.applicationContext.packageName.toString()
                ), file
            )
            if (isPdf) {
                target.setDataAndType(uri, "application/pdf")
            } else {
                target.setDataAndType(uri, "text/plain")
            }
            target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(
                Intent.createChooser(target, context.getString(R.string.open_file_))
            )
        } catch (e: Exception) {
            e.stackTrace
        }
    }

    fun getAllFilePDF(context: Context, dir: File): ArrayList<ItemPDF> {
        val files: Array<File> = dir.listFiles()!!
        val list = ArrayList<ItemPDF>()
        for (file in files) {
            if (file.isFile) {
                val suffix: String = getSuffix(file.name).toString()
                var isPDF: Boolean
                isPDF = suffix == "pdf"
                if (!checkPassword(file)) {
                    list.add(
                        ItemPDF(
                            file.name, formatFileSize(file.length()),
                            file.path, TimeUtils.formatLongToDateTime(
                                FileUtils.getFileLastModified(file.path),
                                false, TimeUtils.DATE_FORMAT_TIME
                            ), isPDF
                        )
                    )
                } else {
                    list.add(
                        ItemPDF(
                            file.name, formatFileSize(file.length()),
                            file.path, TimeUtils.formatLongToDateTime(
                                FileUtils.getFileLastModified(file.path),
                                false, TimeUtils.DATE_FORMAT_TIME
                            ), isPDF, context.getString(R.string.app_name)
                        )
                    )
                }
            } else if (file.isDirectory) {
                getAllFilePDF(context, file.absoluteFile)
            }
        }
        return list
    }

    private fun checkPassword(file: File): Boolean {
        return try {
            val pdfReader = PdfReader(java.lang.String.valueOf(file))
            pdfReader.isEncrypted
            false
        } catch (e: IOException) {
            e.printStackTrace()
            true
        }
    }

    private fun getSuffix(nameFile: String): String? {
        return nameFile.substring(nameFile.lastIndexOf(".") + 1).toLowerCase()
    }

    fun formatFileSize(size: Long): String? {
        var hrSize: String?
        val b = size.toDouble()
        val k = size / 1024.0
        val m = size / 1024.0 / 1024.0
        val g = size / 1024.0 / 1024.0 / 1024.0
        val t = size / 1024.0 / 1024.0 / 1024.0 / 1024.0
        val dec = DecimalFormat("0.00")
        hrSize = when {
            t > 1 -> {
                dec.format(t) + (" TB")
            }
            g > 1 -> {
                dec.format(g) + (" GB")
            }
            m > 1 -> {
                dec.format(m) + (" MB")
            }
            k > 1 -> {
                dec.format(k) + (" KB")
            }
            else -> {
                dec.format(b) + (" Bytes")
            }
        }
        return hrSize
    }

    fun createFolder() {
        val folder = File(Constant.IMAGE_FOLDER)
        if (!folder.exists()) {
            folder.mkdirs()
        }
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }
}
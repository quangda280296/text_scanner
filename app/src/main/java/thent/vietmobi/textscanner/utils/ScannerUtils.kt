package thent.vietmobi.textscanner.utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.DisplayMetrics
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer
import thent.vietmobi.textscanner.R
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import kotlin.collections.ArrayList


object ScannerUtils {
    fun inspect(uri: Uri, context: Context, rotate: Float): String {
        var inputStream: InputStream? = null
        var bitmap: Bitmap? = null
        try {
            inputStream = context.contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            options.inSampleSize = 2
            options.inScreenDensity = DisplayMetrics.DENSITY_LOW
            bitmap = BitmapFactory.decodeStream(inputStream, null, options)
            return inspectFromBitmap(bitmap!!, context, rotate)
        } catch (e: FileNotFoundException) {
            e.stackTrace
        } finally {
            bitmap?.recycle()
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.stackTrace
                }
            }
        }
        return null.toString()
    }

    private fun inspectFromBitmap(bitmap: Bitmap, context: Context, rotate: Float): String {
        val detectedText = StringBuilder()
        val textRecognizer = TextRecognizer.Builder(context).build()
        try {
            if (!textRecognizer.isOperational) {
                AlertDialog.Builder(context).setMessage(context.getString(R.string.text_scan_error))
                    .show()
                return null.toString()
            }
            val frame = Frame.Builder().setBitmap(rotateBitmap(bitmap, rotate)).build()
            val origTextBlocks = textRecognizer.detect(frame)
            val textBlocks = ArrayList<TextBlock>()
            for (i in 0 until origTextBlocks.size()) {
                val textBlock = origTextBlocks.valueAt(i)
                textBlocks.add(textBlock)
            }
            for (textBlock in textBlocks) {
                if (textBlock.value != null) {
                    detectedText.append(textBlock.value)
                    detectedText.append("\n")
                }
            }
        } catch (e: Exception) {
            e.stackTrace
        } finally {
            textRecognizer.release()
        }
        return detectedText.toString()
    }

    private fun rotateBitmap(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
}
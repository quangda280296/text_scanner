package thent.vietmobi.textscanner.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import thent.vietmobi.textscanner.model.ItemMediaModel


object AlbumUtils {

    @SuppressLint("InlinedApi", "Recycle")
    fun getAllShownImagesPath(activity: Activity): ArrayList<ItemMediaModel> {
        val cursorBucket: Cursor
        val column_index_data: Int
        val listOfAllImages = ArrayList<String>()
        var absolutePathOfImage: String?
        val listItemMediaModel = ArrayList<ItemMediaModel>()
        val selectionArgs = arrayOf("%%")

        val orderBy = MediaStore.Images.Media.DATE_ADDED + " DESC"
        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Images.Media.DATA + " like ? "

        val projectionOnlyBucket =
            arrayOf(MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

        cursorBucket = activity.contentResolver.query(
            uri, projectionOnlyBucket, selection, selectionArgs, orderBy
        )!!

        column_index_data = cursorBucket.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
        while (cursorBucket.moveToNext()) {
            absolutePathOfImage = cursorBucket.getString(column_index_data)
            if (absolutePathOfImage != "" && absolutePathOfImage != null)
                listOfAllImages.add(absolutePathOfImage)
        }

        for (i in 0 until listOfAllImages.size) {
            listItemMediaModel.add(ItemMediaModel(listOfAllImages[i]))
        }
        return listItemMediaModel
    }

}
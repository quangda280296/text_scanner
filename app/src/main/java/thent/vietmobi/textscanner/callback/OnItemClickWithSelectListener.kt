package thent.vietmobi.textscanner.callback

import android.widget.ImageView
import thent.vietmobi.textscanner.databinding.ItemFileBinding
import thent.vietmobi.textscanner.model.ItemPDF

interface OnItemClickWithSelectListener {
    fun onItemClicked(itemPDF: ItemPDF)

    fun onItemImageClicked(itemPDF: ItemPDF, imageView: ImageView)

    fun onItemLongClicked(itemFile: ItemFileBinding)

    fun onItemSelected(position:Int)
}

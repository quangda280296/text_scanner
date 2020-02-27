package thent.vietmobi.textscanner.callback

import thent.vietmobi.textscanner.model.ItemPDF

interface OnItemClickListener {
    fun onItemClickClicked(itemPDF: ItemPDF)

    fun onItemClickWithPosition(position:Int)
}

package thent.vietmobi.textscanner.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import thent.vietmobi.textscanner.R
import thent.vietmobi.textscanner.callback.OnItemClickWithSelectListener
import thent.vietmobi.textscanner.databinding.ItemFileBinding
import thent.vietmobi.textscanner.model.ItemPDF

class MyDocumentPDFAdapter(
    private val list: ArrayList<ItemPDF>, var context: Activity,
    private val onItemClickWithSelectListener: OnItemClickWithSelectListener
) : RecyclerView.Adapter<MyDocumentPDFAdapter.MyViewHolder>() {
    var showOptions = false

    inner class MyViewHolder internal constructor(internal var itemFile: ItemFileBinding) :
        RecyclerView.ViewHolder(itemFile.root)

    override fun getItemCount(): Int {
        return list.size
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemPDF: ItemFileBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.item_file, parent, false
        )
        return MyViewHolder(itemPDF)
    }

    override fun onBindViewHolder(@NonNull holder: MyViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val itemPDF = list[position]
        holder.itemFile.txtName.text = itemPDF.nameEndPoint
        holder.itemFile.txtData.text = itemPDF.dataKB
        holder.itemFile.txtTime.text = itemPDF.createAt
        if (itemPDF.isPDF) {
            Glide.with(context).load(R.drawable.pdf_file).into(holder.itemFile.imgAvatar)
        } else {
            Glide.with(context).load(R.drawable.txt_file).into(holder.itemFile.imgAvatar)
        }
        if (itemPDF.selected) {
            Glide.with(context).load(R.drawable.ic_check_box_activated)
                .into(holder.itemFile.imgCheckBox)
        } else {
            Glide.with(context).load(R.drawable.ic_disable).into(holder.itemFile.imgCheckBox)
        }

        if (showOptions) {
            holder.itemFile.imgSelected.visibility = View.GONE
            holder.itemFile.imgCheckBox.visibility = View.VISIBLE
            holder.itemView.setOnClickListener {}
            holder.itemView.setOnLongClickListener { false }
        } else {
            holder.itemFile.imgSelected.visibility = View.VISIBLE
            holder.itemFile.imgCheckBox.visibility = View.GONE

            holder.itemView.setOnClickListener {
                onItemClickWithSelectListener.onItemClicked(itemPDF)
            }
            holder.itemView.setOnLongClickListener {
                onItemClickWithSelectListener.onItemLongClicked(holder.itemFile)
                false
            }
        }
        holder.itemFile.imgSelected.setOnClickListener {
            onItemClickWithSelectListener.onItemImageClicked(
                itemPDF, holder.itemFile.imgSelected
            )
        }
        holder.itemFile.imgCheckBox.setOnClickListener {
            onItemClickWithSelectListener.onItemSelected(position)
        }
    }

    fun getList(): ArrayList<ItemPDF> {
        return list
    }

    fun setOption(key: Boolean): Boolean {
        showOptions = key
        return showOptions
    }

    fun setAllSelected(isShowOption: Boolean) {
        for (i in 0 until list.size) list[i].selected = isShowOption
        notifyDataSetChanged()
    }

    fun handlerGetListSelected(): ArrayList<ItemPDF> {
        val listSelected = ArrayList<ItemPDF>()
        for (i in 0 until list.size) if (list[i].selected) listSelected.add(list[i])
        return listSelected
    }
}

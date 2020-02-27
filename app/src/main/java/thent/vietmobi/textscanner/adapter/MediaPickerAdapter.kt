package thent.vietmobi.textscanner.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import thent.vietmobi.textscanner.R
import thent.vietmobi.textscanner.callback.OnItemClickListener
import thent.vietmobi.textscanner.databinding.ItemImageBinding
import thent.vietmobi.textscanner.model.ItemMediaModel

class MediaPickerAdapter(
    private val context: Context,
    private val list: ArrayList<ItemMediaModel>,
    private val onItemClickListener: OnItemClickListener
) : RecyclerView.Adapter<MediaPickerAdapter.MyViewHolder>() {

    inner class MyViewHolder internal constructor(var itemImageBinding: ItemImageBinding) :
        RecyclerView.ViewHolder(itemImageBinding.root)

    override fun getItemCount(): Int {
        return list.size
    }

    @NonNull
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemImageBinding: ItemImageBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_image, parent, false
        )
        return MyViewHolder(itemImageBinding)
    }

    override fun onBindViewHolder(@NonNull holder: MyViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val imageMedia = list[position]
        Glide.with(context).load(imageMedia.uri).into(holder.itemImageBinding.imgMain)
        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClickWithPosition(position)
            holder.itemImageBinding.imageGradient.visibility = View.VISIBLE
            Handler().postDelayed({
                holder.itemImageBinding.imageGradient.visibility = View.GONE
            }, 50)
        }
    }
}

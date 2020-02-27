package thent.vietmobi.textscanner.utils

import android.annotation.SuppressLint
import android.widget.ImageView
import androidx.annotation.IdRes
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import de.hdodenhof.circleimageview.CircleImageView

object BindUtils {
    @JvmStatic
    @BindingAdapter(value = ["imageUrl"], requireAll = false)
    fun loadImage(view: ImageView, image: String) {
        Glide.with(view.context).load(image).diskCacheStrategy(DiskCacheStrategy.ALL).into(view)
    }

    @JvmStatic
    @BindingAdapter(value = ["circleImageUrl"], requireAll = false)
    fun loadImage(view: CircleImageView, image: String) {
        Glide.with(view.context).load(image).diskCacheStrategy(DiskCacheStrategy.ALL).into(view)
    }

    @SuppressLint("ResourceType")
    @JvmStatic
    @BindingAdapter(value = ["imageSource"], requireAll = false)
    fun loadImage(view: ImageView, @IdRes image: Int) {
        view.setImageResource(image)
    }

    @SuppressLint("ResourceType")
    @JvmStatic
    @BindingAdapter(value = ["circleSource"], requireAll = false)
    fun loadImage(view: CircleImageView, @IdRes image: Int) {
        view.setImageResource(image)
    }
}
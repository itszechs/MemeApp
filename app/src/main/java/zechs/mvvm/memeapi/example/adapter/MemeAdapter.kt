package zechs.mvvm.memeapi.example.adapter

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import zechs.mvvm.memeapi.example.ThisApp.Companion.context
import zechs.mvvm.memeapi.example.databinding.MemeItemBinding
import zechs.mvvm.memeapi.example.models.Meme
import zechs.mvvm.memeapi.example.utils.GlideApp

class MemeAdapter : RecyclerView.Adapter<MemeAdapter.MemeViewHolder>() {

    class MemeViewHolder(private val itemBinding: MemeItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(meme: Meme) {
            context?.let {
                meme.url?.let { url ->
                    if (url.endsWith("png") or url.endsWith("jpg")) {
                        GlideApp.with(it)
                            .asBitmap()
                            .format(DecodeFormat.PREFER_ARGB_8888)
                            .load(url)
                            .listener(object : RequestListener<Bitmap?> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any,
                                    target: Target<Bitmap?>,
                                    isFirstResource: Boolean,
                                ): Boolean {
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Bitmap?,
                                    model: Any,
                                    target: Target<Bitmap?>,
                                    dataSource: DataSource,
                                    isFirstResource: Boolean,
                                ): Boolean {
                                    itemBinding.root.setOnClickListener { _ ->
                                        val i = Intent(Intent.ACTION_SEND)
                                        val path =
                                            MediaStore.Images.Media.insertImage(
                                                it.contentResolver,
                                                resource,
                                                "",
                                                null)

                                        i.putExtra(Intent.EXTRA_STREAM, Uri.parse(path))
                                        i.type = "image/*"

                                        val chooserIntent =
                                            Intent.createChooser(i, "Share image")
                                        chooserIntent.flags = FLAG_ACTIVITY_NEW_TASK
                                        i.putExtra(Intent.EXTRA_STREAM, Uri.parse(path))
                                        it.startActivity(chooserIntent)
                                    }
                                    return false
                                }
                            })
                            .into(itemBinding.imageView)
                    }
                }
            }
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<Meme>() {
        override fun areItemsTheSame(oldItem: Meme, newItem: Meme): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Meme, newItem: Meme): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemeViewHolder {
        val itemBinding =
            MemeItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MemeViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: MemeViewHolder, position: Int) {
        val meme = differ.currentList[position]
        holder.bind(meme)
    }
}
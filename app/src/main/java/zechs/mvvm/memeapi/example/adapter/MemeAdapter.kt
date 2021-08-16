package zechs.mvvm.memeapi.example.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import zechs.mvvm.memeapi.example.ThisApp.Companion.context
import zechs.mvvm.memeapi.example.databinding.MemeItemBinding
import zechs.mvvm.memeapi.example.models.Meme
import zechs.mvvm.memeapi.example.utils.GlideApp


class MemeAdapter : RecyclerView.Adapter<MemeAdapter.MemeViewHolder>() {

    class MemeViewHolder(private val itemBinding: MemeItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(meme: Meme) {
            context?.let {
                if (!meme.url.endsWith("gif")) {
                    GlideApp.with(it)
                        .asBitmap()
                        .load(meme.url)
                        .into(itemBinding.imageView)
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
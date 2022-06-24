package com.rakuseru.storyapp1.ui.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rakuseru.storyapp1.data.ListStory
import com.rakuseru.storyapp1.databinding.ItemListStoryBinding
import com.rakuseru.storyapp1.ui.DetailActivity

class ListStoryAdapter(private val listStory: List<ListStory>):
    RecyclerView.Adapter<ListStoryAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemListStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listStory[position])
        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, DetailActivity::class.java)
            intent.putExtra(DetailActivity.EXTRA_STORY, listStory[position])
            it.context.startActivity(intent,
                ActivityOptionsCompat
                    .makeSceneTransitionAnimation(it.context as Activity)
                    .toBundle()
            )
        }
    }

    // ViewHolder
    class ListViewHolder(private var binding: ItemListStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind (itemData: ListStory) {
            binding.tvItemName.text = itemData.name
            Glide.with(itemView.context)
                .load(itemData.photoUrl)
                .into(binding.ivItemPhoto)
        }
    }

    override fun getItemCount(): Int = listStory.size

    // Callbacks
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ListStory)
    }

}
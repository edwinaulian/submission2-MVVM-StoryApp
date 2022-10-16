package com.storyApp.edwin.mainStoryApp.view.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.storyApp.edwin.mainStoryApp.databinding.ItemStoryBinding
import com.storyApp.edwin.mainStoryApp.model.ListStory
import com.storyApp.edwin.mainStoryApp.view.detail.DetailActivity

class MainAdapter():
    PagingDataAdapter<ListStory, MainAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    inner class StoryViewHolder(val binding: ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ListStory) {
            binding.apply {
                Glide.with(itemView)
                    .load(item.photoUrl)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .circleCrop()
                    .into(ivItemPhoto)
                tvItemName.text = item.name
            }
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.DATA, item)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
//        holder.bind(list[position])
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

//    override fun getItemCount(): Int = list.size

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStory>() {
            override fun areItemsTheSame(oldItem: ListStory, newItem: ListStory): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStory, newItem: ListStory): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
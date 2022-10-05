package com.storyApp.edwin.mainStoryApp.view.detail

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.storyApp.edwin.mainStoryApp.databinding.ActivityDetailBinding
import com.storyApp.edwin.mainStoryApp.model.ListStory

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // get data with bundle
        val data = intent.getParcelableExtra<ListStory>(DATA)
        showLoading(true)

        binding.apply {
            showLoading(false)
            tvName.text = data?.name
            tvDescription.text = data?.description
            Glide.with(this@DetailActivity)
                .load(data?.photoUrl)
                .circleCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(tvAvatar)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBarActDetail.visibility = View.VISIBLE
        } else {
            binding.progressBarActDetail.visibility = View.GONE
        }
    }

    companion object {
        const val DATA = "data"
    }

}
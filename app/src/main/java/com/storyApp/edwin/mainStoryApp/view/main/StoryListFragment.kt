package com.storyApp.edwin.mainStoryApp.view.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.storyApp.edwin.mainStoryApp.R
import com.storyApp.edwin.mainStoryApp.databinding.FragmentStoryListBinding
import com.storyApp.edwin.mainStoryApp.model.UserModel
import com.storyApp.edwin.mainStoryApp.network.StoryResponseItem

class StoryListFragment(newList: PagingData<StoryResponseItem>, user: UserModel) : Fragment(R.layout.fragment_story_list) {

    private var bindingData: FragmentStoryListBinding? = null
    private val binding get() = bindingData!!
//    private val _newList = newList
    private val _user = user
    private lateinit var mainViewModel: MainViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingData = FragmentStoryListBinding.bind(view)
        setUpData()
    }

    private fun setUpData() {
        binding.apply {
//            nameUserLogin.text = getString(R.string.greeting, _user.name)
            rvStory.setHasFixedSize(true)
            rvStory.layoutManager = LinearLayoutManager(context)
            rvStory.adapter = MainAdapter()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_story_list, container, false)
    }

}
package com.storyApp.edwin.mainStoryApp.data

import androidx.lifecycle.LiveData
import androidx.paging.*
import com.storyApp.edwin.mainStoryApp.network.StoryResponseItem
import com.storyApp.edwin.mainStoryApp.service.Api

class StoryRepository(private val apiService: Api) {

    fun getStory(): LiveData<PagingData<StoryResponseItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService)
            }
        ).liveData
    }
}
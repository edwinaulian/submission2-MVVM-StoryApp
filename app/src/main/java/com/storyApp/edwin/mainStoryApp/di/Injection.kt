package  com.storyApp.edwin.mainStoryApp.di

import com.storyApp.edwin.mainStoryApp.data.StoryRepository
import com.storyApp.edwin.mainStoryApp.service.RetrofitClient

object Injection {
    fun provideRepository(token: String): StoryRepository {
        val apiService = RetrofitClient.create(token)
        return StoryRepository(apiService)
    }
}
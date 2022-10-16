package com.storyApp.edwin.mainStoryApp.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.storyApp.edwin.mainStoryApp.data.StoryRepository
import com.storyApp.edwin.mainStoryApp.di.Injection
import com.storyApp.edwin.mainStoryApp.model.UserPreference
import com.storyApp.edwin.mainStoryApp.view.add.AddStoryViewModel
import com.storyApp.edwin.mainStoryApp.view.login.LoginViewModel
import com.storyApp.edwin.mainStoryApp.view.main.MainViewModel
import com.storyApp.edwin.mainStoryApp.view.signup.SignupViewModel

class ViewModelFactory(private val pref: UserPreference, token: String) : ViewModelProvider.NewInstanceFactory() {

    private var _token = token

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(pref, Injection.provideRepository(_token)) as T
            }
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(pref) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(pref) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(pref) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}
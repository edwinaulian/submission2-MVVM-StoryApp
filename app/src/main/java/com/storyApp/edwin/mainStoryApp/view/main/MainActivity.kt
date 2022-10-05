package com.storyApp.edwin.mainStoryApp.view.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.storyApp.edwin.mainStoryApp.R
import com.storyApp.edwin.mainStoryApp.databinding.ActivityMainBinding
import com.storyApp.edwin.mainStoryApp.model.UserModel
import com.storyApp.edwin.mainStoryApp.model.UserPreference
import com.storyApp.edwin.mainStoryApp.view.ViewModelFactory
import com.storyApp.edwin.mainStoryApp.view.add.AddStoryActivity
import com.storyApp.edwin.mainStoryApp.view.maps.MapsFragment
import com.storyApp.edwin.mainStoryApp.view.welcome.WelcomeActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity() : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setContentView(R.layout.activity_main)

        setupView()
        setupViewModel()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(this, ViewModelFactory(UserPreference.getInstance(dataStore)))[MainViewModel::class.java]
        mainViewModel.getUser().observe(this) { user ->
            if (user.isLogin) {
                getAllStory(user.token)
                token = user.token
                setUpMenu(user)
            } else {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }
        }
    }

    private fun setUpMenu(user: UserModel) {
        mainViewModel.story.observe(this) { newList ->
            val listStoryFragment = StoryListFragment(newList, user)
            val mapsFragment = MapsFragment(token)
            setCurrentFragment(listStoryFragment)
            binding.bottomNavigationView.setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.person -> setCurrentFragment(listStoryFragment)
                    R.id.map -> setCurrentFragment(mapsFragment)
                }
                true
            }
        }
    }

    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment, fragment)
            commit()
        }

    private fun getAllStory(token: String) {
        binding.apply {
            if (token != null) {
                mainViewModel.getAllStory(token)
            }
        }
    }

    private fun setupAction() {
        binding.logoutButton.setOnClickListener {
            getIntent().removeExtra("TOKEN");
            applicationContext.getSharedPreferences("TOKEN", 0).edit().clear().commit()
            mainViewModel.logout()
        }

        binding.buttonAdd.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }
    }

    private fun playAnimation() {
        val logout = ObjectAnimator.ofFloat(binding.logoutButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(logout)
            startDelay = 400
        }.start()
    }

}
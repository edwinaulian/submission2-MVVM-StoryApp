package com.storyApp.edwin.mainStoryApp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.storyApp.edwin.mainStoryApp.databinding.ActivityLoginBinding
import com.storyApp.edwin.mainStoryApp.model.LoginResponse
import com.storyApp.edwin.mainStoryApp.model.UserModel
import com.storyApp.edwin.mainStoryApp.model.UserPreference
import com.storyApp.edwin.mainStoryApp.model.request.LoginRequest
import com.storyApp.edwin.mainStoryApp.service.RetrofitClient
import com.storyApp.edwin.mainStoryApp.view.ViewModelFactory
import com.storyApp.edwin.mainStoryApp.view.main.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LoginActivity : AppCompatActivity() {
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var binding: ActivityLoginBinding
    private lateinit var user: UserModel
    private lateinit var myButton: Button
    private lateinit var myEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myButton = binding.loginButton
        myEditText = binding.passwordEditText

        setMyButtonEnable()
        setupView()
        setupViewModel()
        setupAction()
        playAnimation()

        myEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                setMyButtonEnable()
            }
            override fun afterTextChanged(s: Editable) {
            }
        })
    }

    private fun setMyButtonEnable() {
        val result = myEditText.text
        myButton.isEnabled = result != null && result.toString().isNotEmpty()
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
        loginViewModel = ViewModelProvider(this, ViewModelFactory(UserPreference.getInstance(dataStore)))[LoginViewModel::class.java]
        loginViewModel.getUser().observe(this, { user ->
            this.user = user
        })
    }

    private fun setupAction() {
        binding.showHideBtn.setOnClickListener {
            if(binding.showHideBtn.text.toString().equals("Show Password")){
                binding.passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                binding.showHideBtn.text = "Hide Password"
            } else{
                binding.passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                binding.showHideBtn.text = "Show Password"
            }
        }

        myButton.setOnClickListener {
            setupLogin()
        }
    }

    companion object {
        const val TOKEN = "TOKEN"
    }

    private fun setupLogin() {
        val request = LoginRequest()
        request.email =  binding.emailEditText.text.toString().trim()
        request.password = binding.passwordEditText.text.toString().trim()
        val client = RetrofitClient.loginVal().login(request)
        client.enqueue(object: Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error!!) {
                        if (responseBody?.message == "success" && responseBody?.loginResult != null) {
                            val name = responseBody?.loginResult?.name.toString()
                            val token = responseBody?.loginResult?.token.toString()
                            loginViewModel.login(name, token)
                            allertDialogSuccesLogin(responseBody?.loginResult?.token.toString())
                        }
                        Toast.makeText(this@LoginActivity, responseBody.message, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@LoginActivity, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "${t.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun allertDialogSuccesLogin (token: String) {
        AlertDialog.Builder(this).apply {
                        setTitle("Selamat")
                        setMessage("Anda berhasil login.")
                        setPositiveButton("Lanjut") { _, _ ->
                            val bundle = Bundle()
                            bundle.putString(TOKEN, token)
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            intent.putExtras(bundle)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                        create()
                        show()
                    }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(500)
        val message = ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(500)
        val emailTextView = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding.emailEditText, View.ALPHA, 1f).setDuration(500)
        val passwordTextView = ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(500)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding.passwordEditText, View.ALPHA, 1f).setDuration(500)
        val showHidePass = ObjectAnimator.ofFloat(binding.showHideBtn, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, message, emailTextView, emailEditTextLayout, passwordTextView, passwordEditTextLayout, showHidePass, login)
            startDelay = 500
        }.start()
    }

}
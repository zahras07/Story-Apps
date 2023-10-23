package com.aya.storyapps2.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityOptionsCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.aya.storyapps2.R
import com.aya.storyapps2.databinding.ActivityLoginBinding
import com.aya.storyapps2.viewmodel.LoginViewModel
import com.aya.storyapps2.viewmodel.ViewModelFactory
import com.aya.storyapps2.dataclass.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class LoginActivity : AppCompatActivity() {

    private var _binding : ActivityLoginBinding? = null
    private val binding get() = _binding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        loginViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(dataStore))[LoginViewModel::class.java]

        loading(false)
        setupView()
        playAnimation()

        binding?.loginButton?.setOnClickListener {
            loginAction()
        }
        binding?.btnRegister?.setOnClickListener {
            intentToRegistration()
        }
    }

    private fun loginAction() {
        val email = binding?.edLoginEmail?.text.toString()
        val password = binding?.edLoginPassword?.text.toString()

        loginViewModel.login(email, password).observe(this@LoginActivity){ result ->
            if (result != null) {
                when (result) {
                    is Result.Loading -> {
                        loading(true)
                    }
                    is Result.Success-> {
                        loading(false)
                        Toast.makeText(this@LoginActivity, getString(R.string.success), Toast.LENGTH_SHORT).show()
                        CoroutineScope(Dispatchers.Main).launch {
                            delay(2000)
                            val intent =
                                Intent(this@LoginActivity, HomeActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(
                                intent,
                                ActivityOptionsCompat.makeSceneTransitionAnimation(this@LoginActivity as Activity)
                                    .toBundle()
                            )
                        }
                    }
                    is Result.Error -> {
                        loading(false)
                        Toast.makeText(this@LoginActivity, getString(R.string.failed), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    private fun intentToRegistration(){
        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
        startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this@LoginActivity as Activity).toBundle())
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        supportActionBar?.hide()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding?.imageLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding?.loginText, View.ALPHA, 1f).setDuration(300)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding?.edLoginEmail, View.ALPHA, 1f).setDuration(300)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding?.edLoginPassword, View.ALPHA, 1f).setDuration(300)
        val login = ObjectAnimator.ofFloat(binding?.loginButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, emailEditTextLayout, passwordEditTextLayout, login)
            startDelay = 300
        }.start()
    }

    private fun loading(isLoading: Boolean) {
        binding?.pb1?.visibility =
            if (isLoading) View.VISIBLE
            else View.GONE
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
package com.aya.storyapps2.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.aya.storyapps2.R
import com.aya.storyapps2.databinding.ActivityRegisterBinding
import com.aya.storyapps2.viewmodel.ViewModelFactory
import com.aya.storyapps2.dataclass.Result
import com.aya.storyapps2.viewmodel.RegisterViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class RegisterActivity : AppCompatActivity() {

    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding

    private lateinit var registrationViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        registrationViewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(dataStore)
        )[RegisterViewModel::class.java]

        loading(false)
        setupView()
        playAnimation()

        binding?.registerButton?.setOnClickListener {
            registerAction()
        }
        binding?.btnLogin?.setOnClickListener {
            finish()
        }
    }

    private fun registerAction() {
        val username = binding?.edRegisterName?.text.toString()
        val email = binding?.edRegisterEmail?.text.toString()
        val password = binding?.edRegisterPassword?.text.toString()

        when {
            username.isEmpty() -> {
                binding?.edRegisterName?.error = "Enter Username"
            }
            email.isEmpty() or !Patterns.EMAIL_ADDRESS.matcher(email).matches()-> {
                binding?.edRegisterEmail?.error = "Invalid email"
            }
            password.isEmpty()-> {
                binding?.edRegisterPassword?.error = "Enter password"
            }
            password.length < 6->{
                binding?.edRegisterPassword?.error = "Password must contain at least 6 characters!"
            }
            else -> {
                registrationViewModel.createUserRegister(username, email, password)
                    .observe(this@RegisterActivity) { result ->
                        if (result != null) {
                            when (result) {
                                is Result.Loading -> {
                                    loading(true)
                                }
                                is Result.Success -> {
                                    loading(false)
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        getString(R.string.register_ok),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    CoroutineScope(Dispatchers.Main).launch {
                                        delay(2000)
                                        finish()
                                    }
                                }
                                is Result.Error -> {
                                    loading(false)
                                    Toast.makeText(
                                        this@RegisterActivity,
                                        getString(R.string.register_failed),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }

                    }
            }
        }
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

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding?.imageView5, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding?.loginText, View.ALPHA, 1f).setDuration(500)
        val image = ObjectAnimator.ofFloat(binding?.imageView2, View.ALPHA, 1f).setDuration(500)
        val nameEditTextLayout = ObjectAnimator.ofFloat(binding?.edRegisterName, View.ALPHA, 1f).setDuration(500)
        val emailEditTextLayout = ObjectAnimator.ofFloat(binding?.edRegisterEmail, View.ALPHA, 1f).setDuration(500)
        val passwordEditTextLayout = ObjectAnimator.ofFloat(binding?.edRegisterPassword, View.ALPHA, 1f).setDuration(500)
        val signup = ObjectAnimator.ofFloat(binding?.registerButton, View.ALPHA, 1f).setDuration(500)


        AnimatorSet().apply {
            playSequentially(
                title,
                image,
                nameEditTextLayout,
                emailEditTextLayout,
                passwordEditTextLayout,
                signup
            )
            startDelay = 500
        }.start()
    }

    private fun loading(isLoading: Boolean) {
        binding?.pb2?.visibility =
            if (isLoading) View.VISIBLE
            else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

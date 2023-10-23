package com.aya.storyapps2.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.aya.storyapps2.databinding.ActivityHomeBinding
import com.aya.storyapps2.viewmodel.HomeViewModel
import com.aya.storyapps2.viewmodel.SettingViewModel
import com.aya.storyapps2.viewmodel.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class HomeActivity : AppCompatActivity() {

    private var _binding : ActivityHomeBinding? = null
    private val binding get() = _binding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var settingViewModel: SettingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        homeViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(dataStore))[HomeViewModel::class.java]
        settingViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(dataStore))[SettingViewModel::class.java]

        loading(false)

        binding?.rvStory?.layoutManager = LinearLayoutManager(this)
        HomeAction()

        binding?.btnMap?.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
        binding?.btnLogout?.setOnClickListener {
            settingViewModel.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        binding?.fbtn?.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loading(false)
        HomeAction()
    }

    private fun HomeAction() {
        val adapter = HomeAdapter()
        binding?.rvStory?.adapter = adapter

        settingViewModel.getUser().observe(this@HomeActivity){
            loading(true)
            homeViewModel.getStories(it.token).observe(this@HomeActivity){ data ->
                loading(false)
                adapter.submitData(lifecycle, data)
            }
        }
    }

    private fun loading(isLoading: Boolean) {
        binding?.pb4?.visibility =
            if (isLoading) View.VISIBLE
            else View.GONE
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

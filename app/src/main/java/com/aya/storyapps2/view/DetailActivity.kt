package com.aya.storyapps2.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.aya.storyapps2.R
import com.aya.storyapps2.databinding.ActivityDetailBinding
import com.bumptech.glide.Glide
import com.aya.storyapps2.dataclass.Result
import com.aya.storyapps2.responses.Story
import com.aya.storyapps2.viewmodel.DetailViewModel
import com.aya.storyapps2.viewmodel.SettingViewModel
import com.aya.storyapps2.viewmodel.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DetailActivity : AppCompatActivity() {

    private var _binding : ActivityDetailBinding? = null
    private val binding get() = _binding
    private lateinit var detailViewModel: DetailViewModel
    private lateinit var settingViewModel: SettingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = "Detail Story"
        loading(false)

        detailViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(dataStore))[DetailViewModel::class.java]
        settingViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(dataStore))[SettingViewModel::class.java]

        val id = intent.getStringExtra(EXTRA_ID)
        Log.v("id", id.toString())

        detailAction(id)
    }

    private fun detailAction(id: String?) {
        settingViewModel.getUser().observe(this@DetailActivity){
            detailViewModel.getDetail(it.token, id!!).observe(this@DetailActivity){ result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            loading(true)
                        }
                        is Result.Success -> {
                            loading(false)
                            val data = result.data
                            setupView(data)
                            Toast.makeText(this@DetailActivity, getString(R.string.story_ok), Toast.LENGTH_SHORT).show()
                        }
                        is Result.Error -> {
                            loading(false)
                            Toast.makeText(this@DetailActivity, getString(R.string.story_failed), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

    }

    private fun setupView(story: Story){
        Glide.with(this)
            .load(story.photoUrl)
            .into(binding?.ivDetailPhoto!!)
        Log.v("story view", story.name.toString())
        binding?.tvDetailName?.text = story.name
        binding?.tvDetailDescription?.text = story.description
    }

    private fun loading(isLoading: Boolean) {
        binding?.pb3?.visibility =
            if (isLoading) View.VISIBLE
            else View.GONE
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object{
        const val EXTRA_ID = "extra_id"
    }
}
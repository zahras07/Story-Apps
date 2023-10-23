package com.aya.storyapps2.view

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.aya.storyapps2.R
import com.aya.storyapps2.databinding.ActivityMapsBinding
import com.aya.storyapps2.responses.MapsList
import com.aya.storyapps2.viewmodel.MapsViewModel
import com.aya.storyapps2.viewmodel.SettingViewModel
import com.aya.storyapps2.viewmodel.ViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.aya.storyapps2.dataclass.Result


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mapsViewModel: MapsViewModel
    private lateinit var settingViewModel: SettingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Maps"

        mapsViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(dataStore))[MapsViewModel::class.java]
        settingViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(dataStore))[SettingViewModel::class.java]

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.maps) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        settingViewModel.getUser().observe(this@MapsActivity){
            mapsViewModel.getMaps(it.token).observe(this@MapsActivity){ result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            Toast.makeText(this@MapsActivity, getString(R.string.loading), Toast.LENGTH_SHORT).show()
                        }
                        is Result.Success -> {
                            val data = result.data
                            addMarker(data)
                            Toast.makeText(this@MapsActivity, getString(R.string.success), Toast.LENGTH_SHORT).show()
                        }
                        is Result.Error -> {
                            Toast.makeText(this@MapsActivity, getString(R.string.failed), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
    }

    private fun addMarker(list: List<MapsList>) {
        for (data in list){
            val latLng = LatLng(data.lat!!.toDouble(), data.lon!!.toDouble())
            mMap.addMarker(MarkerOptions().position(latLng).title(data.name).snippet(data.description))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }

    }

}
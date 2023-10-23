package com.aya.storyapps2.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.aya.storyapps2.R
import com.aya.storyapps2.databinding.ActivityUploadBinding
import com.aya.storyapps2.rotateBitmap
import com.aya.storyapps2.uriToFile
import com.aya.storyapps2.viewmodel.SettingViewModel
import com.aya.storyapps2.viewmodel.UploadViewModel
import com.aya.storyapps2.viewmodel.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import com.aya.storyapps2.dataclass.Result
import java.io.FileOutputStream

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
@Suppress("DEPRECATION")
class UploadActivity : AppCompatActivity() {

    private var _binding : ActivityUploadBinding? = null
    private val binding get() = _binding
    private lateinit var uploadViewModel: UploadViewModel
    private lateinit var settingViewModel: SettingViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var getFile : File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        loading(false)
        supportActionBar?.title = "Upload"

        uploadViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(dataStore))[UploadViewModel::class.java]
        settingViewModel = ViewModelProvider(this, ViewModelFactory.getInstance(dataStore))[SettingViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        allow()

        binding?.btnGallery?.setOnClickListener {
            gallery()
        }
        binding?.btnCamera?.setOnClickListener {
            startCameraX()
        }
        binding?.btnUpload?.setOnClickListener {
            upload()
        }
    }

    private fun reduceFileSize(file: File): File{
        val bitmap = BitmapFactory.decodeFile(file.path)
        var compressQuality = 100
        var streamLenght: Int

        do{
            val bmpStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
            val bmpPicByteArray = bmpStream.toByteArray()
            streamLenght = bmpPicByteArray.size
            compressQuality -= 5
        } while(streamLenght > 1000000)
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))

        return file
    }

    private fun upload() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),  100)
            return
        }
        when{
            getFile == null -> {
                Toast.makeText(this@UploadActivity, getString(R.string.select), Toast.LENGTH_SHORT).show()
            }

            binding?.storyDescription?.text?.length!! <= 0 -> {
                binding?.storyDescription?.error = getString(R.string.empty_des)

            }
            else -> {
                fusedLocationClient.lastLocation.addOnSuccessListener {
                    if(it != null) {
                        try {
                            val file = reduceFileSize(getFile as File)
                            val description = binding?.storyDescription?.text.toString()
                                .toRequestBody("text/plain".toMediaType())
                            val requestImageFile =
                                file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                            val imageMultipart: MultipartBody.Part =
                                MultipartBody.Part.createFormData(
                                    "photo",
                                    file.name,
                                    requestImageFile
                                )

                            AlertDialog.Builder(this).apply {
                                setTitle(getString(R.string.alert))
                                setMessage(getString(R.string.upload_confirm))
                                setNegativeButton(getString(R.string.no)) { _, _ -> }
                                setPositiveButton(getString(R.string.yes)) { _, _ ->

                                    settingViewModel.getUser().observe(this@UploadActivity){ user ->
                                        if(user.token.isNotEmpty()){
                                            uploadViewModel.uploadToServer(
                                                user.token,
                                                description,
                                                imageMultipart,
                                                it.latitude.toFloat(),
                                                it.longitude.toFloat()
                                            ).observe(this@UploadActivity){ result ->
                                                if (result != null) {
                                                    when (result) {
                                                        is Result.Loading -> {
                                                            loading(true)
                                                        }
                                                        is Result.Success -> {
                                                            loading(false)
                                                            Toast.makeText(this@UploadActivity, getString(R.string.upload_ok), Toast.LENGTH_SHORT).show()
                                                            CoroutineScope(Dispatchers.Main).launch {
                                                                delay(5000)
                                                                val intent =
                                                                    Intent(this@UploadActivity, HomeActivity::class.java)
                                                                intent.flags =
                                                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                                                startActivity(
                                                                    intent,
                                                                    ActivityOptionsCompat.makeSceneTransitionAnimation(this@UploadActivity as Activity)
                                                                        .toBundle()
                                                                )
                                                            }
                                                        }
                                                        is Result.Error -> {
                                                            loading(false)
                                                            Toast.makeText(this@UploadActivity, getString(R.string.upload_failed), Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                }

                                            }
                                        }
                                    }

                                }
                                create()
                                show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Toast.makeText(this, "Lokasi belum dinyalakan", Toast.LENGTH_SHORT).show()
                    }

                }

            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if(it.resultCode == RESULT_OK){
            val selectedImg: Uri = it.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@UploadActivity)
            getFile = myFile
            binding?.imgUpload?.setImageURI(selectedImg)
        }
    }
    private fun gallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        launcherIntentGallery.launch(intent)
    }


    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            getFile = myFile
            val result = rotateBitmap(
                BitmapFactory.decodeFile(getFile?.path),
                isBackCamera
            )

            binding?.imgUpload?.setImageBitmap(result)
        }
    }
    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun allow(){
        if(!allPermisionGranted()){
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSION, REQUEST_CODE_PERMISSION)
        }
    }

    private fun allPermisionGranted() = REQUIRED_PERMISSION.all{
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == REQUEST_CODE_PERMISSION){
            if(!allPermisionGranted()){
                Toast.makeText(
                    this,
                    getString(R.string.allow),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }

    }
    private fun loading(isLoading: Boolean) {
        binding?.pb5?.visibility =
            if (isLoading) View.VISIBLE
            else View.GONE
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object{
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSION = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSION = 10
    }
}
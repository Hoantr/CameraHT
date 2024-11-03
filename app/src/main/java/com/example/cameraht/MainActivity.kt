package com.example.cameraht

import android.os.Bundle
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.Surface
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Locale
import android.util.Size
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy


class MainActivity : ComponentActivity() {
    private lateinit var previewView: PreviewView
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>
    private var cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private lateinit var imageCapture: ImageCapture

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        previewView = findViewById(R.id.previewView)
        cameraExecutor = Executors.newSingleThreadExecutor()
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted = permissions.entries.all { it.value }
            if (granted) {
                startCamera()
            } else {
                showPermissionDeniedMessage(permissions)
            }
        }

        requestCameraPermission()


        findViewById<ImageButton>(R.id.btnGallery).setOnClickListener {
            val intent = Intent(this, ListImageActivity::class.java)
            startActivity(intent)
        }


        findViewById<ImageButton>(R.id.btnCapture).setOnClickListener {
            takePicture()
        }

        findViewById<ImageButton>(R.id.btnChangeCamera).setOnClickListener {
            val cameraProvider = ProcessCameraProvider.getInstance(this).get()
            cameraProvider.unbindAll()
            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }
            startCamera()
        }



    }

    private fun requestCameraPermission() {
        val permissions = when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU -> {
                // Từ Android 12 trở xuống
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                )
            }
            Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU -> {
                // Android 13
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO
                )
            }
            else -> {
                // Android 14 trở lên
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                )
            }
        }

        requestPermissionLauncher.launch(permissions)
    }




    private fun showPermissionDeniedMessage(access: Map<String, Boolean>) {
        lateinit var accessName: String
        for ((key, value) in access) {
            if(!value){
                if (key=="android.permission.CAMERA"){
                    accessName="Camera"
                    break
                }
                if(key=="android.permission.RECORD_AUDIO"){
                    accessName="Ghi Âm"
                    break
                }else{
                    accessName="Bộ Nhớ"
                }
            }
        }

            MaterialAlertDialogBuilder(this)
                .setTitle("Yêu cầu quyền")
                .setMessage("$accessName đã bị từ chối quyền truy cập \n" +
                        "Bạn có thể bật các quyền trong \n" +
                        "Cài đặt > Quyền riêng tư > Quản lý quyền")
                .setPositiveButton("Cài đặt") { _, _ ->
                    openAppSettings()
                }
                .setNegativeButton("Hủy") { dialog , _ ->
                    dialog.dismiss()
                    exitApp()
                }
                .show()

    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
        exitApp()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            imageCapture = ImageCapture.Builder()
                .setResolutionSelector(
                    ResolutionSelector.Builder()
                        .setResolutionStrategy(
                            ResolutionStrategy(
                                Size(1080, 1920),
                                ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER
                            )
                        )
                        .build()
                )
                .setTargetRotation(Surface.ROTATION_0)
                .build()

            // Tạo Preview cho CameraX
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)

        }, ContextCompat.getMainExecutor(this))
    }


    private fun takePicture() {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_" + SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
            put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/Camera") // Hoặc "Pictures/MyApp" nếu muốn
        }

        // Chụp ảnh và lưu
        val outputOptions = ImageCapture.OutputFileOptions.Builder(contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("CameraHT", "Chụp ảnh thất bại: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Toast.makeText(baseContext, "Ảnh đã được lưu", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun exitApp(){
        finishAffinity()
    }

    companion object {
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    }
}


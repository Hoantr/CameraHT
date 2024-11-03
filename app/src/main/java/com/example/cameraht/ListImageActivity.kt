package com.example.cameraht

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cameraht.adapter.ImageAdapter
import com.example.cameraht.adapter.ImageAdapter.AdapterListListener

class ListImageActivity : ComponentActivity(),AdapterListListener {
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    lateinit var adapterrm : ImageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_image)


        val images = loadImagesFromStorage()


        setupRecyclerView(images)

        activityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imagePath = result.data?.getStringExtra("del_image_uri")
                imagePath?.let {
                    val imageUri = Uri.parse(it)
                    removeImageById(imageUri)
                }
            }
        }

    }

    private fun setupRecyclerView(images: MutableList<Uri>) {
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        var adapter= ImageAdapter(images,this)
        adapterrm =adapter
        recyclerView.adapter = adapter
    }


    private fun loadImagesFromStorage(): MutableList<Uri> {
        val images = mutableListOf<Uri>()
        val contentResolver: ContentResolver = contentResolver
        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_TAKEN

        )

        val cursor = contentResolver.query(uri, projection, null, null, "${MediaStore.Images.Media.DATE_TAKEN} DESC")

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val imageUri = Uri.withAppendedPath(uri, id.toString())
                images.add(imageUri)
            }
        }

        if (images.isEmpty()) {
            Toast.makeText(this, "No images found", Toast.LENGTH_SHORT).show()
        }
        return images
    }

    private fun removeImageById(uri: Uri) {
        adapterrm.removeItem(uri)
    }

    override fun sendUri(imageUri: Uri) {
        val deleteIntent = Intent(this, ViewImageActivity::class.java)
        deleteIntent.putExtra("image_uri", imageUri.toString())
        activityResultLauncher.launch(deleteIntent)
    }
}
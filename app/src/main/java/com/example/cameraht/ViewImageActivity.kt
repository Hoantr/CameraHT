package com.example.cameraht

import android.annotation.SuppressLint
import android.app.RecoverableSecurityException
import android.content.ContentResolver
import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ViewImageActivity : ComponentActivity() {

    private lateinit var menuLayout: LinearLayout
    private lateinit var vPhotoEditorView: PhotoEditorView
    private lateinit var vPhotoEditor: PhotoEditor
    var pinchTextScalable = true
    private lateinit var pendingIntent: IntentSender
    private val DELETE_PERMISSION_REQUEST = 1
    private lateinit var deleteUri: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)
        menuLayout = findViewById(R.id.menuImageLayout)
        val imagePath = intent.getStringExtra("image_uri")
        vPhotoEditorView = findViewById(R.id.photoEditorView)

        var imageUri: Uri? = imagePath?.let { Uri.parse(it) }

        vPhotoEditorView.source.setImageURI(imageUri)

        vPhotoEditorView.setOnClickListener {
            toggleMenu()
        }

        findViewById<Button>(R.id.btnShare).setOnClickListener {
            if (imageUri != null) {
                shareImage(imageUri)
            }
        }

        findViewById<Button>(R.id.btnEdit).setOnClickListener {
            val intent = Intent(this, EditImageActivity::class.java)
            intent.putExtra("image_uri", imageUri.toString())
            startActivity(intent)
        }

        findViewById<Button>(R.id.btnDelete).setOnClickListener {
            if (imageUri != null) {
                deleteUri=imageUri

                trashImage(imageUri)
            } else {
                Toast.makeText(this, "URL Null", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btnInfo).setOnClickListener {
            if (imageUri != null) {
                getImageInfo(imageUri)
            }
        }

        vPhotoEditor = PhotoEditor.Builder(this, vPhotoEditorView)
            .setPinchTextScalable(pinchTextScalable)
            .build()

    }



    private fun toggleMenu() {
        if (menuLayout.visibility == View.GONE) {
            menuLayout.visibility = View.VISIBLE

        } else {
            menuLayout.visibility = View.GONE
        }
    }

    private fun shareImage(imageUri: Uri) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, imageUri)
            type = "image/jpeg" //
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        val chooser = Intent.createChooser(shareIntent, "Chia sẻ hình ảnh")
        startActivity(chooser)
    }

    fun getImageInfo(imageUri: Uri) {
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.WIDTH,
            MediaStore.Images.Media.HEIGHT
        )

        val cursor = contentResolver.query(imageUri, projection, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dateTakenIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
            val sizeIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val widthIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH)
            val heightIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT)

            if (it.moveToFirst()) {
                val name = it.getString(nameIndex)
                val dateTaken = it.getLong(dateTakenIndex)
                val size = it.getLong(sizeIndex)
                val width = it.getInt(widthIndex)
                val height = it.getInt(heightIndex)

                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                val date = dateFormat.format(Date(dateTaken))

                displayImageInfo(name, date, size, width, height)
            }
        }
    }


    fun displayImageInfo(name: String, date: String, size: Long, width: Int, height: Int) {
        val infoDialog = AlertDialog.Builder(this)
        infoDialog.setTitle("Thông tin")
        val message = """
        Tên: $name
        Ngày chụp: $date
        Kích thước: ${size / 1024} KB
        Kích thước: ${width} x ${height}
    """.trimIndent()
        infoDialog.setMessage(message)
        infoDialog.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        infoDialog.show()
    }



    @SuppressLint("NewApi")
    private fun trashImage(imageUri: Uri) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val intentSender = MediaStore.createTrashRequest(contentResolver, listOf(imageUri), true).intentSender
                startIntentSenderForResult(intentSender, DELETE_PERMISSION_REQUEST, null, 0, 0, 0, null)
            } else {
                val contentResolver: ContentResolver = contentResolver
                val deletedRows: Int = contentResolver.delete(imageUri, null, null)
                if (deletedRows > 0) {
                    println("Image deleted successfully")
                } else {
                    println("No image found to delete")
                }
            }
        } catch (@SuppressLint("NewApi") e: RecoverableSecurityException) {
            pendingIntent = e.userAction.actionIntent.intentSender
            startIntentSenderForResult(pendingIntent, DELETE_PERMISSION_REQUEST, null, 0, 0, 0, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == DELETE_PERMISSION_REQUEST && resultCode == RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Toast.makeText(this, "Đã đưa vào thùng rác", Toast.LENGTH_SHORT).show()
                val resultIntent = Intent()
                resultIntent.putExtra("del_image_uri", deleteUri.toString())
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show()
            }

        }
    }

}
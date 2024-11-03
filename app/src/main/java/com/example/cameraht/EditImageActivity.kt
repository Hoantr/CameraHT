package com.example.cameraht

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cameraht.fragment.DrawingOptionsFragment
import com.example.cameraht.fragment.EditTextDialogFragment
import com.example.cameraht.fragment.FilterImageFragment
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import ja.burhanrashid52.photoeditor.SaveFileResult
import ja.burhanrashid52.photoeditor.TextStyleBuilder
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder
import ja.burhanrashid52.photoeditor.shape.ShapeType
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.recyclerview.widget.RecyclerView
import com.example.cameraht.adapter.EmojiAdapter

class EditImageActivity : AppCompatActivity(),DrawingOptionsFragment.DrawingOptionsListener, EditTextDialogFragment.EditTextDialogListener {

    private lateinit var mPhotoEditorView: PhotoEditorView
    private lateinit var mPhotoEditor: PhotoEditor
    var pinchTextScalable = true
    var fragment = DrawingOptionsFragment()

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_image)
        mPhotoEditorView = findViewById(R.id.photoEditorView)
        val imagePath = intent.getStringExtra("image_uri")
        fragment.setDrawingOptionsListener(this)

        var imageUri: Uri? = imagePath?.let { Uri.parse(it) }

        mPhotoEditorView.source.setImageURI(imageUri)

        val mTextRobotoTf = ResourcesCompat.getFont(this, R.font.roboto_medium)
        val mEmojiTypeFace = Typeface.createFromAsset(assets, "emojione_android.ttf")


        mPhotoEditor = PhotoEditor.Builder(this, mPhotoEditorView)
            .setPinchTextScalable(pinchTextScalable)
            .setDefaultTextTypeface(mTextRobotoTf)
            .setDefaultEmojiTypeface(mEmojiTypeFace)
            .build()



        val rootView = findViewById<View>(android.R.id.content)
        rootView.setOnTouchListener { _, event ->
            handleScreenTap()
            true
        }

        findViewById<ImageButton>(R.id.btnUndo).setOnClickListener {
            mPhotoEditor.brushEraser()
        }

        findViewById<ImageButton>(R.id.btnUndo).setOnClickListener {
            mPhotoEditor.redo()
        }

        findViewById<ImageButton>(R.id.btnClose).setOnClickListener {
            finish()
        }
        findViewById<ImageButton>(R.id.btnSave).setOnClickListener {
            val photoFile = File(
                getOutputDirectory(),"E_IMG_"+
                        SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                            .format(System.currentTimeMillis()) + ".jpg"
            )
            lifecycleScope.launch {
                val result = mPhotoEditor.saveAsFile(photoFile.toString())
                if (result is SaveFileResult.Success) {
                    Toast.makeText(baseContext, "Ảnh được lưu: $photoFile", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(baseContext, "Lỗi lưu ảnh", Toast.LENGTH_SHORT).show()
                }
            }

            finish()
        }

        findViewById<ImageButton>(R.id.btnBrush).setOnClickListener {
            mPhotoEditor.setBrushDrawingMode(true)

            supportFragmentManager.beginTransaction()
                .add(R.id.menubuttonframe, fragment).addToBackStack(null)
                .commit()

        }

        findViewById<ImageButton>(R.id.btnAddText).setOnClickListener {
            val dialog = EditTextDialogFragment()
            
            dialog.show(supportFragmentManager, "EditTextDialog")

        }

        findViewById<ImageButton>(R.id.btnFilter).setOnClickListener {
            openFilterImageFragment()

        }


        findViewById<ImageButton>(R.id.btnEmoji).setOnClickListener {
            showEmojiPickerDialog()
        }


    }

    override fun onBrushSizeChanged(size: Float) {
        val shapeBuilder = ShapeBuilder().withShapeSize(size)
        mPhotoEditor.setShape(shapeBuilder)
    }

    override fun onBrushColorChanged(color: Int) {
        val shapeBuilder = ShapeBuilder().withShapeColor(color)
        mPhotoEditor.setShape(shapeBuilder)
    }

    override fun onShapeSelected(shapeType: ShapeType) {
        val shapeBuilder = ShapeBuilder().withShapeType(shapeType)
        mPhotoEditor.setShape(shapeBuilder)
    }

    override fun offBrushDrawing() {
        mPhotoEditor.setBrushDrawingMode(false)
        supportFragmentManager.popBackStack()
    }


    private fun getOutputDirectory(): File {
        val mediaDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
        val cameraDir = File(mediaDir, "EditImage")
        if (!cameraDir.exists()) {
            cameraDir.mkdirs()
        }
        return cameraDir
    }

    override fun onTextConfirmed(text: String, color: Int, textSize: Float) {
        val textStyleBuilder = TextStyleBuilder()
        textStyleBuilder.withTextColor(color)
        textStyleBuilder.withTextSize(textSize)

        mPhotoEditor.addText(text, textStyleBuilder)
    }

    private fun openFilterImageFragment() {
        val filterFragment = FilterImageFragment()
        filterFragment.setPhotoEditor(mPhotoEditor)


        supportFragmentManager.beginTransaction()
            .replace(R.id.menubuttonframe, filterFragment).addToBackStack(null)
            .commit()

    }
    private fun handleScreenTap() {
        supportFragmentManager.popBackStack()
    }

    private fun showEmojiPickerDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_emoji_picker, null)
        val emojiRecyclerView: RecyclerView = dialogView.findViewById(R.id.emojiRecyclerView)
        val cancelButton: Button = dialogView.findViewById(R.id.cancelButton)

        val emojiList = getEmojiList()

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()
        emojiRecyclerView.layoutManager = GridLayoutManager(this, 5)
        val adapter = EmojiAdapter(emojiList) { emojiUnicode ->
            mPhotoEditor.addEmoji(emojiUnicode)
            dialog.dismiss()
        }
        emojiRecyclerView.adapter = adapter

        cancelButton.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }
    private fun getEmojiList(): List<String> {
        return listOf(
            "\uD83D\uDE00", // 😀
            "\uD83D\uDE01", // 😁
            "\uD83D\uDE02", // 😂
            "\uD83D\uDE03", // 😃
            "\uD83D\uDE04", // 😄
            "\uD83D\uDE05", // 😅
            "\uD83D\uDE06", // 😆
            "\uD83D\uDE07", // 😇
            "\uD83D\uDE08", // 😈
            "\uD83D\uDE09", // 😉
            "\uD83D\uDE0A", // 😊
            "\uD83D\uDE0B", // 😋
            "\uD83D\uDE0C", // 😌
            "\uD83D\uDE0D", // 😍
            "\uD83D\uDE0E", // 😎
            "\uD83D\uDE12", // 😒
            "\uD83D\uDE13", // 😓
            "\uD83D\uDE14", // 😔
            "\uD83D\uDE15", // 😕
            "\uD83D\uDE31", // 😱
            "\u2764\uFE0F", // ❤️
            "\uD83D\uDC9A", // 💖
            "\uD83D\uDC9B", // 💛
            "\uD83D\uDC9C", // 💜
            "\uD83D\uDC99"  // 💙


        )
    }
    companion object {
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    }

}
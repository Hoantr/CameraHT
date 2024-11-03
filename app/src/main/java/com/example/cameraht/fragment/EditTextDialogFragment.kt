package com.example.cameraht.fragment

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.SeekBar
import com.example.cameraht.R

class EditTextDialogFragment : DialogFragment() {

    private var listener: EditTextDialogListener? = null

    interface EditTextDialogListener {
        fun onTextConfirmed(text: String, color: Int, textSize: Float)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? EditTextDialogListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_text, container, false)
        val editText = view.findViewById<EditText>(R.id.editText)

        val textSizeSeekBar = view.findViewById<SeekBar>(R.id.textSizeSeekBar)
        val btnConfirm = view.findViewById<ImageButton>(R.id.btnConfirmText)

        var chosenColor = Color.BLACK
        var chosenTextSize = 20f



        textSizeSeekBar.max = 100
        textSizeSeekBar.progress = 20
        textSizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                chosenTextSize = progress.toFloat()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        btnConfirm.setOnClickListener {
            val text = editText.text.toString()
            listener?.onTextConfirmed(text, chosenColor, chosenTextSize)
            dismiss()
        }

        return view
    }
}

package com.example.cameraht.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import com.example.cameraht.EditImageActivity
import com.example.cameraht.R
import ja.burhanrashid52.photoeditor.shape.ShapeType

class DrawingOptionsFragment : Fragment() {

    private var listener : DrawingOptionsListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_drawing_options, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chooseSize()
        chooseColor()
        chooseShape()
        view.findViewById<ImageButton>(R.id.btnCheckBrush).setOnClickListener {
            listener?.offBrushDrawing()
        }

    }

    fun setDrawingOptionsListener(listener: EditImageActivity) {
        this.listener = listener
    }

    interface DrawingOptionsListener {
        fun onBrushSizeChanged(size: Float)
        fun onBrushColorChanged(color: Int)
        fun onShapeSelected(shapeType: ShapeType)
        fun offBrushDrawing()
    }

    fun  chooseColor(){

        view?.findViewById<Button>(R.id.btnBlack)?.setOnClickListener {
            listener?.onBrushColorChanged(Color.parseColor("#000000"))
        }
        view?.findViewById<Button>(R.id.btnWhite)?.setOnClickListener {
            listener?.onBrushColorChanged(Color.parseColor("#FFFFFF"))
        }
        view?.findViewById<Button>(R.id.btnRed)?.setOnClickListener {
            listener?.onBrushColorChanged(Color.parseColor("#FF0000"))
        }
        view?.findViewById<Button>(R.id.btnGreen)?.setOnClickListener {
            listener?.onBrushColorChanged(Color.parseColor("#00FF00"))
        }
        view?.findViewById<Button>(R.id.btnBlue)?.setOnClickListener {
            listener?.onBrushColorChanged(Color.parseColor("#0000FF"))
        }
        view?.findViewById<Button>(R.id.btnYellow)?.setOnClickListener {
            listener?.onBrushColorChanged(Color.parseColor("#FFFF00"))
        }
        view?.findViewById<Button>(R.id.btnRose)?.setOnClickListener {
            listener?.onBrushColorChanged(Color.parseColor("#FF00FF"))
        }
        view?.findViewById<Button>(R.id.btnSilver)?.setOnClickListener {
            listener?.onBrushColorChanged(Color.parseColor("#C0C0C0"))
        }
        view?.findViewById<Button>(R.id.btnOrange)?.setOnClickListener {
            listener?.onBrushColorChanged(Color.parseColor("#FF6400"))
        }

    }

    fun chooseShape(){
        val radioGroupShape = view?.findViewById<RadioGroup>(R.id.radioGroupShape)
        radioGroupShape?.setOnCheckedChangeListener { _, checkedId ->
            val shapeType = when (checkedId) {
                R.id.radioOval -> ShapeType.Oval
                R.id.radioRectangle -> ShapeType.Rectangle
                R.id.radioFree -> ShapeType.Brush
                R.id.radioLine -> ShapeType.Line
                else -> ShapeType.Brush
            }
            listener?.onShapeSelected(shapeType)
        }
    }


    fun chooseSize(){
        val seekBarBrushSize = view?.findViewById<SeekBar>(R.id.seekBarBrushSize)
        seekBarBrushSize?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                listener?.onBrushSizeChanged(progress.toFloat())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }
}

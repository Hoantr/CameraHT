package com.example.cameraht.adapter

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import ja.burhanrashid52.photoeditor.PhotoFilter
import com.example.cameraht.R
import com.example.cameraht.data.FilterItem

class FilterAdapter(
    private val filters: List<FilterItem>,
    private val onFilterSelected: (PhotoFilter) -> Unit,
) : RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {

    inner class FilterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.filterImage)

        fun bind(filterItem: FilterItem) {
            val bitmap = BitmapFactory.decodeResource(itemView.resources, filterItem.imageResId)
            imageView.setImageBitmap(bitmap) // Hiển thị hình ảnh
            itemView.setOnClickListener {
                // Gọi callback với PhotoFilter
                onFilterSelected(filterItem.photoFilter)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_filter, parent, false)
        return FilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        holder.bind(filters[position])
    }

    override fun getItemCount(): Int = filters.size
}
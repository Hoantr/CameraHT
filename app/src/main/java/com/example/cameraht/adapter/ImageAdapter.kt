package com.example.cameraht.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cameraht.R


class ImageAdapter(private val photoList: MutableList<Uri>,private val listener: AdapterListListener) :
    RecyclerView.Adapter<ImageAdapter.PhotoViewHolder>() {

    interface AdapterListListener {
        fun sendUri(imageUri: Uri)
    }

    class PhotoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val imageUri = photoList[position]

        Glide.with(holder.imageView.context)
            .load(imageUri)
            .into(holder.imageView)

        holder.imageView.setOnClickListener {
            listener.sendUri(photoList[position])
        }
    }

    override fun getItemCount(): Int = photoList.size

    fun removeItem(uri: Uri) {
        var postt=0
        if (photoList.isEmpty()) {
            println("Danh sách ảnh trống.")
        } else {
            println("Danh sách ảnh:")
            for (uri1 in photoList) {

                println("$uri")
                if(uri==uri1){
                break
                }
                postt++
            }
        }
        photoList.removeAt(postt)
        notifyItemRemoved(postt)
        notifyItemRangeChanged(postt, photoList.size)
    }
}
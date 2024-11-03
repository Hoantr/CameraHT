package com.example.cameraht.adapter


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.cameraht.R
import androidx.recyclerview.widget.RecyclerView

class EmojiAdapter (private val emojis: List<String>, private val onEmojiClick: (String) -> Unit) :
    RecyclerView.Adapter<EmojiAdapter.EmojiViewHolder>() {

    inner class EmojiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val emojiTextView: TextView = itemView.findViewById(R.id.emojiText)

        fun bind(emoji: String) {
            emojiTextView.text = emoji
            itemView.setOnClickListener { onEmojiClick(emoji) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmojiViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_emoji, parent, false)
        return EmojiViewHolder(view)
    }

    override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
        holder.bind(emojis[position])
    }

    override fun getItemCount(): Int {
        return emojis.size
    }
}

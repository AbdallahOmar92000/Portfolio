package com.sarrawi.mysocialnetwork.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.sarrawi.mysocialnetwork.R

class ImagePreviewAdapter(
    private val uris: MutableList<Uri>,
    private val onRemove: (Int) -> Unit
) : RecyclerView.Adapter<ImagePreviewAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img = view.findViewById<ImageView>(R.id.imgPreview)
        val btnRemove = view.findViewById<ImageButton>(R.id.btnRemoveImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_image_preview, parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.img.setImageURI(uris[position]) // يمكنك استخدام Glide هنا
        holder.btnRemove.setOnClickListener { onRemove(holder.adapterPosition) }
    }

    override fun getItemCount() = uris.size
}
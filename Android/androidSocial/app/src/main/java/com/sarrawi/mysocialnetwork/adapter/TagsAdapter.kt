package com.sarrawi.mysocialnetwork.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sarrawi.mysocialnetwork.databinding.ItemTagsBinding
import com.sarrawi.mysocialnetwork.databinding.ItempostBinding
import com.sarrawi.mysocialnetwork.model.ExploreResponse
import com.sarrawi.mysocialnetwork.model.PostResponse
import com.sarrawi.mysocialnetwork.model.PostTags
import com.sarrawi.mysocialnetwork.model.Tag

class TagsAdapter2 : ListAdapter<PostTags, TagsAdapter2.PostsViewHolder>(DiffCallback()) {

    inner class PostsViewHolder(val binding: ItemTagsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsViewHolder {
        val binding = ItemTagsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostsViewHolder, position: Int) {
        val post = getItem(position)

        // عرض الوسوم في TextView واحد، مفصولة بفواصل
        holder.binding.tagsName.text = post.tags.joinToString(", ") { it.name }

        // عرض البريد الإلكتروني للمؤلف
        holder.binding.author.text = post.author_email

        // تحميل صورة المؤلف إن وجدت
        post.author_image?.let { imageUrl ->
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .into(holder.binding.authorImage)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<PostTags>() {
        override fun areItemsTheSame(oldItem: PostTags, newItem: PostTags) = oldItem == newItem
        override fun areContentsTheSame(oldItem: PostTags, newItem: PostTags) = oldItem == newItem
    }
}



class TagsAdapter : ListAdapter<Tag, TagsAdapter.TagsViewHolder2>(TagsAdapter.DiffCallback()) {
    inner class TagsViewHolder2(val binding:ItemTagsBinding): RecyclerView.ViewHolder(binding.root) {

    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagsViewHolder2 {
        val binding = ItemTagsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TagsViewHolder2(binding)
    }

    override fun onBindViewHolder(holder: TagsViewHolder2, position: Int) {

        val tag = getItem(position)
        holder.binding.tagsName.text = tag.name

    }

    class DiffCallback : DiffUtil.ItemCallback<Tag>() {
        override fun areItemsTheSame(oldItem: Tag, newItem: Tag) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Tag, newItem: Tag) = oldItem == newItem
    }

}

package com.sarrawi.mysocialnetwork.adapter

import android.graphics.Color
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sarrawi.mysocialnetwork.databinding.ItemPost2Binding
import com.sarrawi.mysocialnetwork.databinding.ItempostBinding
import com.sarrawi.mysocialnetwork.model.Post2
import com.sarrawi.mysocialnetwork.model.PostResponse
class ProfileAdapter : ListAdapter<Post2, ProfileAdapter.ViewHolder>(DiffCallback()) {

    private var onLikeClickListener: ((Post2) -> Unit)? = null
    private var onDislikeClickListener: ((Post2) -> Unit)? = null

    fun setOnLikeClickListener(listener: (Post2) -> Unit) { onLikeClickListener = listener }
    fun setOnDislikeClickListener(listener: (Post2) -> Unit) { onDislikeClickListener = listener }

    inner class ViewHolder(val binding: ItemPost2Binding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post2) {
            // تحديث ألوان الأزرار وعدد الإعجابات فوراً
            binding.txtlikeCountpost2.text = post.likes_count.toString()
            binding.txtdislikeCountpost2.text = post.dislikes_count.toString()
            binding.btnLikepost2.setColorFilter(if (post.is_liked_by_user) Color.BLUE else Color.GRAY)
            binding.btndisLikepost2.setColorFilter(if (post.is_disliked_by_user) Color.RED else Color.GRAY)

            binding.btnLikepost2.setOnClickListener {
                // تحديث مباشر عند الضغط
                val updatedPost = post.copy(
                    is_liked_by_user = !post.is_liked_by_user,
                    is_disliked_by_user = false,
                    likes_count = if (!post.is_liked_by_user) post.likes_count + 1 else post.likes_count - 1,
                    dislikes_count = if (post.is_disliked_by_user) post.dislikes_count - 1 else post.dislikes_count
                )
                submitUpdatedPost(updatedPost)
                onLikeClickListener?.invoke(updatedPost)
            }

            binding.btndisLikepost2.setOnClickListener {
                val updatedPost = post.copy(
                    is_disliked_by_user = !post.is_disliked_by_user,
                    is_liked_by_user = false,
                    dislikes_count = if (!post.is_disliked_by_user) post.dislikes_count + 1 else post.dislikes_count - 1,
                    likes_count = if (post.is_liked_by_user) post.likes_count - 1 else post.likes_count
                )
                submitUpdatedPost(updatedPost)
                onDislikeClickListener?.invoke(updatedPost)
            }
        }

        // تحديث الكائن في القائمة مباشرة
        private fun submitUpdatedPost(updatedPost: Post2) {
            val currentList = currentList.toMutableList()
            val index = currentList.indexOfFirst { it.id == updatedPost.id }
            if (index != -1) {
                currentList[index] = updatedPost
                submitList(currentList)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPost2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post = getItem(position)

        holder.bind(post)


        with(holder.binding) {
            val maxPreviewLines = 3
            val baseUrl = "https://networksocial.xyz"

            txtUsernamepost2.text = "${post.author.email}"
            txtPostBodypost2.text = post.body ?: ""
            txtPostDatepost2.text = post.created_on
            txtPostBodypost2.maxLines = maxPreviewLines
            txtPostBodypost2.ellipsize = TextUtils.TruncateAt.END
            tvShowMorepost2.text = "Show More"
            txtlikeCountpost2.text = post.likes_count.toString()
            txtdislikeCountpost2.text = post.dislikes_count.toString()

            post.author_image?.let { url ->
                val fullUrl = if (url.startsWith("http")) url else baseUrl + url
                Glide.with(imgUserProfilepost2.context)
                    .load(fullUrl)
                    .circleCrop()
                    .into(imgUserProfilepost2)
            }

            val imageUrls = post.image?.map { image ->
                val url = image.image
                if (url.startsWith("http")) url else baseUrl + url
            } ?: emptyList()

            val hasMultipleImages = imageUrls.size > 1

            val container = imagesContainerpost2
            container.removeAllViews()

            var isExpanded = false

            fun displayImages(showAll: Boolean) {
                container.removeAllViews()
                if (imageUrls.isNotEmpty()) {
                    container.visibility = View.VISIBLE
                    val imagesToShow = if (showAll) imageUrls else listOf(imageUrls.first())
                    for (url in imagesToShow) {
                        val imageView = ImageView(container.context).apply {
                            layoutParams = LinearLayout.LayoutParams(200, 200).apply {
                                setMargins(8, 0, 8, 0)
                            }
                            scaleType = ImageView.ScaleType.CENTER_CROP
                        }
                        Glide.with(container.context)
                            .load(url)
                            .into(imageView)
                        container.addView(imageView)
                    }
                } else {
                    container.visibility = View.GONE
                }
            }

            displayImages(false)

            // افحص إذا كان النص طويل
            txtPostBodypost2.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    txtPostBodypost2.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    val lineCount = txtPostBodypost2.lineCount
                    val isTextLong = lineCount > maxPreviewLines

                    if (isTextLong || hasMultipleImages) {
                        tvShowMorepost2.visibility = View.VISIBLE
                    } else {
                        tvShowMorepost2.visibility = View.GONE
                    }
                }
            })

            tvShowMorepost2.setOnClickListener {
                isExpanded = !isExpanded
                if (isExpanded) {
                    txtPostBodypost2.maxLines = Int.MAX_VALUE
                    tvShowMorepost2.text = "less"
                    displayImages(true)
                } else {
                    txtPostBodypost2.maxLines = maxPreviewLines
                    tvShowMorepost2.text = "Show More"
                    displayImages(false)
                }
            }
        }







}

    class DiffCallback : DiffUtil.ItemCallback<Post2>() {
        override fun areItemsTheSame(oldItem: Post2, newItem: Post2) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Post2, newItem: Post2) = oldItem == newItem
    }
}
//
//class ProfileAdapter2 : ListAdapter<Post2, ProfileAdapter2.ViewHolder>(DiffCallback()) {
//
//    private var onLikeClickListener: ((Post2) -> Unit)? = null
//    private var onDislikeClickListener: ((Post2) -> Unit)? = null
//
//    fun setOnLikeClickListener(listener: (Post2) -> Unit) {
//        onLikeClickListener = listener
//    }
//
//    fun setOnDislikeClickListener(listener: (Post2) -> Unit) {
//        onDislikeClickListener = listener
//    }
//
//
//
//    inner class ViewHolder(val binding: ItemPost2Binding) : RecyclerView.ViewHolder(binding.root){
//        fun bind(post: Post2){
//            binding.btnLike.setOnClickListener {
//                onLikeClickListener?.invoke(post)
//            }
//
//            binding.btndisLike.setOnClickListener {
//                onDislikeClickListener?.invoke(post)
//            }
//
//            if (post.is_liked_by_user) {
//                binding.btnLike.setColorFilter(Color.BLUE)
//            } else {
//                binding.btnLike.setColorFilter(Color.GRAY)
//            }
//
//            if (post.is_disliked_by_user) {
//                binding.btndisLike.setColorFilter(Color.RED)
//            } else {
//                binding.btndisLike.setColorFilter(Color.GRAY)
//            }
//
//            binding.btnLike.setColorFilter(if (post.is_liked_by_user) Color.BLUE else Color.GRAY)
//            binding.btndisLike.setColorFilter(if (post.is_disliked_by_user) Color.RED else Color.GRAY)
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val binding = ItemPost2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return ViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//
//        val post = getItem(position)
//
//        holder.bind(post)
//
//
//        with(holder.binding) {
//            val maxPreviewLines = 3
//            val baseUrl = "https://networksocial.xyz"
//
//            txtUsername.text = "${post.author.email}"
//            txtPostBody.text = post.body ?: ""
//            txtPostDate.text = post.created_on
//            txtPostBody.maxLines = maxPreviewLines
//            txtPostBody.ellipsize = TextUtils.TruncateAt.END
//            tvShowMore.text = "Show More"
//            txtlikeCount.text = post.likes_count.toString()
//            txtdislikeCount.text = post.dislikes_count.toString()
//
//            post.author_image?.let { url ->
//                val fullUrl = if (url.startsWith("http")) url else baseUrl + url
//                Glide.with(imgUserProfile.context)
//                    .load(fullUrl)
//                    .circleCrop()
//                    .into(imgUserProfile)
//            }
//
//            val imageUrls = post.image?.map { image ->
//                val url = image.image
//                if (url.startsWith("http")) url else baseUrl + url
//            } ?: emptyList()
//
//            val hasMultipleImages = imageUrls.size > 1
//
//            val container = imagesContainer
//            container.removeAllViews()
//
//            var isExpanded = false
//
//            fun displayImages(showAll: Boolean) {
//                container.removeAllViews()
//                if (imageUrls.isNotEmpty()) {
//                    container.visibility = View.VISIBLE
//                    val imagesToShow = if (showAll) imageUrls else listOf(imageUrls.first())
//                    for (url in imagesToShow) {
//                        val imageView = ImageView(container.context).apply {
//                            layoutParams = LinearLayout.LayoutParams(200, 200).apply {
//                                setMargins(8, 0, 8, 0)
//                            }
//                            scaleType = ImageView.ScaleType.CENTER_CROP
//                        }
//                        Glide.with(container.context)
//                            .load(url)
//                            .into(imageView)
//                        container.addView(imageView)
//                    }
//                } else {
//                    container.visibility = View.GONE
//                }
//            }
//
//            displayImages(false)
//
//            // افحص إذا كان النص طويل
//            txtPostBody.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//                override fun onGlobalLayout() {
//                    txtPostBody.viewTreeObserver.removeOnGlobalLayoutListener(this)
//
//                    val lineCount = txtPostBody.lineCount
//                    val isTextLong = lineCount > maxPreviewLines
//
//                    if (isTextLong || hasMultipleImages) {
//                        tvShowMore.visibility = View.VISIBLE
//                    } else {
//                        tvShowMore.visibility = View.GONE
//                    }
//                }
//            })
//
//            tvShowMore.setOnClickListener {
//                isExpanded = !isExpanded
//                if (isExpanded) {
//                    txtPostBody.maxLines = Int.MAX_VALUE
//                    tvShowMore.text = "less"
//                    displayImages(true)
//                } else {
//                    txtPostBody.maxLines = maxPreviewLines
//                    tvShowMore.text = "Show More"
//                    displayImages(false)
//                }
//            }
//        }
//
//
//
//
//
//    }
//
//    class DiffCallback : DiffUtil.ItemCallback<Post2>() {
//        override fun areItemsTheSame(oldItem: Post2, newItem: Post2) = oldItem.id == newItem.id
//        override fun areContentsTheSame(oldItem: Post2, newItem: Post2) = oldItem == newItem
//    }
//}

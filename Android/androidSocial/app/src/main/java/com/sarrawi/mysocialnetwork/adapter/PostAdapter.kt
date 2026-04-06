package com.sarrawi.mysocialnetwork.adapter

import android.graphics.Color
import android.text.TextUtils
import android.util.Log
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
import com.sarrawi.mysocialnetwork.databinding.ItempostBinding
import com.sarrawi.mysocialnetwork.model.PostResponse

class PostAdapter : ListAdapter<PostResponse, PostAdapter.PostViewHolder>(DiffCallback()) {

    private var onLikeClickListener: ((PostResponse) -> Unit)? = null
    private var onDislikeClickListener: ((PostResponse) -> Unit)? = null
    private var onEmailClickListener: ((PostResponse) -> Unit)? = null
    private var onSharePostlistener: ((PostResponse) -> Unit)? = null
    private var onPostClickListener: ((PostResponse) -> Unit)? = null



    fun setOnPostClickListener(listener:(PostResponse)->Unit ){
        onPostClickListener = listener
    }
    fun setOnLikeClickListener(listener: (PostResponse) -> Unit) {
        onLikeClickListener = listener
    }

    fun setOnDislikeClickListener(listener: (PostResponse) -> Unit) {
        onDislikeClickListener = listener
    }

    fun setOnshareListener(listener: (PostResponse) -> Unit) {
        onSharePostlistener = listener
    }

    fun setOnEmail(listener: (PostResponse) -> Unit){
        onEmailClickListener = listener
    }

    inner class PostViewHolder(val binding: ItempostBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: PostResponse) {

            binding.btnLike.setOnClickListener {
                onLikeClickListener?.invoke(post)
            }

            binding.btndisLike.setOnClickListener {
                onDislikeClickListener?.invoke(post)
            }

            if (post.isLikedByUser) {
                binding.btnLike.setColorFilter(Color.BLUE)
            } else {
                binding.btnLike.setColorFilter(Color.GRAY)
            }

            if (post.isDislikedByUser) {
                binding.btndisLike.setColorFilter(Color.RED)
            } else {
                binding.btndisLike.setColorFilter(Color.GRAY)
            }

            binding.btnLike.setColorFilter(if (post.isLikedByUser) Color.BLUE else Color.GRAY)
            binding.btndisLike.setColorFilter(if (post.isDislikedByUser) Color.RED else Color.GRAY)
            binding.btnShare.setOnClickListener {
                Log.d("PostAdapter", "Share clicked for postId=${post.id}")

                onSharePostlistener ?.invoke(post)
            }

            binding.txtUsername.setOnClickListener {
                onEmailClickListener?.invoke(post)
            }

            binding.txtPostBody.setOnClickListener {
                onPostClickListener?.invoke(post)
            }


        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItempostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {


        val post = getItem(position)

        holder.bind(post)


        with(holder.binding) {
            val maxPreviewLines = 3
            val baseUrl = "https://networksocial.xyz"

            txtUsername.text = "${post.author.email}"
            txtPostBody.text = post.body ?: ""
            txtPostDate.text = post.created_on
            txtPostBody.maxLines = maxPreviewLines
            txtPostBody.ellipsize = TextUtils.TruncateAt.END
            tvShowMore.text = "Show More"
            txtlikeCount.text = post.likes_count.toString()
            txtdislikeCount.text = post.dislikes_count.toString()

            post.author_image?.let { url ->
                val fullUrl = if (url.startsWith("http")) url else baseUrl + url
                Glide.with(imgUserProfile.context)
                    .load(fullUrl)
                    .circleCrop()
                    .into(imgUserProfile)
            }

            val imageUrls = post.image?.map { image ->
                val url = image.image
                if (url.startsWith("http")) url else baseUrl + url
            } ?: emptyList()

            val hasMultipleImages = imageUrls.size > 1

            val container = imagesContainer
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
            txtPostBody.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    txtPostBody.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    val lineCount = txtPostBody.lineCount
                    val isTextLong = lineCount > maxPreviewLines

                    if (isTextLong || hasMultipleImages) {
                        tvShowMore.visibility = View.VISIBLE
                    } else {
                        tvShowMore.visibility = View.GONE
                    }
                }
            })

            tvShowMore.setOnClickListener {
                isExpanded = !isExpanded
                if (isExpanded) {
                    txtPostBody.maxLines = Int.MAX_VALUE
                    tvShowMore.text = "less"
                    displayImages(true)
                } else {
                    txtPostBody.maxLines = maxPreviewLines
                    tvShowMore.text = "Show More"
                    displayImages(false)
                }
            }
        }


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
//
//
//            // تحميل صورة المستخدم
//            post.author_image?.let { url ->
//                val fullUrl = if (url.startsWith("http")) url else baseUrl + url
//                Glide.with(imgUserProfile.context)
//                    .load(fullUrl)
//                    .circleCrop()
//                    .into(imgUserProfile)
//            }
//
//            val container = holder.binding.imagesContainer
//            container.removeAllViews() // تنظيف الصور قبل الإضافة
//
//            var isExpanded = false // لتتبع حالة العرض الموسّع أو المختصر
//
//            fun displayImages(showAll: Boolean) {
//                container.removeAllViews()
//
//                if (!post.image.isNullOrEmpty()) {
//                    container.visibility = View.VISIBLE
//
//                    val imageUrls = post.image.map { image ->
//                        val url = image.image
//                        if (url.startsWith("http")) url else baseUrl + url
//                    }
//
//                    val imagesToShow = if (showAll) imageUrls else listOf(imageUrls.first())
//
//                    for (url in imagesToShow) {
//                        val imageView = ImageView(container.context).apply {
//                            layoutParams = LinearLayout.LayoutParams(200, 200).apply {
//                                setMargins(8, 0, 8, 0)
//                            }
//                            scaleType = ImageView.ScaleType.CENTER_CROP
//                        }
//
//                        Glide.with(container.context)
//                            .load(url)
//                            .into(imageView)
//
//                        container.addView(imageView)
//                    }
//                } else {
//                    container.visibility = View.GONE
//                }
//            }
//
//            // عرض صورة واحدة في البداية
//            displayImages(false)
//
//            tvShowMore.setOnClickListener {
//                isExpanded = !isExpanded
//
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
//
//        }


    }

    class DiffCallback : DiffUtil.ItemCallback<PostResponse>() {
        override fun areItemsTheSame(oldItem: PostResponse, newItem: PostResponse) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: PostResponse, newItem: PostResponse) = oldItem == newItem
    }
}


//            // تحميل أول صورة من الصور (لو موجودة)
//            if (!post.images.isNullOrEmpty()) {
//                imgPost.visibility = View.VISIBLE
//                Glide.with(imgPost.context)
//                    .load(post.images[0].image_url)
//                    .into(imgPost)
//            } else {
//                imgPost.visibility = View.GONE
//            }
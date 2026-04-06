package com.sarrawi.mysocialnetwork.adapter

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.sarrawi.mysocialnetwork.R
import com.sarrawi.mysocialnetwork.model.Post2
import com.sarrawi.mysocialnetwork.model.PostResponse

class TagsAdapter3 : RecyclerView.Adapter<TagsAdapter3.PostViewHolder>() {



    private val differCallback = object : DiffUtil.ItemCallback<Post2>() {
        override fun areItemsTheSame(oldItem: Post2, newItem: Post2): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post2, newItem: Post2): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, differCallback)

    fun submitList(list: List<Post2>) = differ.submitList(list)

    private var onLikeClickListener: ((Post2) -> Unit)? = null
    private var onDislikeClickListener: ((Post2) -> Unit)? = null
    private var onSharePostlistener: ((Post2) -> Unit)? = null
    private var onEmailClickListener: ((Post2) -> Unit)? = null

    fun setOnEmail(listener: (Post2) -> Unit){
        onEmailClickListener = listener
    }
    fun setOnLikeClickListener(listener: (Post2) -> Unit) {
        onLikeClickListener = listener
    }

    fun setOnDislikeClickListener(listener: (Post2) -> Unit) {
        onDislikeClickListener = listener
    }

    fun setOnshareListener(listener: (Post2) -> Unit) {
        onSharePostlistener = listener
    }

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivAuthor: ImageView = itemView.findViewById(R.id.ivAuthor)
        val ivtag: LinearLayout = itemView.findViewById(R.id.imagesContainer2)
        val tvEmail: TextView = itemView.findViewById(R.id.tvEmail)
        val tvBody: TextView = itemView.findViewById(R.id.tvBody)
        val tvTags: TextView = itemView.findViewById(R.id.tvTags)
        val btn_like     :ImageButton = itemView.findViewById(R.id.btnLikeexp)
        val btn_dis_like : ImageButton = itemView.findViewById(R.id.btndis_likeexp)
        val btnShareexp : ImageButton = itemView.findViewById(R.id.btnShareexp)
        val txt_count_like      :TextView=itemView.findViewById(R.id.txtlike_count)
        val txt_count_dislike   :TextView=itemView.findViewById(R.id.txtdislike_count)

        fun bind(post2: Post2){



            btn_like.setOnClickListener {
                onLikeClickListener?.invoke(post2)
            }

            btn_dis_like.setOnClickListener {
                onDislikeClickListener?.invoke(post2)
            }

            if (post2.is_liked_by_user) {
                btn_like.setColorFilter(Color.BLUE)
            } else {
                btn_like.setColorFilter(Color.GRAY)
            }

            if (post2.is_disliked_by_user) {
                btn_dis_like.setColorFilter(Color.RED)
            } else {
                btn_dis_like.setColorFilter(Color.GRAY)
            }

            btn_like.setColorFilter(if (post2.is_liked_by_user) Color.BLUE else Color.GRAY)
            btn_dis_like.setColorFilter(if (post2.is_disliked_by_user) Color.RED else Color.GRAY)
            btnShareexp.setOnClickListener {
                Log.d("PostAdapter", "Share clicked for postId=${post2.id}")

                onSharePostlistener ?.invoke(post2)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_explore, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = differ.currentList[position]

        holder.bind(post)
        holder.tvEmail.text = post.author.email
        holder.tvBody.text = post.body ?: ""

        // عرض الوسوم بشكل مرتب
        //joinToString هي دالة في Kotlin تحول List إلى String.
        //
        //" " هو الفاصل بين كل عنصر (هنا نفصل الوسوم بمسافة).
        //
        //{ it.name } يعني: لكل عنصر Tag2 في القائمة، خذ name فقط.
        //
        //النتيجة هنا تصبح نص واحد:
        val tagsString = post.tags?.joinToString(" ") { it.name } ?: ""
        holder.tvTags.text = tagsString

        Log.d("Adapter", "Post ${post.id} tags: ${post.tags}")


        // تحميل صورة الكاتب باستخدام Glide
        Glide.with(holder.itemView)
            .load(post.author_image)
            .placeholder(R.drawable.ic_user)
            .circleCrop()
            .into(holder.ivAuthor)


        val circularProgressDrawable = CircularProgressDrawable(holder.itemView.context).apply {
            strokeWidth = 5f
            centerRadius = 30f
            start() // يبدأ التحريك
        }

        holder.ivtag.removeAllViews()


        post.image?.forEach { image ->
            val imageView = ImageView(holder.itemView.context).apply {
                layoutParams = LinearLayout.LayoutParams(200, 200).apply { setMargins(8, 0, 8, 0) }
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            Glide.with(holder.itemView)
                .load(image.image)
                .placeholder(circularProgressDrawable)
                .into(imageView)
            holder.ivtag.addView(imageView)
        }




        holder.txt_count_like.text = post.likes_count.toString()
        holder.txt_count_dislike.text = post.dislikes_count.toString()

        holder.btn_like.setOnClickListener {
            onLikeClickListener?.invoke(post)
        }

        holder.btn_dis_like.setOnClickListener {
            onDislikeClickListener?.invoke(post)
        }

        if (post.is_liked_by_user) {
            holder.btn_like.setColorFilter(Color.BLUE)
        } else {
            holder.btn_like.setColorFilter(Color.GRAY)
        }

        if (post.is_disliked_by_user) {
            holder.btn_dis_like.setColorFilter(Color.RED)
        } else {
            holder.btn_dis_like.setColorFilter(Color.GRAY)
        }

        holder.btn_like.setColorFilter(if (post.is_liked_by_user) Color.BLUE else Color.GRAY)
        holder.btn_dis_like.setColorFilter(if (post.is_disliked_by_user) Color.RED else Color.GRAY)
        holder.tvEmail.setOnClickListener {
            onEmailClickListener?.invoke(post)
        }





//// تحميل أول صورة من قائمة الصور (إن وجدت)
//        val firstImageUrl = post.image?.firstOrNull()?.image
//        if (firstImageUrl != null) {
//            Glide.with(holder.itemView)
//                .load(firstImageUrl)
//                .placeholder(R.drawable.ic_user)
//                .centerCrop()
//                .into(holder.ivtag)
//        } else {
//            // إذا لم توجد صور، يمكن وضع صورة افتراضية أو تركها فارغة
//            holder.ivtag.visibility=View.GONE
//        }
    }

    override fun getItemCount(): Int = differ.currentList.size
}

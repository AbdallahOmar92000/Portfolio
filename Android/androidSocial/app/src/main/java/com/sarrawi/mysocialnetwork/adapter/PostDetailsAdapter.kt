//package com.sarrawi.mysocialnetwork.adapter
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.*
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.sarrawi.mysocialnetwork.R
//import com.sarrawi.mysocialnetwork.model.Post2
//import com.sarrawi.mysocialnetwork.model.PostResponse
//import com.sarrawi.mysocialnetwork.model.postdetails.Comment
//import com.sarrawi.mysocialnetwork.model.postdetails.PostDetailsItem
//import com.sarrawi.mysocialnetwork.model.postdetails.Reply
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//class PostDetailsAdapter (
//    private val items: MutableList<PostDetailsItem>
//) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
//
//    private var onLikeClickListener: ((Post2) -> Unit)? = null
//    private var onDislikeClickListener: ((Post2) -> Unit)? = null
//
//    private var onLikeClickListenerComm: ((Comment) -> Unit)? = null
//    private var onDislikeClickListenerComm: ((Comment) -> Unit)? = null
//
//    fun setOnLikeClickListener(listener: (Post2) -> Unit) {
//        onLikeClickListener = listener
//    }
//
//    fun setOnDislikeClickListener(listener: (Post2) -> Unit) {
//        onDislikeClickListener = listener
//    }
//
//    fun setOnLikeClickListenerComm(listener: (Comment) -> Unit) {
//        onLikeClickListenerComm = listener
//    }
//
//    fun setOnDislikeClickListenerComm(listener: (Comment) -> Unit) {
//        onDislikeClickListenerComm = listener
//    }
//
//    companion object {
//        private const val VIEW_TYPE_POST = 1
//        private const val VIEW_TYPE_COMMENT = 2
//        private const val VIEW_TYPE_REPLY = 3
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        return when (items[position]) {
//            is PostDetailsItem.PostItem -> VIEW_TYPE_POST
//            is PostDetailsItem.CommentItem -> VIEW_TYPE_COMMENT
//            is PostDetailsItem.ReplyItem -> VIEW_TYPE_REPLY
//            else -> {
//                VIEW_TYPE_COMMENT
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
//        return when (viewType) {
//
//            VIEW_TYPE_POST -> {
//                val view = LayoutInflater.from(parent.context)
//                    .inflate(R.layout.itempost, parent, false)
//                PostViewHolder(view)
//            }
//
//            VIEW_TYPE_COMMENT -> {
//                val view = LayoutInflater.from(parent.context)
//                    .inflate(R.layout.item_comment, parent, false)
//                CommentViewHolder(view)
//            }
//
//            else -> {
//                val view = LayoutInflater.from(parent.context)
//                    .inflate(R.layout.item_reply, parent, false)
//                ReplyViewHolder(view)
//            }
//        }
//    }
//
//    override fun getItemCount(): Int = items.size
//
//    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//
//        when (val item = items[position]) {
//
//            is PostDetailsItem.PostItem -> {
//                (holder as PostViewHolder).bind(item.post)
//            }
//
//            is PostDetailsItem.CommentItem -> {
//                (holder as CommentViewHolder).bind(item.comment)
//            }
//
//            is PostDetailsItem.ReplyItem -> {
//                (holder as ReplyViewHolder).bind(item.reply)
//            }
//            else -> {}
//        }
//    }
//
//    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
//        private val imgUserProfile: ImageView = itemView.findViewById(R.id.imgUserProfile)
//        private val imagesContainer: LinearLayout = itemView.findViewById(R.id.imagesContainer)
//        private val tvUserName: TextView = itemView.findViewById(R.id.txtUsername)
//        private val txtPostBody: TextView = itemView.findViewById(R.id.txtPostBody)
//        private val txtPostDate: TextView = itemView.findViewById(R.id.txtPostDate)
//        private val txtLikeCount: TextView = itemView.findViewById(R.id.txtlike_count)
//        private val txtDislikeCount: TextView = itemView.findViewById(R.id.txtdislike_count)
//        private val btnLike: Button = itemView.findViewById(R.id.btnLike)
//        private val btnDislike: Button = itemView.findViewById(R.id.btndis_like)
//
//        private val baseUrl = "https://networksocial.xyz"
//
//        fun bind(post: Post2) {
//            // ✔ تحميل صورة البروفايل
//            val profileUrl = post.author_image?.let {
//                if (it.startsWith("http")) it else baseUrl + it
//            }
//            Glide.with(imgUserProfile.context)
//                .load(profileUrl)
//                .circleCrop()
//                .into(imgUserProfile)
//
//            // ✔ اسم المستخدم
//            tvUserName.text = post.author.email
//
//            // ✔ نص المنشور
//            txtPostBody.text = post.body ?: ""
//
//            // ✔ التاريخ
//            txtPostDate.text = post.created_on ?: ""
//
//            // ✔ عدد اللايكات والديسلايكات
//            txtLikeCount.text = post.likes_count.toString()
//            txtDislikeCount.text = post.dislikes_count.toString()
//
//            // ✔ زر Like
//            btnLike.text = if (post.is_liked_by_user) "Liked" else "Like"
//            btnLike.setOnClickListener { onLikeClickListener?.invoke(post) }
//
//            // ✔ زر Dislike
//            btnDislike.text = if (post.is_disliked_by_user) "Disliked" else "Dislike"
//            btnDislike.setOnClickListener { onDislikeClickListener?.invoke(post) }
//
//            // ✔ التعامل مع صور المنشور
//            val imageUrls = post.image?.map { img ->
//                if (img.image.startsWith("http")) img.image else baseUrl + img.image
//            } ?: emptyList()
//
//            displayImages(imageUrls)
//        }
//
//        private fun displayImages(imageUrls: List<String>) {
//            imagesContainer.removeAllViews()
//            if (imageUrls.isEmpty()) {
//                imagesContainer.visibility = View.GONE
//                return
//            }
//
//            imagesContainer.visibility = View.VISIBLE
//            val maxInitialImages = 1 // نعرض صورة واحدة أولًا
//            val imagesToShow = imageUrls.take(maxInitialImages)
//
//            for (url in imagesToShow) {
//                val imageView = ImageView(imagesContainer.context).apply {
//                    layoutParams = LinearLayout.LayoutParams(200, 200).apply {
//                        setMargins(8, 0, 8, 0)
//                    }
//                    scaleType = ImageView.ScaleType.CENTER_CROP
//                }
//                Glide.with(imagesContainer.context).load(url).into(imageView)
//                imagesContainer.addView(imageView)
//            }
//
//            // إذا كان هناك أكثر من صورة واحدة، يمكن إضافة زر لتوسيع الصور
//            if (imageUrls.size > maxInitialImages) {
//                val btnShowMore = Button(imagesContainer.context).apply {
//                    text = "Show All (${imageUrls.size})"
//                    layoutParams = LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.WRAP_CONTENT,
//                        LinearLayout.LayoutParams.WRAP_CONTENT
//                    ).apply { setMargins(8, 8, 8, 0) }
//                }
//                btnShowMore.setOnClickListener {
//                    imagesContainer.removeAllViews()
//                    imageUrls.forEach { url ->
//                        val imgView = ImageView(imagesContainer.context).apply {
//                            layoutParams = LinearLayout.LayoutParams(200, 200).apply {
//                                setMargins(8, 0, 8, 0)
//                            }
//                            scaleType = ImageView.ScaleType.CENTER_CROP
//                        }
//                        Glide.with(imagesContainer.context).load(url).into(imgView)
//                        imagesContainer.addView(imgView)
//                    }
//                }
//                imagesContainer.addView(btnShowMore)
//            }
//        }
//    }
//
//
////    inner class PostViewHolder22222(itemView: View) : RecyclerView.ViewHolder(itemView) {
////
////        fun bind(post: Post2) {
////
////            val imgUserProfile = itemView.findViewById<ImageView>(R.id.imgUserProfile)
////            val imagesContainer = itemView.findViewById<ImageView>(R.id.imagesContainer)
////            val tvUserName = itemView.findViewById<TextView>(R.id.txtUsername)
////            val txtPostBody = itemView.findViewById<TextView>(R.id.txtPostBody)
////            val txtPostDate = itemView.findViewById<TextView>(R.id.txtPostDate)
////            val txtlike_count = itemView.findViewById<TextView>(R.id.txtlike_count)
////            val txtdislike_count = itemView.findViewById<TextView>(R.id.txtdislike_count)
////            val btnLike = itemView.findViewById<Button>(R.id.btnLike)
////            val btndis_like = itemView.findViewById<Button>(R.id.btndis_like)
////
////            val baseUrl = "https://networksocial.xyz"
////
////            // ✔ تحميل صورة البروفايل
////            val fullUrl = if (post.author_image?.startsWith("http") == true)
////                post.author_image
////            else
////                baseUrl + (post.author_image ?: "")
////
////            Glide.with(imgUserProfile.context)
////                .load(fullUrl)
////                .circleCrop()
////                .into(imgUserProfile)
////
////
////
////
////            //////////////////////
////
////
////
////            val imageUrls = post.image?.map { image ->
////                val url = image.image
////                if (url.startsWith("http")) url else baseUrl + url
////            } ?: emptyList()
////
////            val hasMultipleImages = imageUrls.size > 1
////
////            val container = imagesContainer
////            container.removeAllViews()
////
////            var isExpanded = false
////
////            fun displayImages(showAll: Boolean) {
////                container.removeAllViews()
////                if (imageUrls.isNotEmpty()) {
////                    container.visibility = View.VISIBLE
////                    val imagesToShow = if (showAll) imageUrls else listOf(imageUrls.first())
////                    for (url in imagesToShow) {
////                        val imageView = ImageView(container.context).apply {
////                            layoutParams = LinearLayout.LayoutParams(200, 200).apply {
////                                setMargins(8, 0, 8, 0)
////                            }
////                            scaleType = ImageView.ScaleType.CENTER_CROP
////                        }
////                        Glide.with(container.context)
////                            .load(url)
////                            .into(imageView)
////                        container.addView(imageView)
////                    }
////                } else {
////                    container.visibility = View.GONE
////                }
////            }
////
////            displayImages(false)
////            /////////////////////////
////
////            // ✔ اسم المستخدم
////            tvUserName.text = post.author.email
////
////            // ✔ نص المنشور
////            txtPostBody.text = post.body ?: ""
////
////            // ✔ التاريخ
////            txtPostDate.text = post.created_on ?: ""
////
////            // ❌ كنت تضع is_liked_by_user داخل like_count
////            // ✔ الصحيح:
////            txtlike_count.text = post.likes_count.toString()
////            txtdislike_count.text = post.dislikes_count.toString()
////
////            // ✔ زر Like
////            btnLike.text = if (post.is_liked_by_user) "Liked" else "Like"
////            btnLike.setOnClickListener { onLikeClickListener?.invoke(post) }
////
////            // ✔ زر Dislike
////            btndis_like.text = if (post.is_disliked_by_user) "Disliked" else "Dislike"
////            btndis_like.setOnClickListener { onDislikeClickListener?.invoke(post) }
////        }
////    }
//
//
//    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
//        var onSendComment: ((String) -> Unit)? = null
//
//        // 1️⃣ صورة بروفايل المستخدم
//        val imgUserProfile: ImageView = itemView.findViewById(R.id.imgUserProfileComm)
//
//        // 2️⃣ اسم المستخدم
//        val txtUsername: TextView = itemView.findViewById(R.id.txtUsernameComm)
//
//        // 3️⃣ تاريخ التعليق
//        val txtCommentDate: TextView = itemView.findViewById(R.id.txtCommentDateComm)
//
//        // 4️⃣ نص التعليق
//        val txtCommentBody: TextView = itemView.findViewById(R.id.txtCommentBodyComm)
//
//        // 5️⃣ صورة التعليق (اختياري)
//        val imgCommentImage: ImageView = itemView.findViewById(R.id.imgCommentImageComm)
//
//        // 6️⃣ عدد اللايكات
//        val txtLikesCount: TextView = itemView.findViewById(R.id.txtlike_countcom)
//
//        // 7️⃣ عدد الديسلايكات
//        val txtDislikesCount: TextView = itemView.findViewById(R.id.txtdislike_countcomm)
//        val btnLike: Button = itemView.findViewById(R.id.btnLike)
//        val btnDislike: Button = itemView.findViewById(R.id.btnDislike)
//
//        // 8️⃣ زر الرد
//        val btnReply: TextView = itemView.findViewById(R.id.btnReplyComm)
//
//        // 9️⃣ EditText لإضافة تعليق جديد
//        val edtNewComment: EditText = itemView.findViewById(R.id.edtNewCommentComm)
//
//        // 🔟 زر إرسال التعليق
//        val btnSendComment: Button = itemView.findViewById(R.id.btnSendCommentComm)
//
//        // 1️⃣1️⃣ الحاوية الأساسية للتعليق (يمكن استخدامها للتلوين أو click كامل)
//        val commentSection: LinearLayout = itemView.findViewById(R.id.commentSection)
//
//        private val baseUrl = "https://networksocial.xyz"
//
//        fun bind(comment: Comment){
//
//            val profileUrl = comment.author.user.picture?.let {
//                if (it.startsWith("http")) it else baseUrl + it
//            }
//            Glide.with(imgUserProfile.context)
//                .load(profileUrl)
//                .circleCrop()
//                .into(imgUserProfile)
//
//            txtUsername.text=comment.author.user.email
//            txtCommentDate.text=comment.created_on
//            txtCommentBody.text=comment.comment
//
//            val commentImageUrl = comment.image_url
//            if(commentImageUrl.isNullOrEmpty()){
//                imgCommentImage.visibility = View.GONE
//            }
//            else {
//                // يوجد صورة — أظهرها
//                imgCommentImage.visibility = View.VISIBLE
//
//                // تجهيز الرابط
//                val fullImageUrl = if (commentImageUrl.startsWith("http")) {
//                    commentImageUrl
//                } else {
//                    baseUrl + commentImageUrl
//                }
//// تحميل الصورة
//                Glide.with(imgCommentImage.context)
//                    .load(fullImageUrl)
//                    .centerCrop()
//                    .into(imgCommentImage)
//            }
//
//            txtLikesCount.text=comment.likes_count.toString()
//            txtDislikesCount.text=comment.dislikes_count.toString()
//
//            btnSendComment.setOnClickListener {
//                val text = edtNewComment.text.toString().trim()
//                if(text.isEmpty()){
//                    edtNewComment.error="............."
//                    return@setOnClickListener
//                }
//                // إرسال للـ Adapter مع postId
//                onSendComment?.invoke(text)
//
//                edtNewComment.setText("") // مسح
//            }
//
//            btnDislike.setOnClickListener {
//                onLikeClickListenerComm?.invoke(comment)
//
//            }
//            btnLike.setOnClickListener {
//                onDislikeClickListenerComm?.invoke(comment)
//            }
//
//
//
//
//
//            btnReply.setOnClickListener {
//                val currentPos = bindingAdapterPosition
//                if (currentPos == RecyclerView.NO_POSITION) return@setOnClickListener
//
//                // إزالة أي ReplyItem مفتوح سابقًا
//                items.removeAll { it is PostDetailsItem.ReplyItem }
//                notifyDataSetChanged()
//
//                // تحويل كل Comment في replies إلى Reply
//                val replyList = comment.replies.map { c ->
//                    Reply(
//                        id = c.id,
//                        comment = c.comment,
//                        image = c.image,
//                        image_url = c.image_url,
//                        author = c.author,
//                        created_on = c.created_on,
//                        likes_count = c.likes_count,
//                        dislikes_count = c.dislikes_count,
//                        replies = c.replies.map { cc ->  // تحويل الردود المتداخلة أيضا
//                            Reply(
//                                id = cc.id,
//                                comment = cc.comment,
//                                image = cc.image,
//                                image_url = cc.image_url,
//                                author = cc.author,
//                                created_on = cc.created_on,
//                                likes_count = cc.likes_count,
//                                dislikes_count = cc.dislikes_count,
//                                replies = emptyList() // أو يمكنك تحويلهم متداخلين إذا تريد
//                            )
//                        }
//                    )
//                }
//
//                // إضافة كل رد أسفل التعليق مباشرة
//                replyList.forEachIndexed { index, reply ->
//                    items.add(currentPos + 1 + index, PostDetailsItem.ReplyItem(reply))
//                }
//                notifyItemRangeInserted(currentPos + 1, replyList.size)
//            }
//
//
//            // أضف ReplyInputItem تحت التعليق مباشرة
//                // حذف أي ردود مفتوحة حالياً لتجنب التكرار
//
////            btnReply.setOnClickListener {
////                val currentPos = bindingAdapterPosition
////                if (currentPos == RecyclerView.NO_POSITION) return@setOnClickListener
////
////                // حذف أي ردود مفتوحة حالياً لتجنب التكرار
////                items.removeAll { it is PostDetailsItem.ReplyItem }
////                notifyDataSetChanged()
////
////                // إضافة جميع الردود الخاصة بالتعليق من السيرفر
////                comment.replies.forEachIndexed { index, reply ->
////                    items.add(currentPos + 1 + index, PostDetailsItem.ReplyItem(
////                        Reply(
////                            id = reply.id,
////                            comment = reply.comment,
////                            image = reply.image,
////                            image_url = reply.image_url,
////                            author = reply.author,
////                            created_on = reply.created_on,
////                            likes_count = reply.likes_count,
////                            dislikes_count = reply.dislikes_count,
////                            replies = reply.replies // لو هناك ردود متداخلة
////                        )
////                    ))
////                }
////                notifyItemRangeInserted(currentPos + 1, comment.replies.size)
////            }
////        }
//
//
//
//        }
//    }
//    inner class ReplyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
//        var onSendReComment: ((String) -> Unit)? = null
//        private val edtReply = itemView.findViewById<EditText>(R.id.edtReply)
//        private val btnSendReply = itemView.findViewById<Button>(R.id.btnSendReply)
//        private val imgUserProfileReComm = itemView.findViewById<ImageView>(R.id.imgUserProfileReComm)
//        private val txtUsernameReComm = itemView.findViewById<TextView>(R.id.txtUsernameReComm)
//        private val txtReCommentDateReComm = itemView.findViewById<TextView>(R.id.txtReCommentDateReComm)
//        private val txtReCommentBodyReComm = itemView.findViewById<TextView>(R.id.txtReCommentBodyReComm)
//
//        private val baseUrl = "https://networksocial.xyz"
//
//        fun bind(reply: Reply) {
//            // إذا كان هذا عنصر لإضافة رد جديد
//            val isNewReplyInput = reply.id == -1
//
//            if (isNewReplyInput) {
//                // إخفاء البيانات القديمة
//                txtUsernameReComm.visibility = View.GONE
//                txtReCommentDateReComm.visibility = View.GONE
//                txtReCommentBodyReComm.visibility = View.GONE
//                imgUserProfileReComm.visibility = View.GONE
//
//                // إظهار مربع الإدخال وزر الإرسال
//                edtReply.visibility = View.VISIBLE
//                btnSendReply.visibility = View.VISIBLE
//            } else {
//                // إخفاء مربع الإدخال
//                edtReply.visibility = View.GONE
//                btnSendReply.visibility = View.GONE
//
//                // عرض بيانات الرد
//                txtUsernameReComm.visibility = View.VISIBLE
//                txtReCommentDateReComm.visibility = View.VISIBLE
//                txtReCommentBodyReComm.visibility = View.VISIBLE
//                imgUserProfileReComm.visibility = View.VISIBLE
//
//                txtUsernameReComm.text = reply.author.user.email
//                txtReCommentDateReComm.text = reply.created_on
//                txtReCommentBodyReComm.text = reply.comment ?: ""
//
//                val profileUrl = reply.author.user.picture?.let { if (it.startsWith("http")) it else baseUrl + it }
//                Glide.with(imgUserProfileReComm.context)
//                    .load(profileUrl)
//                    .circleCrop()
//                    .into(imgUserProfileReComm)
//            }
//
//            btnSendReply.setOnClickListener {
//                val text = edtReply.text.toString().trim()
//                if (text.isEmpty()) {
//                    edtReply.error = "اكتب ردًا"
//                    return@setOnClickListener
//                }
//
//                val currentPos = bindingAdapterPosition
//                if (currentPos == RecyclerView.NO_POSITION) return@setOnClickListener
//
//                // إرسال البيانات للـ ViewModel
//                onSendReComment?.invoke(text)
//                edtReply.setText("") // مسح
//            }
//        }
//    }
//
//
//
//    inner class ReplyViewHolder2(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
//        var onSendReComment: ((String) -> Unit)? = null
//        val edtReply = itemView.findViewById<EditText>(R.id.edtReply)
//        val btnSendReply = itemView.findViewById<Button>(R.id.btnSendReply)
//        val imgUserProfileReComm =itemView.findViewById<ImageView>(R.id.imgUserProfileReComm)
//        val txtUsernameReComm=itemView.findViewById<TextView>(R.id.txtUsernameReComm)
//        val txtReCommentDateReComm=itemView.findViewById<TextView>(R.id.txtReCommentDateReComm)
//        val txtReCommentBodyReComm=itemView.findViewById<TextView>(R.id.txtReCommentBodyReComm)
//
//        private val baseUrl = "https://networksocial.xyz"
//
//        fun bind(reply: Reply) {
//
//            if (reply != null) {
//                // عرض البيانات
//                txtUsernameReComm.visibility = View.VISIBLE
//                txtReCommentDateReComm.visibility = View.VISIBLE
//                txtReCommentBodyReComm.visibility = View.VISIBLE
//                imgUserProfileReComm.visibility = View.VISIBLE
//
//                txtUsernameReComm.text = reply.author.user.email
//                txtReCommentDateReComm.text = reply.created_on
//                txtReCommentBodyReComm.text = reply.comment ?: ""
//
//                // تحميل صورة البروفايل
//                Glide.with(imgUserProfileReComm.context)
//                    .load(reply.author.picture ?: "")
//                    .circleCrop()
//                    .into(imgUserProfileReComm)
//
//            } else {
//                // إخفاء العناصر
//                txtUsernameReComm.visibility = View.GONE
//                txtReCommentDateReComm.visibility = View.GONE
//                txtReCommentBodyReComm.visibility = View.GONE
//                imgUserProfileReComm.visibility = View.GONE
//            }
//
//
//            val profileUrl = reply.author.user.picture?.let {
//                if (it.startsWith("http")) it else baseUrl + it
//            }
//            Glide.with(imgUserProfileReComm.context)
//                .load(profileUrl)
//                .circleCrop()
//                .into(imgUserProfileReComm)
//
//
//
//
//
//            btnSendReply.setOnClickListener {
//                val text = edtReply.text.toString().trim()
//                if (text.isEmpty()) {
//                    edtReply.error = "اكتب ردًا"
//                    return@setOnClickListener
//                }
//
//                val currentPos = bindingAdapterPosition
//                if (currentPos == RecyclerView.NO_POSITION) return@setOnClickListener
//
//                // إرسال البيانات للـ ViewModel
//                onSendReComment?.invoke(text)
//                edtReply.setText("") // مسح
//            }
//
//
//
//
//        }
//    }
//}
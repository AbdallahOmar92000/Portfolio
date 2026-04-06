package com.sarrawi.mysocialnetwork.adapter

import android.graphics.Color
import com.sarrawi.mysocialnetwork.model.postdetails.Comment
import com.sarrawi.mysocialnetwork.model.postdetails.PostDetailsItem
import com.sarrawi.mysocialnetwork.model.postdetails.Reply

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sarrawi.mysocialnetwork.R
import com.sarrawi.mysocialnetwork.model.Post2
import com.sarrawi.mysocialnetwork.model.PostResponse
import com.sarrawi.mysocialnetwork.model.postdetails.PostDetails

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class PostDetailsAdapter2(
    private val items: MutableList<PostDetailsItem>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onSendComment: ((commentId: Int?, text: String) -> Unit)? = null
    // 1️⃣ حدث إرسال تعليق جديد

    // 2️⃣ حدث إرسال رد
//    var onSendReply: ((commentIdRep: Int?, text: String) -> Unit)? = null
    var onSendReply: ((commentIdRep: Int?, text: String, imageFile: File?) -> Unit)? = null
    var onPickImage: ((callback: (File) -> Unit) -> Unit)? = null

    private var onLikeClickListener: ((Comment) -> Unit)? = null
    private var onDislikeClickListener: ((Comment) -> Unit)? = null

//    private var onPostClickListener: ((PostDetails) -> Unit)? = null
//
//    fun setonPostClickListener(listener: (PostDetails) -> Unit){
//        onPostClickListener = listener
//    }

    private var onLikeClickListener2: ((Int) -> Unit)? = null
    private var onDislikeClickListener2: ((Int) -> Unit)? = null

    fun setOnLikeClickListener2(listener: (Int) -> Unit) {
        onLikeClickListener2 = listener
    }

    fun setOnDislikeClickListener2(listener: (Int) -> Unit) {
        onDislikeClickListener2 = listener
    }


    fun setOnLikeClickListener(listener: (Comment) -> Unit) {
        onLikeClickListener = listener
    }

    fun setOnDislikeClickListener(listener: (Comment) -> Unit) {
        onDislikeClickListener = listener
    }
    companion object {
        private const val VIEW_TYPE_POST = 1
        private const val VIEW_TYPE_COMMENT = 2
        private const val VIEW_TYPE_REPLY = 3
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is PostDetailsItem.PostItem -> VIEW_TYPE_POST
            is PostDetailsItem.CommentItem -> VIEW_TYPE_COMMENT
            is PostDetailsItem.ReplyItem -> VIEW_TYPE_REPLY
            else -> VIEW_TYPE_COMMENT // أو أي قيمة افتراضية مناسبة
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {

            VIEW_TYPE_COMMENT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_comment, parent, false)
                CommentViewHolder(view)
            }
//            VIEW_TYPE_POST -> {
//                val view = LayoutInflater.from(parent.context)
//                    .inflate(R.layout.ss, parent, false)
//                PostViewHolder(view)
//            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_reply, parent, false)
                ReplyViewHolder(view)
            }
        }
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = items[position]) {
//            is PostDetailsItem.PostItem -> (holder as PostViewHolder).bind(item.post)
            is PostDetailsItem.CommentItem -> (holder as CommentViewHolder).bind(item.comment, position)
            is PostDetailsItem.ReplyItem -> (holder as ReplyViewHolder).bind(item.reply, item.depth, position)
            else -> {} // تجاهل أي نوع آخر

        }
    }

    /*** ViewHolders ***/

    inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtUsername = itemView.findViewById<TextView>(R.id.txtUsernameComm)
        private val txtCommentBody = itemView.findViewById<TextView>(R.id.txtCommentBodyComm)
        private val txtCommentDateComm = itemView.findViewById<TextView>(R.id.txtCommentDateComm)
        private val btnReply = itemView.findViewById<TextView>(R.id.btnReplyComm)
        private val edtReply = itemView.findViewById<EditText>(R.id.edtNewCommentComm)
        private val btnSend = itemView.findViewById<Button>(R.id.btnSendCommentComm)
        private val container = itemView.findViewById<LinearLayout>(R.id.commentSection)
        private val imagesContainer = itemView.findViewById<LinearLayout>(R.id.imagesContainercomm)
        val btndis_likecomm = itemView.findViewById<ImageButton>(R.id.btndis_likecomm)
        val btnLikecomm = itemView.findViewById<ImageButton>(R.id.btnLikecomm)
        val txtlike_countcom = itemView.findViewById<TextView>(R.id.txtlike_countcom)
        val txtdislike_countcomm = itemView.findViewById<TextView>(R.id.txtdislike_countcomm)

        private val baseUrl = "https://networksocial.xyz"

        fun bind(comment: Comment, position: Int) {
            txtUsername.text = comment.author.user.email
            txtCommentBody.text = comment.comment
            txtCommentDateComm.text = comment.created_on
            edtReply.visibility = View.GONE
            btnSend.visibility = View.GONE
            txtlike_countcom .text=comment.likes_count.toString()
            txtdislike_countcomm .text=comment.dislikes_count.toString()

            if (comment.is_liked_by_user) {
                btnLikecomm.setColorFilter(Color.BLUE)
            } else {
                btnLikecomm.setColorFilter(Color.GRAY)
            }

            if (comment.is_disliked_by_user) {
                btndis_likecomm.setColorFilter(Color.RED)
            } else {
                btndis_likecomm.setColorFilter(Color.GRAY)
            }

            btnLikecomm.setColorFilter(if (comment.is_liked_by_user) Color.BLUE else Color.GRAY)
            btndis_likecomm.setColorFilter(if (comment.is_disliked_by_user) Color.RED else Color.GRAY)

//            btnLikecomm.setOnClickListener {
//                onLikeClickListener?.invoke(comment)
//            }
//            btndis_likecomm.setOnClickListener {
//                onDislikeClickListener?.invoke(comment)
//            }

            // عند bind لكل item:
            btnLikecomm.setOnClickListener {
                onLikeClickListener2?.invoke(comment.id)  // ← إرسال ID فقط
            }

            btndis_likecomm.setOnClickListener {
                onDislikeClickListener2?.invoke(comment.id)  // ← إرسال ID فقط
            }

            val imageUrls = comment.image?.let { url ->
                val fullUrl = if (url.startsWith("http")) url else baseUrl + url
                listOf(fullUrl)
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



            btnReply.setOnClickListener {
                // إزالة أي reply input مفتوح
                items.removeAll { it is PostDetailsItem.ReplyItem && it.reply.id == -1 }
                notifyDataSetChanged()

                // إضافة ReplyInputItem وهمي مع parentCommentId
                val inputReply = Reply(
                    id = -1,
                    comment = null,
                    image = null,
                    image_url = null,
                    author = comment.author,
                    created_on = "",
                    likes_count = 0,
                    dislikes_count = 0,
                    replies = emptyList(),
                    parentCommentId = comment.id, // 👈 تمرير ID التعليق الأب,
                    is_liked_by_user = false,     // ← القيمة الافتراضية
                    is_disliked_by_user = false   // ← القيمة الافتراضية
                )

                items.add(position + 1, PostDetailsItem.ReplyItem(inputReply, depth = 1))
                notifyItemInserted(position + 1)
            }


            btnSend.setOnClickListener {
                val text = edtReply.text.toString().trim()
                if (text.isEmpty()) return@setOnClickListener
                onSendComment?.invoke(comment.id, text)
                edtReply.setText("")
                edtReply.visibility = View.GONE
                btnSend.visibility = View.GONE
            }
        }
    }



    inner class ReplyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtUsername = itemView.findViewById<TextView>(R.id.txtUsernameReComm)
        private val txtBody = itemView.findViewById<TextView>(R.id.txtReCommentBodyReComm)
        private val btnReplyOnReply = itemView.findViewById<TextView>(R.id.btnReplyOnReply)
        private val edtReply = itemView.findViewById<EditText>(R.id.edtReply)
        private val btnSend = itemView.findViewById<Button>(R.id.btnSendReply)
        private val btnPickImage = itemView.findViewById<ImageButton>(R.id.btnPickImage)
        private val inputContainer = itemView.findViewById<LinearLayout>(R.id.inputReplyContainer)
        private val replySection = itemView.findViewById<LinearLayout>(R.id.replySection)
        private val imgPreview = itemView.findViewById<ImageView>(R.id.imgPreviewReply)
        private val imgReplyPhoto = itemView.findViewById<ImageView>(R.id.imgReplyPhoto)
        private val replyBubbleContainer =itemView.findViewById<LinearLayout>(R.id.replyBubbleContainer)
        private val previewContainer = itemView.findViewById<FrameLayout>(R.id.previewContainer)
        private val btnRemovePreview = itemView.findViewById<ImageButton>(R.id.btnRemovePreview)
        private val linesContainer = itemView.findViewById<LinearLayout>(R.id.linesContainer)
        private val baseUrl = "https://networksocial.xyz"
        private var selectedReplyImage: File? = null

        fun bind(reply: Reply, depth: Int, position: Int) {
            val isInputMode = reply.id == -1
            if (isInputMode) {
                // حالة حقل الإدخال
                inputContainer.visibility = View.VISIBLE
                txtUsername.visibility = View.GONE
                txtBody.visibility = View.GONE
                btnReplyOnReply.visibility = View.GONE

                // إظهار زر اختيار الصورة والصورة المصغرة
                btnPickImage.visibility = View.VISIBLE
                imgPreview.visibility = View.GONE

                // ليكون الـ EditText جاهزاً للكتابة فوراً
                edtReply.requestFocus()
            } else {
                // حالة عرض البيانات
                inputContainer.visibility = View.GONE
                txtUsername.visibility = View.VISIBLE
                txtBody.visibility = View.VISIBLE
                btnReplyOnReply.visibility = View.VISIBLE

                txtUsername.text = reply.author.user.email
                txtBody.text = reply.comment ?: ""

                // عرض صورة الرد القادمة من السيرفر (إذا موجودة)
                val imageUrl = reply.image
                if (!imageUrl.isNullOrEmpty()) {
                    imgReplyPhoto.visibility = View.VISIBLE
                    val fullUrl = if (imageUrl.startsWith("http")) imageUrl else baseUrl + imageUrl
                    Glide.with(itemView.context)
                        .load(fullUrl)
                        .into(imgReplyPhoto)
                } else {
                    imgReplyPhoto.visibility = View.GONE
                }
            }
            val hasText = !reply.comment.isNullOrBlank()
            val hasImage = !reply.image.isNullOrEmpty()

            if (hasText || hasImage) {
                replyBubbleContainer.visibility = View.VISIBLE
            } else {
                replyBubbleContainer.visibility = View.GONE
            }
            if (hasText) {
                txtBody.visibility = View.VISIBLE
                txtBody.text = reply.comment
            } else {
                txtBody.visibility = View.GONE
            }
            if (hasImage) {
                imgReplyPhoto.visibility = View.VISIBLE
                val fullUrl =
                    if (reply.image!!.startsWith("http"))
                        reply.image
                    else
                        baseUrl + reply.image

                Glide.with(itemView.context)
                    .load(fullUrl)
                    .into(imgReplyPhoto)
            } else {
                imgReplyPhoto.visibility = View.GONE
            }


            val params = replySection.layoutParams as ViewGroup.MarginLayoutParams
            val indentSize = 48
            params.marginStart = depth * indentSize
            params.topMargin = 8
            params.bottomMargin = 8
            replySection.layoutParams = params

            linesContainer.removeAllViews()

            // إضافة خط لكل مستوى عمق (يبدأ من عمق 1)
            for (i in 1..depth) {
                val lineView = View(itemView.context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        2.dpToPx(itemView.context), // عرض الخط
                        ViewGroup.LayoutParams.MATCH_PARENT // طول الخط ليغطي الارتفاع
                    ).apply {
                        setMargins(4.dpToPx(itemView.context), 0, 4.dpToPx(itemView.context), 0)
                    }
                    setBackgroundColor(Color.parseColor("#CCCCCC")) // لون الخط (رمادي)
                }
                linesContainer.addView(lineView)
            }

            // اختيار صورة للرد
            btnPickImage.setOnClickListener {
                onPickImage?.invoke { file ->
                    selectedReplyImage = file
                    previewContainer.visibility = View.VISIBLE // إظهار الحاوية كاملة
                    imgPreview.visibility = View.VISIBLE
                    Glide.with(imgPreview.context)
                        .load(file)
                        .into(imgPreview)
                }
            }

            // الضغط على زر "رد" تحت الرد
            btnReplyOnReply.setOnClickListener {
                val existingInputPos = items.indexOfFirst { it is PostDetailsItem.ReplyItem && it.reply.id == -1 }
                if (existingInputPos != -1) {
                    items.removeAt(existingInputPos)
                    notifyItemRemoved(existingInputPos)
                }

                val inputItem = Reply(
                    id = -1,
                    author = reply.author,
                    comment = null,
                    parentCommentId = reply.id,
                    replies = emptyList(),
                    created_on = "",
                    dislikes_count = 0,
                    likes_count = 0,
                    image = null,
                    image_url = null,
                    is_liked_by_user = false,       // ← قيمة افتراضية
                    is_disliked_by_user = false     // ← قيمة افتراضية
                )

                val currentIdx = items.indexOf(items[position])
                val newPosition = currentIdx + 1
                items.add(newPosition, PostDetailsItem.ReplyItem(inputItem, depth + 1))
                notifyItemInserted(newPosition)
            }

            btnRemovePreview.setOnClickListener {
                selectedReplyImage = null
                previewContainer.visibility = View.GONE
                imgPreview.setImageDrawable(null)
            }

            // إرسال الرد
            btnSend.setOnClickListener {
                val text = edtReply.text.toString().trim()
                if (text.isEmpty() && selectedReplyImage == null) return@setOnClickListener

                onSendReply?.invoke(reply.parentCommentId ?: reply.id, text, selectedReplyImage)

                // تنظيف الحقول
                edtReply.setText("")
                selectedReplyImage = null
                imgPreview.setImageDrawable(null)
                imgPreview.visibility = View.GONE
                selectedReplyImage = null
                previewContainer.visibility = View.GONE
                items.removeAt(position)
                notifyItemRemoved(position)
            }
        }
    }


    private fun Int.dpToPx(context: android.content.Context): Int =
        (this * context.resources.displayMetrics.density).toInt()

    /*** دوال مساعدة لتحويل التعليقات + الردود لشجرة ***/
    fun addCommentsWithReplies(comments: List<Comment>) {
        items.clear()
        comments.forEach { comment ->
            items.add(PostDetailsItem.CommentItem(comment))
            addRepliesRecursively(comment.replies, 1)
        }
        notifyDataSetChanged()
    }



    fun updateComments(comments: List<Comment>) {
        items.clear()
        comments.forEach { comment ->
            // 1. إضافة التعليق الأساسي (عمق = 0)
            items.add(PostDetailsItem.CommentItem(comment))

            // 2. استدعاء الدالة التكرارية لإضافة الردود المتداخلة
            addRepliesRecursively(comment.replies, 1)
        }
        notifyDataSetChanged()
    }

    private fun addRepliesRecursively(replies: List<Comment>?, depth: Int) {
        replies?.forEach { reply ->
            // تحويل كائن Comment القادم من السيرفر إلى ReplyItem للعرض
            val convertedReply = reply.toReplyModel()

            // إضافة الرد للقائمة مع تحديد عمقه الحالي
            items.add(PostDetailsItem.ReplyItem(convertedReply, depth))

            // إذا كان لهذا الرد ردود (رد على رد)، نكرر العملية مع زيادة العمق
            if (!reply.replies.isNullOrEmpty()) {
                addRepliesRecursively(reply.replies, depth + 1)
            }
        }
    }

    fun updatePostAndComments(post: PostDetails) {
        items.clear()
        // إضافة البوست أولاً
        items.add(PostDetailsItem.PostItem(post))
        // إضافة التعليقات (قد تكون فارغة)
        post.comments.forEach { comment ->
            items.add(PostDetailsItem.CommentItem(comment))
            addRepliesRecursively(comment.replies, 1)
        }
        notifyDataSetChanged()
    }

    fun Comment.toReplyModel(): Reply {
        return Reply(
            id = this.id,
            author = this.author,
            comment = this.comment,
            created_on = this.created_on,
            likes_count = this.likes_count,
            dislikes_count = this.dislikes_count,
            image = this.image,
            image_url = this.image_url,
            parentCommentId = null, // اختياري
            is_disliked_by_user = this.is_disliked_by_user,
            is_liked_by_user = this.is_liked_by_user,
            // هنا نقوم بتحويل الـ List<Comment> إلى List<Reply> إذا كان موديل الـ Reply يتطلب ذلك
            replies = this.replies?.map { it.toReplyModel() } ?: emptyList()
        )
    }
    fun updateCommentLike(updatedComment: Comment) {
        val index = items.indexOfFirst {
            // ابحث في التعليقات أو الردود عن هذا الـ ID
            (it is PostDetailsItem.CommentItem && it.comment.id == updatedComment.id) ||
                    (it is PostDetailsItem.ReplyItem && it.reply.id == updatedComment.id)
        }

        if (index != -1) {
            val item = items[index]
            if (item is PostDetailsItem.CommentItem) {
                items[index] = PostDetailsItem.CommentItem(updatedComment)
            } else if (item is PostDetailsItem.ReplyItem) {
                // تحويل التعليق المحدث إلى موديل رد للحفاظ على توافق الأنواع
                items[index] = PostDetailsItem.ReplyItem(updatedComment.toReplyModel(), item.depth)
            }
            notifyItemChanged(index)
        }
    }


}

package com.sarrawi.mysocialnetwork.ui.social

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.sarrawi.mysocialnetwork.R
import com.sarrawi.mysocialnetwork.adapter.PostDetailsAdapter2
import com.sarrawi.mysocialnetwork.api.ApiPost
import com.sarrawi.mysocialnetwork.databinding.FragmentPostDetailsBinding
import com.sarrawi.mysocialnetwork.model.postdetails.PostDetailsItem
import com.sarrawi.mysocialnetwork.repository.PostRepository
import com.sarrawi.mysocialnetwork.viewmodel.PostViewModel
import com.sarrawi.mysocialnetwork.viewmodel.PostViewModelFactory
import java.io.File

class PostDetailsFragment : Fragment() {

    private var _binding: FragmentPostDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PostViewModel
    private val adapter = PostDetailsAdapter2(mutableListOf())
//jj
    private var token: String? = null
    private var PostIdd = -1
    private val selectedImages = mutableListOf<Uri>()

    // --- متغيرات الصور المفصلة ---
    private var selectedCommentImage: File? = null  // للتعليق الرئيسي الأسفل
    private var onReplyImagePickedCallback: ((File) -> Unit)? = null // للردود داخل الـ Adapter

    // --- Launchers مفصلة ---

    // 1. لانشر التعليق الرئيسي
    private val pickCommentImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            binding.btnRemoveImage.visibility = View.VISIBLE // ✅ إظهار زر الحذف
            binding.imagePreview.visibility = View.VISIBLE
            binding.imagePreview.setImageURI(it)
            selectedCommentImage = uriToFile(it)
        }
    }

    // 2. لانشر الردود المتداخلة
    private val pickReplyImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            val file = uriToFile(it)
            onReplyImagePickedCallback?.invoke(file)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPostDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        PostIdd = PostDetailsFragmentArgs.fromBundle(requireArguments()).postId

        setupViewModel()
        setupRecyclerView()
        setupObservers()
        setupListeners()
    }

    private fun setupViewModel() {
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        token = prefs.getString("auth_token", null)

        val repo = PostRepository(ApiPost.provideRetrofitInstance(), requireContext())
        viewModel = ViewModelProvider(this, PostViewModelFactory(repo, requireContext()))
            .get(PostViewModel::class.java)

        if (!token.isNullOrEmpty()) {
            viewModel.loadPostDetails(PostIdd, token!!)
        } else {
            Toast.makeText(requireContext(), "يرجى تسجيل الدخول", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerComments.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerComments.adapter = adapter

        // --- إعداد أحداث الـ Adapter للردود ---
        adapter.onPickImage = { callback ->
            onReplyImagePickedCallback = callback
            pickReplyImageLauncher.launch("image/*")
        }

//        adapter.onSendReply = { commentId, text, imageFile ->
//            commentId?.let {
//                viewModel.addReply(PostIdd, it, text, imageFile, token ?: "")
//            }
//        }

        adapter.onSendReply = { commentId, text, imageFile ->
            // سجل لمتابعة البيانات قبل إرسالها للـ ViewModel
            Log.d("ReplyDebug", "Attempting to send reply:")
            Log.d("ReplyDebug", "PostID: $PostIdd, ParentID: $commentId, Text: $text")
            Log.d("ReplyDebug", "Image Path: ${imageFile?.absolutePath ?: "No Image"}")

            commentId?.let {
                if (token.isNullOrEmpty()) {
                    Log.e("ReplyDebug", "Token is null or empty!")
                    Toast.makeText(requireContext(), "خطأ في التوكن", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.addReply(PostIdd, it, text, imageFile, token!!)
                }
            } ?: Log.e("ReplyDebug", "CommentID is NULL!")
        }

//        adapter.setOnDislikeClickListener {commentId ->
//            viewModel.dislikeComment(PostIdd, commentId.id, token!!)
//        }
//
//        adapter.setOnLikeClickListener {commentId ->
//            viewModel.likeComment(PostIdd, commentId.id, token!!)
//        }

        adapter.setOnLikeClickListener2 { commentId ->
            if (!token.isNullOrEmpty()) {
                viewModel.likeComment(PostIdd, commentId, token!!)
            }
        }

        adapter.setOnDislikeClickListener2 { commentId ->
            if (!token.isNullOrEmpty()) {
                viewModel.dislikeComment(PostIdd, commentId, token!!)
            }
        }

        viewModel.comm_like.observe(viewLifecycleOwner) { updatedComment ->
            if (updatedComment != null) {
                // نطلب من الأداπتر تحديث هذا التعليق فوراً في الشاشة
                adapter.updateCommentLike(updatedComment)
            }
        }

        viewModel.discomm_like.observe(viewLifecycleOwner) { updatedComment ->
            if (updatedComment != null) {
                // نطلب من الأداπتر تحديث هذا التعليق فوراً في الشاشة
                adapter.updateCommentLike(updatedComment)
            }
        }

    }

    private fun setupListeners() {
        // اختيار صورة للتعليق الرئيسي
        binding.btnSelectImage.setOnClickListener {
            pickCommentImageLauncher.launch("image/*")
        }

        // إرسال التعليق الرئيسي
        binding.btnSubmitComment.setOnClickListener {
            val commentText = binding.edtComment.text.toString().trim()
            if (commentText.isEmpty() && selectedCommentImage == null) {
                Toast.makeText(requireContext(), "أدخل تعليق أو اختر صورة", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.postComment(PostIdd, commentText, selectedCommentImage, token!!)

            // تصفير حقول التعليق الرئيسي فقط
            binding.edtComment.setText("")
            binding.imagePreview.visibility = View.GONE
            selectedCommentImage = null
        }

        binding.btnRemoveImage.setOnClickListener {
            selectedImages.clear()
            binding.imagePreview.visibility = View.GONE
            binding.btnRemoveImage.visibility = View.GONE
        }
    }

    private fun setupObservers() {
        val baseUrl = "https://networksocial.xyz"

        viewModel.postDetails.observe(viewLifecycleOwner) { postDetails ->
            adapter.updatePostAndComments(postDetails)

            // تحديث بيانات البوست
            binding.txtBody.text = postDetails.body
            binding.txtAuthor.text = postDetails.author.email
            binding.txtDate.text = postDetails.created_on
            binding.txtdislikeCount.text = postDetails.dislikes_count.toString()
            binding.txtlikeCount.text = postDetails.likes_count.toString()

            binding.btnLike.setColorFilter(if (postDetails.is_liked_by_user) Color.BLUE else Color.GRAY)
            binding.btndisLike.setColorFilter(if (postDetails.is_disliked_by_user) Color.RED else Color.GRAY)
            Log.d("ReplyDebug", "Fragment: Received updated post details, updating adapter")
            // استخدم الدالة التي أعددناها سابقاً لتحديث التعليقات والردود
            adapter.updatePostAndComments(postDetails)
            // صورة كاتب البوست
            postDetails.author_image?.let { url ->
                val fullUrl = if (url.startsWith("http")) url else baseUrl + url
                Glide.with(this).load(fullUrl).circleCrop().into(binding.imgProfile)
            }


            // صور البوست المتعددة
            setupPostImages(postDetails.image, baseUrl)
        }


        viewModel.commentResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Toast.makeText(requireContext(), "تمت الإضافة بنجاح", Toast.LENGTH_SHORT).show()
            }.onFailure { e ->
                Toast.makeText(requireContext(), "خطأ: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnLike.setOnClickListener {
            token?.let {
                viewModel.likePostDetails(it, PostIdd)
            }
        }

        binding.btndisLike.setOnClickListener {
            token?.let {
                viewModel.dislikePostDetails(it, PostIdd)
            }
        }
    }

    private fun setupPostImages(images: List<com.sarrawi.mysocialnetwork.model.Image>?, baseUrl: String) {
        val container = binding.imagesContainer
        container.removeAllViews()

        val imageUrls = images?.map {
            if (it.image.startsWith("http")) it.image else baseUrl + it.image
        } ?: emptyList()

        if (imageUrls.isNotEmpty()) {
            container.visibility = View.VISIBLE
            // عرض أول صورة كمثال (يمكنك تعديل المنطق لعرض الكل)
            val imageView = ImageView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(300, 300).apply { setMargins(8, 0, 8, 0) }
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
            Glide.with(this).load(imageUrls[0]).into(imageView)
            container.addView(imageView)
        } else {
            container.visibility = View.GONE
        }
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream = requireContext().contentResolver.openInputStream(uri)!!
        val tempFile = File.createTempFile("upload_", ".jpg", requireContext().cacheDir)
        inputStream.use { input ->
            tempFile.outputStream().use { output -> input.copyTo(output) }
        }
        return tempFile
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

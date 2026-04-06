package com.sarrawi.mysocialnetwork.ui.social

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sarrawi.mysocialnetwork.FileUtil
import com.sarrawi.mysocialnetwork.R
import com.sarrawi.mysocialnetwork.adapter.ImagePreviewAdapter
import com.sarrawi.mysocialnetwork.adapter.PostAdapter
import com.sarrawi.mysocialnetwork.api.ApiAuth
import com.sarrawi.mysocialnetwork.api.ApiPost
import com.sarrawi.mysocialnetwork.databinding.FragmentLogInBinding
import com.sarrawi.mysocialnetwork.databinding.FragmentPostBinding
import com.sarrawi.mysocialnetwork.repository.AuthRepository
import com.sarrawi.mysocialnetwork.repository.PostRepository
import com.sarrawi.mysocialnetwork.ui.MainActivity
import com.sarrawi.mysocialnetwork.viewmodel.AuthViewModel
import com.sarrawi.mysocialnetwork.viewmodel.AuthViewModelFactory
import com.sarrawi.mysocialnetwork.viewmodel.PostViewModel
import com.sarrawi.mysocialnetwork.viewmodel.PostViewModelFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class PostFragment : Fragment() {

    //_binding يخزن القيمة الفعلية للـ binding
    //
    //binding لا يخزن شيء، هو مجرد getter يسهل الوصول
    private var _binding: FragmentPostBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PostViewModel
    val adapter = PostAdapter()
    var token: String? = null
    var userId:Int?=null

    private val selectedImages = mutableListOf<Uri>()
//    private val imagePickerLauncher =
//        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
//            selectedImages.clear()
//            selectedImages.addAll(uris)
//            Toast.makeText(requireContext(), "${uris.size} صور تم اختيارها", Toast.LENGTH_SHORT).show()
//        }

    private lateinit var previewAdapter: ImagePreviewAdapter

    // في الـ Launcher
    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
            selectedImages.clear()
            selectedImages.addAll(uris)

            if (selectedImages.isNotEmpty()) {
                binding.rvSelectedImagesPreview.visibility = View.VISIBLE
                previewAdapter = ImagePreviewAdapter(selectedImages) { position ->
                    selectedImages.removeAt(position)
                    previewAdapter.notifyItemRemoved(position)
                    if (selectedImages.isEmpty()) binding.rvSelectedImagesPreview.visibility = View.GONE
                }
                binding.rvSelectedImagesPreview.adapter = previewAdapter
            }
        }



    // 1. onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // 2. onCreateView —> لإنشاء واجهة المستخدم
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostBinding.inflate(inflater, container, false)

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val repo = PostRepository(ApiPost.provideRetrofitInstance(), requireContext())
        viewModel = ViewModelProvider(this, PostViewModelFactory(repo, requireContext()))[PostViewModel::class.java]


        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        token = prefs.getString("auth_token", null)

        if (token == null) {
            Toast.makeText(requireContext(), "يرجى تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show()
            //findNavController().navigate(R.id.action_postFragment_to_logInFragment)
            return
        }
        viewModel.loadPosts(token!!)

        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            adapter.submitList(posts)  // يقوم بإعلام RecyclerView بالتحديث مع القيم الجديدة
        }
        // عندما يصلك الرد من السيرفر
        viewModel.loadProfile(token!!)
        viewModel.profile.observe(viewLifecycleOwner) { response ->
            response?.let {
                val currentUserId = it.user.id // من الـ JSON
                val prefs = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE)
                prefs.edit().putInt("user_id", currentUserId).apply()
                Log.d("PostFragment", "Current user ID: $currentUserId")
            }
        }


        // زر اختيار الصور
        binding.btnSelectImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        // زر إرسال المنشور
        binding.btnSubmitPost.setOnClickListener {
            val body = binding.etPostBody.text.toString().trim()
            if (body.isEmpty()) {
                Toast.makeText(requireContext(), "اكتب شيئًا للمنشور", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val imageParts = selectedImages.mapNotNull { uri -> prepareFilePart(uri) }
            Log.d("CheckUpload", "عدد الصور الجاهزة للرفع: ${imageParts.size}")
            viewModel.addPost(
                authToken = token!!,
                body = body,
                sharedBody = null,
                imageParts  = imageParts,
//                onSuccess = {
//                    Toast.makeText(requireContext(), "تم إضافة المنشور", Toast.LENGTH_SHORT).show()
//                    binding.etPostBody.text.clear()
//                    selectedImages.clear()
//                },

                // في الـ btnSubmitPost (عند النجاح)
                onSuccess = {
                    Toast.makeText(requireContext(), "تم إضافة المنشور", Toast.LENGTH_SHORT).show()
                    binding.etPostBody.text.clear()
                    selectedImages.clear()
                    binding.rvSelectedImagesPreview.visibility = View.GONE
                },
                onError = {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            )
        }

        binding.postsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        setAdapter()

        viewModel.posts.observe(viewLifecycleOwner){
            adapter.submitList(it)
        }
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
//            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }



    }


    // 4. onDestroyView —> لتحرير الموارد
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onResume() {
        super.onResume()
    }
    private fun uriToFile(uri: Uri): File? {
        val cursor = requireContext().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            val name = it.getString(nameIndex)
            val file = File(requireContext().cacheDir, name)
            requireContext().contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            return file
        }
        return null
    }

    private fun prepareFilePart(uri: Uri): MultipartBody.Part? {
        val context = requireContext()
        val file = FileUtil.from(context, uri)

        // استخرج اسم الملف مع الامتداد من الـ URI
        val fileNameWithExtension = getFileNameWithExtension(context, uri) ?: file!!.name

        Log.d("UploadDebug", "Uploading image with key: image and file name: $fileNameWithExtension")

        val requestFile = file!!.asRequestBody("image/*".toMediaTypeOrNull())

        return MultipartBody.Part.createFormData("images", fileNameWithExtension, requestFile)
    }


    private fun getFileNameWithExtension(context: Context, uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    result = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result
    }


    fun setAdapter(){


        adapter.setOnLikeClickListener { post ->
            // أرسل طلب Like أو حدث الحالة
            Toast.makeText(requireContext(), "Liked: ${post.body}", Toast.LENGTH_SHORT).show()
            // viewModel.likePost(post.id)

            viewModel.likePost(token!!,post)
        }

        adapter.setOnDislikeClickListener { post ->
            Toast.makeText(requireContext(), "Disliked: ${post.body}", Toast.LENGTH_SHORT).show()
            viewModel.dislikePost(token!!,post)

        }
//        adapter.setOnshareListener {
//            Toast.makeText(requireContext(), "Share: ${it.body}", Toast.LENGTH_SHORT).show()
//            viewModel.sharePost(token!!,it.id,it.body!!)
//        }
        adapter.setOnshareListener { post ->
            Toast.makeText(requireContext(), "Share: ${post.body}", Toast.LENGTH_SHORT).show()
            val bodyText = post.body ?: "" // إذا null خليها فارغة
            viewModel.sharePost(token!!, post.id, bodyText)
        }

        adapter.setOnEmail { post ->
            // داخل PostFragment
//            findNavController().navigate(
//                PostFragmentDirections.actionPostFragmentToUserProfileFragment(post.author.id)
//            )
            val navController = findNavController() // هذا مرتبط بالفراجمنت الحالي داخل Tab

            val action = UserProfileFragmentDirections
                .actionGlobalToUserProfileFragment(post.author.id)

            navController.navigate(action)




        }
        adapter.setOnPostClickListener { post ->


            val navController = requireActivity().findNavController(R.id.nav_host_fragment_content_main)

            val action = PostFragmentDirections.actionGlobalPostDetailsFragment(post.id)
            navController.navigate(action)


        }

        binding.postsRecyclerView.adapter = adapter

//        adapter.setOnLikeClickListener { post ->
//            // أرسل طلب Like أو حدث الحالة
//            Toast.makeText(requireContext(), "Liked: ${post.body}", Toast.LENGTH_SHORT).show()
//            // viewModel.likePost(post.id)
//            viewModel.likePost(post)
//            //viewModel.likePost(post.id)
//        }
//
//        adapter.setOnDislikeClickListener { post ->
//            Toast.makeText(requireContext(), "Disliked: ${post.body}", Toast.LENGTH_SHORT).show()
//            viewModel.dislikePost(post)
//        }


    }

    //    عندي mainactivity مرتبط معها logi reg navgraph 1
//    اذا نجح التسجيل يذهب ل maintabs fragment
//    android:id="@+id/mainTabsFragment"
//    تفتح لي mainTabsFragment
//        private lateinit var navController: NavController
//        val b1:Button  = view.findViewById<Button>(R.id.buttonContact)
//        b1.setOnClickListener {
//            val navController = requireActivity().findNavController(R.id.nav_host_fragment_content_main)
//            navController.navigate(R.id.blankFragment2)
//        }

//b1.setOnClickListener {
//    val userId = 1 // أو أي قيمة تريد تمريرها
//
//    val blankFragment2 = BlankFragment2()
//    val bundle = Bundle()
//    bundle.putInt("user_id", userId)
//    blankFragment2.arguments = bundle
//
//    requireActivity().supportFragmentManager.beginTransaction()
//        .replace(R.id.nav_host_fragment_content_main, blankFragment2)
//        .addToBackStack(null) // إذا أردت العودة للفراجمنت السابق بالضغط على الرجوع
//        .commit()
//}

}



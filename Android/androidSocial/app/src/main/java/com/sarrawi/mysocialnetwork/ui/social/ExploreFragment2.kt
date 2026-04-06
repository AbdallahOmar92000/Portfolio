package com.sarrawi.mysocialnetwork.ui.social

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sarrawi.mysocialnetwork.R
import com.sarrawi.mysocialnetwork.adapter.TagsAdapter3
import com.sarrawi.mysocialnetwork.api.ApiPost
import com.sarrawi.mysocialnetwork.databinding.FragmentExplore2Binding
import com.sarrawi.mysocialnetwork.repository.PostRepository
import com.sarrawi.mysocialnetwork.viewmodel.PostViewModel
import com.sarrawi.mysocialnetwork.viewmodel.PostViewModelFactory

class ExploreFragment2 : Fragment() {

    private var _binding: FragmentExplore2Binding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel2: PostViewModel
//    val viewModel: PostViewModel by activityViewModels()
    private val viewModel: PostViewModel by activityViewModels {
        PostViewModelFactory(PostRepository(ApiPost.provideRetrofitInstance(), requireContext()), requireContext())
    }
    private val adapter = TagsAdapter3() // Adapter واحد فقط
    private var token: String? = null
    private var currentUserId: Int = -1  // سيتم تعيينه من ViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExplore2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // إعداد Repository و ViewModel
        val repo = PostRepository(ApiPost.provideRetrofitInstance(), requireContext())
//        viewModel = ViewModelProvider(this, PostViewModelFactory(repo, requireContext()))[PostViewModel::class.java]

        // الحصول على التوكن
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        token = prefs.getString("auth_token", null)



        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "يرجى تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show()
            return
        }




        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "يرجى تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show()
            return
        }
        viewModel.loadProfile(token!!)
        viewModel.profile.observe(viewLifecycleOwner) { response ->
            response?.let {
                val currentUserId = it.user.id // من الـ JSON

                Log.d("Explragment", "Current user ID: $currentUserId")
            }
        }
        // جلب البيانات من الـ API
        viewModel.fetchExplore(token!!)

        // إعداد RecyclerView
        setupRecyclerView()

        // المراقبة وعرض البيانات
        viewModel.exploreData.observe(viewLifecycleOwner) { response ->
            adapter.submitList(response.posts)
        }



        binding.btnSearch.setOnClickListener {
            val query = binding.etSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                viewModel.searchExplore(token!!, query)
            } else {
                // إذا كان البحث فارغ، عرض كل البيانات الأصلية
                viewModel.fetchExplore(token!!)
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvTags.apply {
            adapter = this@ExploreFragment2.adapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        adapter.setOnDislikeClickListener {
            Toast.makeText(requireContext(), "Disliked: ${it.body}", Toast.LENGTH_SHORT).show()
            viewModel.dislikeexp2(token!!, it)
        }
        adapter.setOnLikeClickListener {
            Toast.makeText(requireContext(), "Liked: ${it.body}", Toast.LENGTH_SHORT).show()
            // viewModel.likePost(post.id)

            viewModel.likeexp2(token!!, it)
        }
        adapter.setOnshareListener {

        }
//        adapter.setOnEmail {
//            val navController = findNavController()
//            val action = UserProfileFragmentDirections.actionGlobalToUserProfileFragment(it.author.id)
//            navController.navigate(action)
//        }

//        adapter.setOnEmail {
//
//                // مستخدم آخر → الانتقال خارج Tabs (UserProfileFragment)
//            val navController = findNavController()
//            val action = UserProfileFragmentDirections.actionGlobalToUserProfileFragment(it.author.id)
//            navController.navigate(action)
//
//        }

//        adapter.setOnEmail {
//            val currentUserEmail = viewModel.currentUserId.value?:0 // البريد الحالي من session/token
//            val clickedUserEmail = it.author.id // البريد الذي تم الضغط عليه
//            val navController = findNavController()
//            if (clickedUserEmail == currentUserEmail) {
//                // اليوزر نفسه → نذهب إلى ProfileFragment
//                navController.navigate(R.id.profileFragment2)
//                val mainTabsFragment = parentFragment as? MainTabsFragment
//                mainTabsFragment?.goToProfileTab()
//
////                val mainTabsFragment = parentFragmentManager
////                    .fragments
////                    .find { it is MainTabsFragment } as? MainTabsFragment
////                mainTabsFragment?.goToProfileTab()
//            } else {
//                // يوزر آخر → نذهب إلى UserProfileFragment مع تمرير userId
//                val bundle = Bundle().apply {
//                    putInt("userId", it.author.id) // أو حسب نوعه Int/Long
//                }
//                navController.navigate(R.id.userProfileFragment, bundle)
//            }
//        }

        adapter.setOnEmail {
            val currentUserId = viewModel.currentUserId.value ?: 0
            val clickedUserId = it.author.id

            if (clickedUserId == currentUserId) {
                // المستخدم الحالي → لا تفتح UserProfileFragment
                listener?.onCurrentUserClicked()
            } else {
                // مستخدم آخر → افتح صفحة البروفايل الخاصة به
                listener?.onOtherUserClicked(clickedUserId)
            }
        }

    }

        // دالة مساعدة لجلب currentUserId
        private fun getCurrentUserId(): Int {
            val prefs = requireContext().getSharedPreferences("prefs", Context.MODE_PRIVATE)
            return prefs.getInt("user_id", -1)
        }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface OnUserClickListener {
        fun onCurrentUserClicked()
        fun onOtherUserClicked(userId: Int)
    }

    private var listener: OnUserClickListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // parentFragment لأن ExploreFragment2 داخل MainTabsFragment
        if (parentFragment is OnUserClickListener) {
            listener = parentFragment as OnUserClickListener
        } else {
            throw RuntimeException("$parentFragment must implement OnUserClickListener")
        }
    }
}
//adapter.setOnEmail { post ->
//
//    // NavController على مستوى Activity (لتجنب مشاكل Nested NavGraph)
//    val navController = requireActivity().findNavController(R.id.nav_host_fragment_content_main)
//
//    if (post.author.id == userId) {
//        // المستخدم الحالي → ProfileFragment
//        navController.navigate(R.id.action_global_profileFragment)
//    } else {
//        // مستخدم آخر → UserProfileFragment مع تمرير ID
//        val bundle = Bundle().apply { putInt("userId", post.author.id) }
//        navController.navigate(R.id.action_global_userProfileFragment, bundle)
//    }
//}
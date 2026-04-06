package com.sarrawi.mysocialnetwork.ui.social

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.sarrawi.mysocialnetwork.R
import com.sarrawi.mysocialnetwork.adapter.NotificationAdapter
import com.sarrawi.mysocialnetwork.adapter.ProfileAdapter
import com.sarrawi.mysocialnetwork.api.ApiPost
import com.sarrawi.mysocialnetwork.databinding.FragmentNotificationsBinding
import com.sarrawi.mysocialnetwork.databinding.FragmentProfileBinding
import com.sarrawi.mysocialnetwork.repository.PostRepository
import com.sarrawi.mysocialnetwork.viewmodel.PostViewModel
import com.sarrawi.mysocialnetwork.viewmodel.PostViewModelFactory


class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PostViewModel
    private val adapter = ProfileAdapter()
    private var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


     override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1️⃣ إعداد Repository و ViewModel
        val repo = PostRepository(ApiPost.provideRetrofitInstance(), requireContext())
        viewModel = ViewModelProvider(
            this,
            PostViewModelFactory(repo, requireContext())
        )[PostViewModel::class.java]

        // 2️⃣ جلب التوكن من SharedPreferences
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        token = prefs.getString("auth_token", null)
        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "يرجى تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show()
            return
        }

        // 3️⃣ إعداد RecyclerView و Adapter
        binding.rvPosts.layoutManager = LinearLayoutManager(requireContext())
        setAdapter()
        binding.rvPosts.adapter = adapter

        // 4️⃣ تحميل بيانات الملف الشخصي والمنشورات
        viewModel.loadProfile(token!!)



        // 6️⃣ مراقبة بيانات الملف الشخصي
        viewModel.profile.observe(viewLifecycleOwner) { profile ->
            // تحديث النصوص
            binding.tvName.text = profile.profile.name
            binding.tvUsername.text = profile.user.email
            binding.tvBio.text = profile.profile.bio
            binding.tvBirthday.text = profile.profile.user.birth_date
            binding.tvGender.text = profile.profile.user.birth_date
            binding.tvLocation.text = profile.profile.user.birth_date
            binding.tvFollowers.text = "${profile.profile.followers_count} followers"
            binding.tvFollowing.text = "${profile.profile.following_count} following"

            // تحديث صورة الملف الشخصي
            profile.profile.picture?.let { url ->
                Glide.with(binding.imgProfilePicture.context)
                    .load(url)
                    .circleCrop()
                    .into(binding.imgProfilePicture)
            }

            // إذا أردت عرض منشورات الملف الشخصي في نفس القائمة
            adapter.submitList(profile.posts)
        }

        // 7️⃣ مراقبة الأخطاء
        viewModel.error.observe(viewLifecycleOwner) { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

         binding.tvFollowers.setOnClickListener {
             val navController = requireActivity().findNavController(R.id.nav_host_fragment_content_main)
            navController.navigate(R.id.followersFragment)
         }
         binding.tvFollowing.setOnClickListener {
             val navController = requireActivity().findNavController(R.id.nav_host_fragment_content_main)
             navController.navigate(R.id.followingFragment)
         }

    }


    fun setAdapter() {


        adapter.setOnLikeClickListener { post ->
            // أرسل طلب Like أو حدث الحالة
            Toast.makeText(requireContext(), "Liked: ${post.body}", Toast.LENGTH_SHORT).show()
            // viewModel.likePost(post.id)

            viewModel.likePost2(token!!, post)

        }

        adapter.setOnDislikeClickListener { post ->
            Toast.makeText(requireContext(), "Disliked: ${post.body}", Toast.LENGTH_SHORT).show()
            viewModel.dislikePost2(token!!, post)
//            val navController = requireActivity().findNavController(R.id.nav_host_fragment_content_main)
//            navController.navigate(R.id.blankFragment2)

        }
        binding.rvPosts.adapter = adapter
    }
}

//binding.btnFollowers.setOnClickListener {
//    val action = ProfileFragmentDirections.actionProfileToFollowFragment(userId)
//    findNavController().navigate(action)
//}
//
//binding.btnFollowing.setOnClickListener {
//    val action = ProfileFragmentDirections.actionProfileToFollowFragment(userId)
//    findNavController().navigate(action)
//}

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

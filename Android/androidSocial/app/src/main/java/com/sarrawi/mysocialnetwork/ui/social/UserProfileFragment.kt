package com.sarrawi.mysocialnetwork.ui.social

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.sarrawi.mysocialnetwork.R
import com.sarrawi.mysocialnetwork.adapter.ProfileAdapter
import com.sarrawi.mysocialnetwork.api.ApiPost
import com.sarrawi.mysocialnetwork.databinding.FragmentProfileBinding
import com.sarrawi.mysocialnetwork.databinding.FragmentUserProfileBinding
import com.sarrawi.mysocialnetwork.model.ProfileModel.ProfileState
import com.sarrawi.mysocialnetwork.repository.PostRepository
import com.sarrawi.mysocialnetwork.viewmodel.PostViewModel
import com.sarrawi.mysocialnetwork.viewmodel.PostViewModelFactory
import androidx.navigation.fragment.findNavController


class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PostViewModel
    private val adapter = ProfileAdapter()
    private var token: String? = null
    private var userIdd = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var userId = UserProfileFragmentArgs.fromBundle(requireArguments()).userId
        arguments?.let {
//            userIdd = it.getString("userId")
            userIdd = arguments?.getInt("userId")!!

        }
        val repo = PostRepository(ApiPost.provideRetrofitInstance(), requireContext())
        viewModel = ViewModelProvider(
            this,
            PostViewModelFactory(repo, requireContext())
        )[PostViewModel::class.java]

        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        token = prefs.getString("auth_token", null)
        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "يرجى تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show()
            return
        }

        binding.rvPosts.layoutManager = LinearLayoutManager(requireContext())
        setAdapter()
        binding.rvPosts.adapter = adapter

        // ✅ Observer على followStatus مرة واحدة
        viewModel.followStatus.observe(viewLifecycleOwner) { status ->
            binding.btnFollowUnfollow.text = if (status.is_following) "Unfollow" else "Follow"
        }

        // ✅ تحميل حالة المتابعة مرة واحدة
        //viewModel.loadFollowStatus(userId, token!!)

        // ✅ تحميل البروفايل
        viewModel.loadUserProfile(userId, token!!)

        binding.btnFollowUnfollow.setOnClickListener {
            viewModel.toggleFollow(userId, token!!)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.profileState_w_id.collect { state ->
                when(state){
                    is ProfileState.Idle -> {}
                    is ProfileState.Loading -> binding.progressBar.visibility = View.VISIBLE
                    is ProfileState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val profile = state.profile
                        binding.tvName.text = profile.profile.name
                        binding.tvUsername.text = profile.user.email
                        binding.tvBio.text = profile.profile.bio
                        binding.tvBirthday.text = profile.profile.user.birth_date
                        binding.tvGender.text = profile.profile.user.birth_date
                        binding.tvLocation.text = profile.profile.user.birth_date
                        binding.tvFollowers.text = "${profile.profile.followers_count} followers"
                        binding.tvFollowing.text = "${profile.profile.following_count} following"


                        profile.profile.picture?.let { url ->
                            Glide.with(binding.imgProfilePicture.context)
                                .load(url)
                                .circleCrop()
                                .into(binding.imgProfilePicture)
                        }

                        adapter.submitList(profile.posts)
                    }
                    is ProfileState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

         userId = UserProfileFragmentArgs.fromBundle(requireArguments()).userId

        binding.tvFollowers.setOnClickListener {
            val action = UserProfileFragmentDirections
                .actionUserProfileFragmentToFollowersFragment(userId)
            findNavController().navigate(action)
        }

        binding.tvFollowing.setOnClickListener {
            val action = UserProfileFragmentDirections
                .actionUserProfileFragmentToFollowingFragment(userId)
            findNavController().navigate(action)
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


        }
        binding.rvPosts.adapter = adapter
    }

}

//binding.btnFollowUnfollow.setOnClickListener {
//    token?.let {
//        viewModel.toggleFollow(userId, it)
//    } ?: Toast.makeText(requireContext(), "Token not found", Toast.LENGTH_SHORT).show()
//}
//viewModel.followStatus.observe(viewLifecycleOwner) { status ->
//    binding.btnFollowUnfollow.isEnabled = true
//    binding.btnFollowUnfollow.text = if (status.is_following) "Unfollow" else "Follow"
//}
//binding.btnFollowUnfollow.isEnabled = false
//Log.d("DEBUG_TOKEN", "Token = $token")

//إذا أردت الاعتماد على API الـ GET toggle-follow، تأكد مع backend أن:
//
//endpoint يقبل التوكن الحالي.
//
//لا يحتاج CSRF token إضافي.
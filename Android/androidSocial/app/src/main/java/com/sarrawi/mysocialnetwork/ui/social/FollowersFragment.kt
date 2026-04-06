package com.sarrawi.mysocialnetwork.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.sarrawi.mysocialnetwork.adapter.FollowAdapter
import com.sarrawi.mysocialnetwork.api.ApiPost
import com.sarrawi.mysocialnetwork.databinding.FragmentFollowersBinding
import com.sarrawi.mysocialnetwork.repository.PostRepository
import com.sarrawi.mysocialnetwork.viewmodel.PostViewModel
import com.sarrawi.mysocialnetwork.viewmodel.PostViewModelFactory

class FollowersFragment : Fragment() {

    private var _binding: FragmentFollowersBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: PostViewModel
    private lateinit var adapter: FollowAdapter
    private var userId: Int? = null // null = current user
    var token: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFollowersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        val args = FollowersFragmentArgs.fromBundle(requireArguments())
        userId = if (args.userId == -1) {
            null // -1 يعني المستخدم الحالي
        } else {
            args.userId
        }



        val repo = PostRepository(ApiPost.provideRetrofitInstance(), requireContext())
        viewModel = ViewModelProvider(this, PostViewModelFactory(repo, requireContext()))[PostViewModel::class.java]


        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        token = prefs.getString("auth_token", null)

        if (token == null) {
            Toast.makeText(requireContext(), "يرجى تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show()
            //findNavController().navigate(R.id.action_postFragment_to_logInFragment)
            return
        }


        adapter = FollowAdapter()
        binding.recyclerViewFollowers.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewFollowers.adapter = adapter

        // افترض أن لديك توكن المستخدم

        // تحميل المتابعين
        viewModel.loadFollowers(userId, token!!)

        // مراقبة LiveData للمتابعين
        viewModel.followers.observe(viewLifecycleOwner, Observer { list ->
            adapter.submitFollowers(list)
        })

        // مراقبة الأخطاء
        viewModel.error.observe(viewLifecycleOwner, Observer { message ->
            message?.let {
                // يمكنك عرض Toast أو Snackbar
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

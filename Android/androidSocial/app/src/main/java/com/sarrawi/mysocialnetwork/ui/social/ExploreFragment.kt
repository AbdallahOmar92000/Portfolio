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
import androidx.recyclerview.widget.LinearLayoutManager
import com.sarrawi.mysocialnetwork.R
import com.sarrawi.mysocialnetwork.adapter.PostAdapter
import com.sarrawi.mysocialnetwork.adapter.TagsAdapter
import com.sarrawi.mysocialnetwork.api.ApiPost
import com.sarrawi.mysocialnetwork.databinding.FragmentExploreBinding
import com.sarrawi.mysocialnetwork.databinding.FragmentPostBinding
import com.sarrawi.mysocialnetwork.model.Tag
import com.sarrawi.mysocialnetwork.repository.PostRepository
import com.sarrawi.mysocialnetwork.viewmodel.PostViewModel
import com.sarrawi.mysocialnetwork.viewmodel.PostViewModelFactory


class ExploreFragment : Fragment() {

    private var _binding : FragmentExploreBinding?=null
    val binding get() = _binding!!

    private lateinit var viewModel: PostViewModel
    val adapter = TagsAdapter()
    var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentExploreBinding.inflate(inflater, container, false)

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
            return
        }

        binding.rvTags.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTags.adapter = adapter

        // مراقبة الوسوم
        viewModel.tagsState2.observe(viewLifecycleOwner) { tagsList ->
            adapter.submitList(tagsList)
            Log.d("ExploreFragment", "Adapter submitted ${tagsList.size} tags")
        }

        // مراقبة الأخطاء
        viewModel.error.observe(viewLifecycleOwner) { message ->
            message?.let { Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show() }
        }

        // تحميل البيانات مرة واحدة فقط
        viewModel.loadTags2(token!!)

        }


    }




//            val tagsList: List<Tag> = response
//                .flatMap { exploreResponse -> exploreResponse.posts } // جمع كل المنشورات
//                .flatMap { post -> post.tags }                       // جمع كل الوسوم من المنشورات
//                .filter { it.name.isNotEmpty() }
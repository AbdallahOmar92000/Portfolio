package com.sarrawi.mysocialnetwork.ui.social

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sarrawi.mysocialnetwork.R
import com.sarrawi.mysocialnetwork.adapter.PostAdapter
import com.sarrawi.mysocialnetwork.adapter.SuggestionsAdapter
import com.sarrawi.mysocialnetwork.api.ApiPost
import com.sarrawi.mysocialnetwork.databinding.FragmentSuggestionsBinding
import com.sarrawi.mysocialnetwork.repository.PostRepository
import com.sarrawi.mysocialnetwork.viewmodel.PostViewModel
import com.sarrawi.mysocialnetwork.viewmodel.PostViewModelFactory


class SuggestionsFragment : Fragment() {

    private var _binding : FragmentSuggestionsBinding?=null
    private val binding get()= _binding!!
    private lateinit var viewModel: PostViewModel
    val adapter = SuggestionsAdapter()
    var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding= FragmentSuggestionsBinding.inflate(inflater,container,false)

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

        binding.suggRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        setAdapter()
        viewModel.getSuggestions(token!!)

        viewModel.suggestions.observe(viewLifecycleOwner, Observer { list ->
            adapter.submitList(list)
        })
    }

    private fun setAdapter() {

        adapter.setOnEmail { post ->
            // داخل PostFragment
//            findNavController().navigate(
//                PostFragmentDirections.actionPostFragmentToUserProfileFragment(post.author.id)
//            )
            val navController = findNavController() // هذا مرتبط بالفراجمنت الحالي داخل Tab

            val action = UserProfileFragmentDirections
                .actionGlobalToUserProfileFragment(post.id)

            navController.navigate(action)




        }
        binding.suggRecyclerView.adapter = adapter

    }


}
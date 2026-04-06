package com.sarrawi.mysocialnetwork.ui.social

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sarrawi.mysocialnetwork.R
import com.sarrawi.mysocialnetwork.adapter.UsersAdapter
import com.sarrawi.mysocialnetwork.api.ApiPost
import com.sarrawi.mysocialnetwork.databinding.FragmentPostBinding
import com.sarrawi.mysocialnetwork.databinding.FragmentSearchFragmentBinding
import com.sarrawi.mysocialnetwork.repository.PostRepository
import com.sarrawi.mysocialnetwork.viewmodel.PostViewModel
import com.sarrawi.mysocialnetwork.viewmodel.PostViewModelFactory
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchFragmentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PostViewModel
    private lateinit var adapter: UsersAdapter
    private var token: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchFragmentBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setHomeButtonEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.hide()

        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        token = prefs.getString("auth_token", null)
        if (token == null) {
            Toast.makeText(requireContext(), "يرجى تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show()
        }

        val repo = PostRepository(ApiPost.provideRetrofitInstance(), requireContext())
        viewModel = ViewModelProvider(this, PostViewModelFactory(repo, requireContext()))[PostViewModel::class.java]

        adapter = UsersAdapter()
        adapter.setOnEmail {
            val navController = findNavController()
            val action = UserProfileFragmentDirections.actionGlobalToUserProfileFragment(it.id)
            navController.navigate(action)
        }
        binding.recyclerViewSearch.adapter = adapter
        binding.recyclerViewSearch.layoutManager = LinearLayoutManager(requireContext())

        setupSearchView()
        observeSearchResults()

        return binding.root
    }

    private fun setupSearchView() {
        binding.searchView.isIconified = false
        binding.searchView.requestFocus()
        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    token?.let { t ->
                        viewModel.searchUsers("Token $t", query)

                    }
                }
                binding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    token?.let { t ->
                        viewModel.searchUsers("Token $t", it)

                    }
                }
                return false
            }
        })
    }

    private fun observeSearchResults() {
        viewModel.searchResults.observe(viewLifecycleOwner) { response ->
            adapter.submitList(response?.profiles ?: emptyList())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


class SearchFragment2 : Fragment() {
    private var _binding: FragmentSearchFragmentBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchFragmentBinding.inflate(inflater, container, false)


        // التعامل مع البحث
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {

                }
                binding.searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // تحديث النتائج أثناء الكتابة (اختياري)
                return false
            }
        })
        return binding.root
    }


}
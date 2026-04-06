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
import com.sarrawi.mysocialnetwork.adapter.NotificationAdapter
import com.sarrawi.mysocialnetwork.adapter.PostAdapter
import com.sarrawi.mysocialnetwork.api.ApiPost
import com.sarrawi.mysocialnetwork.databinding.FragmentNotificationsBinding
import com.sarrawi.mysocialnetwork.repository.PostRepository
import com.sarrawi.mysocialnetwork.viewmodel.PostViewModel
import com.sarrawi.mysocialnetwork.viewmodel.PostViewModelFactory




class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PostViewModel
    private val adapter = NotificationAdapter()
    private var token: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1️⃣ استرجاع التوكن من SharedPreferences
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        token = prefs.getString("auth_token", null)
        if (token.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "يرجى تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show()
            return
        }

        // 2️⃣ إعداد RecyclerView
        binding.notifrecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.notifrecycler.adapter = adapter

        // 3️⃣ إعداد ViewModel
        val repo = PostRepository(ApiPost.provideRetrofitInstance(), requireContext())
        viewModel = ViewModelProvider(this, PostViewModelFactory(repo, requireContext()))
            .get(PostViewModel::class.java)

        // 4️⃣ مراقبة LiveData قبل أي تفاعل مع Adapter
        setupObservers()

        // 5️⃣ تحميل الإشعارات من السيرفر
        viewModel.fetchNotifications("Token $token")
        Log.d("NotifActivity", "Token used: Token $token")

        // 6️⃣ إعداد الـ Adapter للضغط على زر الحذف
        setupAdapter()
        binding.notifrecycler.adapter = adapter
    }

    private fun setupObservers() {
        // مراقبة قائمة الإشعارات
        viewModel.notifications.observe(viewLifecycleOwner) { notifications ->
            if (!notifications.isNullOrEmpty()) {
                adapter.submitList(notifications.toList()) // نسخة جديدة لتحديث UI
            } else {
                adapter.submitList(emptyList())
//                Toast.makeText(requireContext(), "لا توجد إشعارات", Toast.LENGTH_SHORT).show()
            }
        }

        // مراقبة حالة الحذف
        viewModel.removeStatus.observe(viewLifecycleOwner) { status ->
//            Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupAdapter() {
        adapter.setOnDeleteClickListener { notification ->
            viewModel.removeNotification2(notification.id, "Token $token")
            viewModel.fetchNotifications("Token $token")
        }
        binding.notifrecycler.adapter = adapter

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


package com.sarrawi.mysocialnetwork.ui.auth

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.sarrawi.mysocialnetwork.LoadingDialog
import com.sarrawi.mysocialnetwork.R
import com.sarrawi.mysocialnetwork.api.ApiAuth
import com.sarrawi.mysocialnetwork.databinding.FragmentLogInBinding
import com.sarrawi.mysocialnetwork.repository.AuthRepository
import com.sarrawi.mysocialnetwork.ui.MainActivity
import com.sarrawi.mysocialnetwork.viewmodel.AuthViewModel
import com.sarrawi.mysocialnetwork.viewmodel.AuthViewModelFactory

class LogInFragment : Fragment() {

    private var _binding: FragmentLogInBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AuthViewModel
    private lateinit var loadingDialog: LoadingDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLogInBinding.inflate(inflater, container, false)



        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
//        val sessionId = prefs.getString("sessionid", null)
//        if (sessionId != null) {
//            // إذا الجلسة محفوظة، انتقل مباشرة إلى الصفحة الرئيسية
//            findNavController().navigate(R.id.action_logInFragment_to_postFragment)
//        }

        val token = prefs.getString("auth_token", null)
        if(token != null) {
            // المستخدم مسجل دخول مسبقًا
            findNavController().navigate(R.id.action_logInFragment_to_mainTabsFragment)
        }



        val authRepository = AuthRepository(ApiAuth.provideRetrofitInstance(),requireContext())
        val factory = AuthViewModelFactory(authRepository, requireContext())
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        binding.signupPrompt.setOnClickListener {
            findNavController().navigate(R.id.action_logInFragment_to_registerFragment)
        }

        binding.btnSignin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Snackbar.make(binding.root, "يرجى إدخال البريد وكلمة المرور", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.login(email, password)
        }

        loadingDialog = LoadingDialog(requireContext())
        // ✅ مراقبة حالة التحميل
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                loadingDialog.show()
                binding.btnSignin.isEnabled = false
                binding.etEmail.isEnabled = false
                binding.etPassword.isEnabled = false
            } else {
                loadingDialog.dismiss()
                binding.btnSignin.isEnabled = true
                binding.etEmail.isEnabled = true
                binding.etPassword.isEnabled = true
            }
        }



        // ✅ مراقبة النتيجة


        viewModel.loginResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                // احصل على التوكن من ViewModel (مثلاً LiveData أو متغير)
                val token = viewModel.token.value  // تأكد أن الـ token محفوظ في ViewModel

                if (token != null) {
                    val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    prefs.edit().putString("auth_token", token).apply()  // احفظ التوكن
                }

                // انتقل للصفحة الرئيسية
                findNavController().navigate(R.id.action_logInFragment_to_mainTabsFragment)
            } else {
                Snackbar.make(binding.root, "Login failed, check data", Snackbar.LENGTH_SHORT).show()
            }
        }




        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

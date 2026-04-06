package com.sarrawi.mysocialnetwork.ui.auth

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.sarrawi.mysocialnetwork.LoadingDialog2
import com.sarrawi.mysocialnetwork.R
import com.sarrawi.mysocialnetwork.api.ApiAuth
import com.sarrawi.mysocialnetwork.databinding.FragmentRegisterBinding
import com.sarrawi.mysocialnetwork.repository.AuthRepository
import com.sarrawi.mysocialnetwork.ui.MainActivity
import com.sarrawi.mysocialnetwork.viewmodel.AuthViewModel
import com.sarrawi.mysocialnetwork.viewmodel.AuthViewModelFactory
import java.time.LocalDate
import java.util.*


class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AuthViewModel
    private lateinit var loadingDialog: LoadingDialog2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)



        val authRepository = AuthRepository(ApiAuth.provideRetrofitInstance(),requireContext())
        val factory = AuthViewModelFactory(authRepository, requireContext())
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        // تهيئة Spinner الأيام
        val days = (1..31).map { it.toString() }
        val dayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, days)
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerBirthDay.adapter = dayAdapter

        // تهيئة Spinner الشهور (الأسماء بالإنجليزية)
        val months = listOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        val monthAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, months)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerBirthMonth.adapter = monthAdapter

        // تهيئة Spinner السنوات (1950 إلى 2025)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)  // السنة الحالية ديناميكيًا

        val years = (1950..currentYear).map { it.toString() }
        val yearAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, years)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerBirthYear.adapter = yearAdapter
        // Dialog تحميل
        loadingDialog = LoadingDialog2(requireContext())

        loadingDialog = LoadingDialog2(requireContext())

        // ✅ مراقبة التحميل
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                loadingDialog.show()
                binding.btnSignup.isEnabled = false
            } else {
                loadingDialog.dismiss()
                binding.btnSignup.isEnabled = true
            }
        }

        // ✅ مراقبة نجاح أو فشل التسجيل
        viewModel.registerResult.observe(viewLifecycleOwner) { success ->
            if (success) {
                val token = viewModel.token.value
                val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                prefs.edit().putString("sessionid", token).apply()

                Snackbar.make(binding.root, "Registration successful", Snackbar.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_registerFragment_to_mainTabsFragment)
            } else {
                Snackbar.make(binding.root, "Registration failed, please check your data", Snackbar.LENGTH_SHORT).show()
            }
        }


        binding.loginLink.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_logInFragment)
        }






        binding.btnSignup.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val firstName = binding.etFirstName.text.toString().trim()
            val lastName = binding.etLastName.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            val day = binding.spinnerBirthDay.selectedItem.toString().toIntOrNull()
            val month = binding.spinnerBirthMonth.selectedItemPosition + 1
            val year = binding.spinnerBirthYear.selectedItem.toString().toIntOrNull()

            val gender = when (binding.radioGroupGender.checkedRadioButtonId) {
                R.id.radio_male -> "M"
                R.id.radio_female -> "F"
                R.id.radio_other -> "O"
                else -> ""
            }

            // ✅ التحقق من القيم الفارغة
            if (email.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || password.isEmpty()
                || confirmPassword.isEmpty() || day == null || month == null || year == null || gender.isEmpty()
            ) {
                Snackbar.make(binding.root, "Please fill all fields", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ✅ التحقق من صحة البريد الإلكتروني
            val emailPattern = Patterns.EMAIL_ADDRESS
            if (!emailPattern.matcher(email).matches()) {
                Snackbar.make(binding.root, "Invalid email address", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ✅ التحقق من مطابقة كلمتي المرور
            if (password != confirmPassword) {
                Snackbar.make(binding.root, "Passwords do not match", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ✅ تحقق من تعقيد كلمة المرور (مطابق للبنية في Django)
            if (password.length < 8) {
                Snackbar.make(binding.root, "Password must be at least 8 characters long", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!password.contains(Regex("[A-Z]"))) {
                Snackbar.make(binding.root, "Password must contain at least one uppercase letter", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!password.contains(Regex("[a-z]"))) {
                Snackbar.make(binding.root, "Password must contain at least one lowercase letter", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!password.contains(Regex("\\d"))) {
                Snackbar.make(binding.root, "Password must contain at least one number", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!password.contains(Regex("[!@#\$%^&*()_+=\\[\\]{};:\"\\\\|,.<>/?-]"))) {
                Snackbar.make(binding.root, "Password must contain at least one special character", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ✅ تحقق من تاريخ الميلاد
            val birthDate = try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LocalDate.of(year, month, day)
                } else {
                    TODO("VERSION.SDK_INT < O")
                }
            } catch (e: Exception) {
                Snackbar.make(binding.root, "Invalid birth date", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ✅ إذا وصلنا هنا، أرسل البيانات
            viewModel.register(
                email = email,
                firstName = firstName,
                lastName = lastName,
                password = password,
                confirmPassword = confirmPassword,
                day = day.toString(),
                month = month.toString(),
                year = year.toString(),
                gender = gender
            )
        }
        return binding.root
    }

}
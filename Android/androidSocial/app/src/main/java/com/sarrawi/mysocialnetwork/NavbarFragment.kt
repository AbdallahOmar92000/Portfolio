package com.sarrawi.mysocialnetwork

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.sarrawi.mysocialnetwork.databinding.FragmentNavbarBinding
import com.sarrawi.mysocialnetwork.databinding.FragmentPostBinding
import com.sarrawi.mysocialnetwork.databinding.NavBarBinding


class NavbarFragment : Fragment() {

    private var _binding: NavBarBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = NavBarBinding.inflate(inflater, container, false)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        binding.topAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_account -> {
                    showUserBottomSheet()
                    true
                }
                R.id.action_notifications -> {
                    Toast.makeText(requireContext(), "Notifications clicked", Toast.LENGTH_SHORT)
                        .show()
                    true
                }
                R.id.action_messages -> {
                    Toast.makeText(requireContext(), "Messages clicked", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }

            // لو تبغي تضيف دعم للقائمة تظهر في Toolbar
        }
    }


    private fun showUserBottomSheet() {
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_user_menu, null)
        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(bottomSheetView)

        val bottomNav = bottomSheetView.findViewById<BottomNavigationView>(R.id.bottom_navigation_user_menu)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_profile -> {
                    Toast.makeText(requireContext(), "Profile clicked", Toast.LENGTH_SHORT).show()
                    bottomSheetDialog.dismiss()
                    true
                }
                R.id.menu_logout -> {
                    Toast.makeText(requireContext(), "Logout clicked", Toast.LENGTH_SHORT).show()
                    bottomSheetDialog.dismiss()
                    true
                }
                else -> false
            }
        }

        bottomSheetDialog.show()
    }
}




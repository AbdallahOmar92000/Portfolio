package com.sarrawi.mysocialnetwork.ui.social

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

import com.sarrawi.mysocialnetwork.R


class MainTabsFragment : Fragment() , ExploreFragment2.OnUserClickListener{



    lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    var imgbtn:ImageButton?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_main_tabs, container, false)

//        val toolbar = view.findViewById<Toolbar>(R.id.topAppBara)
//        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)


        viewPager = view.findViewById(R.id.viewPager)
        tabLayout = view.findViewById(R.id.tabLayout)
        val imgbtn = view.findViewById<ImageButton>(R.id.button_search)
        imgbtn.setOnClickListener {
            // هنا ضع الكود عند الضغط على الزر
            findNavController().navigate(R.id.action_global_searchFragment)
        }

        val fragments = listOf(
            PostFragment(),
            SuggestionsFragment(),
            ExploreFragment2(),
            NotificationsFragment(),
            ProfileFragment()
        )

        val titles = listOf("Post", "Suggestions","Explore", "Notifications", "Profile")
        val icons = listOf(R.drawable.ic_home,R.drawable.ic_group,R.drawable.ic_explore,R.drawable.ic_notifications,R.drawable.ic_user)

        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount() = fragments.size
            override fun createFragment(position: Int) = fragments[position]
        }

//        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
//            tab.text = titles[position]
//
//            tab.setIcon(icons[position])
//        }.attach()

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->

    val tabView = LayoutInflater.from(context).inflate(R.layout.tab_custom_view, null)
    val tabIcon = tabView.findViewById<ImageView>(R.id.tab_icon)
    val tabText = tabView.findViewById<TextView>(R.id.tab_text)

    tabIcon.setImageResource(icons[position])
    tabText.text = titles[position]

    tab.customView = tabView


}.attach()

        return view
    }

    fun goToProfileTab() {
        viewPager.currentItem = 4 // رقم التاب الخاص بـ ProfileFragment
    }

    override fun onCurrentUserClicked() {
        viewPager.currentItem = 4

    }

    override fun onOtherUserClicked(userId: Int) {
        val bundle = Bundle().apply { putInt("userId", userId) }
        findNavController().navigate(R.id.userProfileFragment, bundle)
    }

}

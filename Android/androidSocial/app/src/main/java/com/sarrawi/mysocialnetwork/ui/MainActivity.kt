package com.sarrawi.mysocialnetwork.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.appbar.MaterialToolbar
import com.sarrawi.mysocialnetwork.R
import com.sarrawi.mysocialnetwork.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var topAppBar :MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.topAppBar)  // ← هذه الإضافة مهمة

//        binding.topAppBar.setOnMenuItemClickListener { item ->
//            when (item.itemId) {
////                R.id.nav_home -> {
////                    Toast.makeText(this, "Home", Toast.LENGTH_SHORT).show()
////                    true
////                }
//                R.id.nav_explore -> {
//                    Toast.makeText(this, "Explore", Toast.LENGTH_SHORT).show()
//                    true
//                }
//                R.id.action_messages -> {
//                    Toast.makeText(this, "Inbox", Toast.LENGTH_SHORT).show()
//                    true
//                }
//                R.id.action_notifications -> {
//                    Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show()
//                    true
//                }
//                R.id.nav_suggestions -> {
//                    Toast.makeText(this, "Suggestions", Toast.LENGTH_SHORT).show()
//                    true
//                }
//                R.id.action_search -> {
//                    Toast.makeText(this, "Search", Toast.LENGTH_SHORT).show()
//                    true
//                }
//                R.id.action_account -> {
//                    showGroupsMenu(it = findViewById(R.id.action_account))
//                    true
//                }
//                else -> false
//            }
//        }

        val navController = findNavController(R.id.nav_host_fragment_content_main)


        appBarConfiguration = AppBarConfiguration.Builder(R.id.logInFragment)
            // إذا كنت تستخدم درج التنقل
            .build()
        setupActionBarWithNavController(navController, appBarConfiguration)
        supportActionBar?.hide()


        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.logInFragment -> showToolbar(false) // تخفي Toolbar في شاشة تسجيل الدخول
                R.id.mainTabsFragment -> showToolbar(false) // تخفي Toolbar في شاشة تسجيل الدخول
                else -> showToolbar(true) // تظهر Toolbar في بقية الشاشات
            }
        }



    }



    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    private fun showGroupsMenu(it: View) {
        val popup = PopupMenu(this, it)
        popup.menuInflater.inflate(R.menu.bottom_nav_menu, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_logout -> {
                    Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.menu_profile -> {
                    Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    fun showToolbar(show: Boolean) {
        binding.topAppBar.visibility = if (show) View.VISIBLE else View.GONE

    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.top_appbar_menu, menu)
//
//        val searchItem = menu!!.findItem(R.id.action_search)
//        val searchView = searchItem.actionView as SearchView
//
//        searchView.queryHint = "Search email"
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                // نفذ عملية البحث هنا
//                Toast.makeText(this@MainActivity, "Searching: $query", Toast.LENGTH_SHORT).show()
//                searchView.clearFocus()
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                // أثناء الكتابة يمكنك تنفيذ فلترة مباشرة إذا أردت
//                return false
//            }
//        })
//
//        return true
//    }

}
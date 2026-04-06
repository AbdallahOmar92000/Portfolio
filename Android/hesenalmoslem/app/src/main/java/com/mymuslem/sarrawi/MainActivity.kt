package com.mymuslem.sarrawi

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mymuslem.sarrawi.adapter.TypefaceChangeListener
import com.mymuslem.sarrawi.databinding.ActivityMainBinding
import com.mymuslem.sarrawi.db.viewModel.ZekerTypesViewModel


class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: ZekerTypesViewModel
    private lateinit var bottomNav : BottomNavigationView
    var fragment = 1
    private val zekerTypesViewModel: ZekerTypesViewModel by lazy {
        ViewModelProvider(this,ZekerTypesViewModel.AzkarViewModelFactory(this.application))[ZekerTypesViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        bottomNav = findViewById(R.id.bottomNav)
        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
        appBarConfiguration = AppBarConfiguration(setOf(R.id.firsFragment,R.id.favoriteFragment,R.id.fragmentSetting))

        bottomNav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if( destination.id == R.id.splashFragment||destination.id == R.id.fragmentViewPager  ) {

                bottomNav.visibility = View.GONE
            } else {

                bottomNav.visibility = View.VISIBLE
            }
        }

//        setupActionBarWithNavController(navController, appBarConfiguration)

//        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }



  }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }


}
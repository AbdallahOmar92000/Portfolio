package com.sarrawi.mybattery

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.sarrawi.mybattery.databinding.FragmentFirstBinding
import com.sarrawi.mybattery.databinding.FragmentSplashBinding


class SplashFragment : Fragment() {

    private var _binding:FragmentSplashBinding?=null
    private val binding get()=_binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val img=view.findViewById<ImageView>(R.id.imageView2)

        Handler(Looper.getMainLooper()).postDelayed({
            val navController = findNavController()

            if (isAdded && navController.currentDestination?.id == R.id.splashFragment) {
                navController.navigate(
                    R.id.action_splashFragment_to_FirstFragment,
                    null,
                    NavOptions.Builder()
                        .setPopUpTo(R.id.splashFragment, true)
                        .build()
                )
            }

        }, 5000)
    }
}
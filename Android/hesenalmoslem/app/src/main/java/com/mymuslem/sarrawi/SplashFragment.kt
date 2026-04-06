package com.mymuslem.sarrawi

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.mymuslem.sarrawi.databinding.FragmentSplashBinding


class SplashFragment : Fragment() {
    private lateinit var _binding : FragmentSplashBinding
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_splash, container, false)
        _binding = FragmentSplashBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler(Looper.myLooper()!!).postDelayed({
            findNavController()
                .navigate(R.id.action_splashFragment_to_FirstFragment,null,NavOptions
                    .Builder()
                    .setPopUpTo(
                    R.id.splashFragment,true).build())
        },5000)
    }

}
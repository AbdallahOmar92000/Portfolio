package com.mymuslem.sarrawi

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.mymuslem.sarrawi.adapter.FavoriteAdapter
import com.mymuslem.sarrawi.databinding.FragmentSecondBinding
import com.mymuslem.sarrawi.db.viewModel.SettingsViewModel
import com.mymuslem.sarrawi.db.viewModel.ZekerTypesViewModel
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondFragment : Fragment() {

    private var _binding: FragmentSecondBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var font1: Typeface? = null
    private var font2: Typeface? = null
    private var font3: Typeface? = null
    private var font4: Typeface? = null
    private var font5: Typeface? = null
    private var font6: Typeface? = null
    private var font7: Typeface? = null
    private var Ffont: Typeface? = null
    var clickCount = 0
    var mInterstitialAd: InterstitialAd?=null
    private val favoriteAdapter by lazy { FavoriteAdapter(requireContext(),this,Ffont) }

    private val zekerTypesViewModel: ZekerTypesViewModel by lazy {
        ViewModelProvider(this,
            ZekerTypesViewModel.AzkarViewModelFactory(requireActivity().application))[ZekerTypesViewModel::class.java]
    }

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentSecondBinding.inflate(inflater, container, false)
        (activity as MainActivity).fragment = 1

        return binding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val sharedPref = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        val fontSize = sharedPref.getInt("font_size", 14)
        settingsViewModel.fontSize = fontSize

        favoriteAdapter.notifyDataSetChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        InterstitialAd_fun()
        initFonts()
        setUpRv()
        adapterOnClick()
        specifyFont()
    }

    private fun adapterOnClick() {
        favoriteAdapter.onItemClick = { tID ->
            clickCount++
            if (clickCount >= 4) {
// بمجرد أن يصل clickCount إلى 4، اعرض الإعلان
                if (mInterstitialAd != null) {
                    mInterstitialAd?.show(requireActivity())
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.")
                }
                clickCount = 0 // اعيد قيمة المتغير clickCount إلى الصفر بعد عرض الإعلان

            }
            val direction = SecondFragmentDirections.actionSecondFragmentToFragmentViewPager(tID)
            findNavController().navigate(direction)
        }

        favoriteAdapter.del_fav = {
            zekerTypesViewModel.viewModelScope.launch {
                zekerTypesViewModel.update_fav(it.ID!!, 0) // update item state
                zekerTypesViewModel.delete_fav(it)
                Toast.makeText(requireContext(), "تم الحذف من المفضلة", Toast.LENGTH_SHORT).show()
                setUpRv()
                favoriteAdapter.notifyDataSetChanged()
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private  fun setUpRv() = zekerTypesViewModel.viewModelScope.launch {
        favoriteAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.ALLOW
        zekerTypesViewModel.getFav().observe(viewLifecycleOwner) { listShows ->
            favoriteAdapter.zeker_fav_list = listShows
            if (binding.recyclerViewFav.adapter == null) {
                binding.recyclerViewFav.layoutManager = LinearLayoutManager(requireContext())
                binding.recyclerViewFav.adapter = favoriteAdapter
                favoriteAdapter.notifyDataSetChanged()
            } else {
                favoriteAdapter.notifyDataSetChanged()
            }
            Log.e("tessst", "enter111")
        }
    }

    private fun initFonts() {
        font1 = Typeface.createFromAsset(requireContext().assets, "fonts/a.otf")
        font2 = Typeface.createFromAsset(requireContext().assets, "fonts/ab.otf")
        font3 = Typeface.createFromAsset(requireContext().assets, "fonts/ac.otf")
        font4 = Typeface.createFromAsset(requireContext().assets, "fonts/ad.otf")
        font5 = Typeface.createFromAsset(requireContext().assets, "fonts/ae.otf")
        font6 = Typeface.createFromAsset(requireContext().assets, "fonts/af.otf")
        font7 = Typeface.createFromAsset(requireContext().assets, "fonts/ag.otf")
    }

    private fun specifyFont() {
        val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val fontIndex = sp.getInt("font", 0) // استخراج رقم الخط المحدد

        Ffont = when (fontIndex) {
            0 -> font1
            1 -> font2
            2 -> font3
            3 -> font4
            4 -> font5
            5 -> font6
            6 -> font7
            else -> font1
        }

        favoriteAdapter?.setFont(Ffont)
        favoriteAdapter?.notifyDataSetChanged()
        val editor = sp.edit()
        editor.putInt("font", fontIndex)
        editor.apply()
    }

    fun InterstitialAd_fun (){


        MobileAds.initialize(requireActivity()) { initializationStatus ->
            // do nothing on initialization complete
        }

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            requireActivity(),
            "ca-app-pub-1895204889916566/9786749620",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    // The mInterstitialAd reference will be null until an ad is loaded.
                    mInterstitialAd = interstitialAd
                    Log.i("onAdLoadedL", "onAdLoaded")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    // Handle the error
                    Log.d("onAdLoadedF", loadAdError.toString())
                    mInterstitialAd = null
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


//package com.mymuslem.sarrawi
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.graphics.Typeface
//import android.os.Bundle
//import android.preference.PreferenceManager
//import android.util.Log
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.Button
//import android.widget.Toast
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.viewModelScope
//import androidx.navigation.fragment.findNavController
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.mymuslem.sarrawi.adapter.FavoriteAdapter
//import com.mymuslem.sarrawi.databinding.FragmentSecondBinding
//import com.mymuslem.sarrawi.db.viewModel.SettingsViewModel
//import com.mymuslem.sarrawi.db.viewModel.ZekerTypesViewModel
//import kotlinx.coroutines.launch
//
///**
// * A simple [Fragment] subclass as the second destination in the navigation.
// */
//class SecondFragment : Fragment() {
//
//    private var _binding: FragmentSecondBinding? = null
//
//    // This property is only valid between onCreateView and
//    // onDestroyView.
//    private val binding get() = _binding!!
//    private var font1: Typeface? = null
//    private var font2: Typeface? = null
//    private var font3: Typeface? = null
//    private var font4: Typeface? = null
//    private var font5: Typeface? = null
//    private var font6: Typeface? = null
//    private var font7: Typeface? = null
//    private var Ffont: Typeface? = null
//
//    private val favoriteAdapter by lazy { FavoriteAdapter(requireContext(),this,Ffont) }
//
//    private val zekerTypesViewModel: ZekerTypesViewModel by lazy {
//        ViewModelProvider(this,
//            ZekerTypesViewModel.AzkarViewModelFactory(requireActivity().application))[ZekerTypesViewModel::class.java]
//    }
//
//    private lateinit var settingsViewModel: SettingsViewModel
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//
//        _binding = FragmentSecondBinding.inflate(inflater, container, false)
//        (activity as MainActivity).fragment = 1
//
//        return binding.root
//
//    }
//
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        val sharedPref = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
//
//        val fontSize = sharedPref.getInt("font_size", 14)
//        settingsViewModel.fontSize = fontSize
//
//        favoriteAdapter.notifyDataSetChanged()
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
//        initFonts()
//        setUpRv()
//        adapterOnClick()
//        specifyFont()
//
//
//
//
//    }
//
//
//    private fun adapterOnClick() {
//
//        favoriteAdapter.onItemClick = {tID->
////            val toastText = "تم النقر على العنصر بالرقم: $ID"
////            Toast.makeText(requireContext(), toastText, Toast.LENGTH_SHORT).show()
////            Log.i("MyApp", "$ID")
//            val direction=SecondFragmentDirections.actionSecondFragmentToFragmentViewPager(tID)
//            findNavController().navigate(direction)
//
//        }
//
//        favoriteAdapter.del_fav = {
//            zekerTypesViewModel.viewModelScope.launch {
//                zekerTypesViewModel.update_fav(it.ID!!,0) // update item state
//                zekerTypesViewModel.delete_fav(it)
////                val result = mainRepository.deleteFav(it)   // delete favorite item from db
//                Toast.makeText(requireContext(),"تم الحذف من المفضلة", Toast.LENGTH_SHORT).show()
//                setUpRv()
//                favoriteAdapter.notifyDataSetChanged()
//            }
//
//        }
//    }
//
//    @SuppressLint("SuspiciousIndentation")
//    private  fun setUpRv() = zekerTypesViewModel.viewModelScope.launch {
//
////        binding.rcMsgTypes.apply {
////            adapter = msgstypesAdapter
////            setHasFixedSize(true)
////        }
//
//
//        zekerTypesViewModel.getFav().observe(viewLifecycleOwner) { listShows ->
//            //  msgsAdapter.stateRestorationPolicy=RecyclerView.Adapter.StateRestorationPolicy.ALLOW
//            favoriteAdapter.stateRestorationPolicy= RecyclerView.Adapter.StateRestorationPolicy.ALLOW
////            msgsAdapter.msgsModel = listShows
////            binding.rcMsgs.adapter = msgsAdapter
//            favoriteAdapter.zeker_fav_list = listShows
//            if(binding.recyclerViewFav.adapter == null){
//                binding.recyclerViewFav.layoutManager = LinearLayoutManager(requireContext())
//                binding.recyclerViewFav.adapter = favoriteAdapter
//                favoriteAdapter.notifyDataSetChanged()
//            }else{
//                favoriteAdapter.notifyDataSetChanged()
//            }
//            Log.e("tessst","enter111")
//
//        }
//    }
//
//    private fun initFonts() {
//        font1 = Typeface.createFromAsset(requireContext().assets, "fonts/a.otf")
//        font2 = Typeface.createFromAsset(requireContext().assets, "fonts/ab.otf")
//        font3 = Typeface.createFromAsset(requireContext().assets, "fonts/ac.otf")
//        font4 = Typeface.createFromAsset(requireContext().assets, "fonts/ad.otf")
//        font5 = Typeface.createFromAsset(requireContext().assets, "fonts/ae.otf")
//        font6 = Typeface.createFromAsset(requireContext().assets, "fonts/af.otf")
//        font7 = Typeface.createFromAsset(requireContext().assets, "fonts/ag.otf")
//    }
//
//
//    private fun specifyFont() {
//        val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
//        val fontIndex = sp.getInt("font", 0) // استخراج رقم الخط المحدد
//
//        Ffont = when (fontIndex) {
//            0 -> font1
//            1 -> font2
//            2 -> font3
//            3 -> font4
//            4 -> font5
//            5 -> font6
//            6 -> font7
//            else -> font1
//        }
//
//        favoriteAdapter?.setFont(Ffont)
//        favoriteAdapter?.notifyDataSetChanged()
//        val editor = sp.edit()
//        editor.putInt("font", fontIndex)
//        editor.apply()
//    }
//
//}
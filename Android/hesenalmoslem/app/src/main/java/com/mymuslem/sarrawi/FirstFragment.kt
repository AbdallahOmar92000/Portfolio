package com.mymuslem.sarrawi

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
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
import com.mymuslem.sarrawi.adapter.TypefaceChangeListener
import com.mymuslem.sarrawi.adapter.ZekerTypes_Adapter
import com.mymuslem.sarrawi.databinding.FragmentFirstBinding
import com.mymuslem.sarrawi.db.viewModel.SettingsViewModel
import com.mymuslem.sarrawi.db.viewModel.ZekerTypesViewModel
import com.mymuslem.sarrawi.models.FavoriteModel
import com.mymuslem.sarrawi.models.Letters
import kotlinx.coroutines.launch


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment(), SearchView.OnQueryTextListener  {

    lateinit var _binding: FragmentFirstBinding
    private val binding get() = _binding!!
    private var font1: Typeface? = null
    private var font2: Typeface? = null
    private var font3: Typeface? = null
    private var font4: Typeface? = null
    private var font5: Typeface? = null
    private var font6: Typeface? = null
    private var font7: Typeface? = null
    private var Ffont: Typeface? = null
    private val zekerTypesViewModel: ZekerTypesViewModel by lazy {
        ViewModelProvider(this,ZekerTypesViewModel.AzkarViewModelFactory(requireActivity().application))[ZekerTypesViewModel::class.java]
    }
    private val zekertypesAdapter by lazy {  ZekerTypes_Adapter(requireContext(), this,Ffont/*isDark*/) }

    private lateinit var settingsViewModel: SettingsViewModel

    var clickCount = 0
    var mInterstitialAd: InterstitialAd?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // استرداد إعدادات الخط من SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val fontSize = sharedPref.getInt("font_size", 14)

        // إنشاء ViewModel للإعدادات
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
        settingsViewModel.fontSize = fontSize

        // تحديث الـ Adapter ليعرض الخط المحدد
        zekertypesAdapter.notifyDataSetChanged()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // إنشاء ViewModel للإعدادات
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        InterstitialAd_fun()
        // تهيئة الخطوط المختلفة
        initFonts()

        // إعداد RecyclerView و Adapter
        setUpRv()

        // إعداد قائمة الخيارات في القائمة العلوية
        menu_item()

        // التعامل مع حدث النقر على عنصر في الـ Adapter
        adapterOnClick()

        // تحديد الخط المحدد وتغيير الخط عند الحاجة
        specifyFont()



    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onDestroyView() {
        super.onDestroyView()

    }

    private fun adapterOnClick() {

        zekertypesAdapter.onItemClick = {ID->

            clickCount++
            if (clickCount >= 1) {
// بمجرد أن يصل clickCount إلى 4، اعرض الإعلان
                if (mInterstitialAd != null) {
                    mInterstitialAd?.show(requireActivity())
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.")
                }
                clickCount = 0 // اعيد قيمة المتغير clickCount إلى الصفر بعد عرض الإعلان

            }

            val direction = FirstFragmentDirections.actionFirsFragmentToFragmentViewPager(ID)
                findNavController().navigate(direction)
            Log.d("MyApp", "$ID")
        }

        zekertypesAdapter.onFavClick = { it: Letters, i: Int ->
            val fav = FavoriteModel(it.ID!!, it.Name)
            // check if item is favorite or not

            if (it.Fav == 0) {
                zekerTypesViewModel.update_fav(it.ID!!, 1) // update favorite item state
                zekerTypesViewModel.add_fav(fav) // add item to db
                Toast.makeText(requireContext(), "تم الاضافة الى المفضلة", Toast.LENGTH_SHORT).show()
                setUpRv()

                zekertypesAdapter.notifyDataSetChanged()
            } else {
                zekerTypesViewModel.update_fav(it.ID!!, 0) // update favorite item state
                zekerTypesViewModel.delete_fav(fav) // delete item from db
                Toast.makeText(requireContext(), "تم الحذف من المفضلة", Toast.LENGTH_SHORT).show()
                setUpRv()
                zekertypesAdapter.notifyDataSetChanged()
            }

        }




    }

    private fun setUpRv() = zekerTypesViewModel.viewModelScope.launch {



        zekerTypesViewModel.getAllZekerTypes().observe(requireActivity()) { listShows ->
            //     Log.e("tessst",listTvShows.size.toString()+"  adapter")
            zekertypesAdapter.stateRestorationPolicy= RecyclerView.Adapter.StateRestorationPolicy.ALLOW
            zekertypesAdapter.zekerTypes_list = listShows
            if(binding.recyclerView.adapter == null){

                binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                binding.recyclerView.adapter = zekertypesAdapter
                zekertypesAdapter.notifyDataSetChanged()
            }else{
                zekertypesAdapter.notifyDataSetChanged()
            }


        }

    }

    private fun menu_item() {
        // The usage of an interface lets you inject your own implementation
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                menuInflater.inflate(R.menu.first_frag_menu, menu)

                val search = menu?.findItem(R.id.menu_search)
                val searchView = search?.actionView as androidx.appcompat.widget.SearchView

                searchView.setOnQueryTextListener(object: androidx.appcompat.widget.
                SearchView.OnQueryTextListener{
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        if (query != null) {

                            searchDatabase(query)
                        }
                        return true
                    }
//dd
                    override fun onQueryTextChange(newText: String?): Boolean {
                        if (newText != null) {
                            searchDatabase(newText)
                        }
                        return true
                    }

                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                when(menuItem.itemId){

//                    R.id.action_theme -> {


//                        val prefs = SharedPref(requireContext())
//                        val isDark = prefs.getThemeStatePref()
//                        prefs.saveThemeStatePref(!isDark)
//
//                        if(isDark){
//                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//                        }
//                        else{
//                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//                        }
//                    }

                }
                return true
            }

        },viewLifecycleOwner, Lifecycle.State.RESUMED)
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

        zekertypesAdapter?.setFont(Ffont)
        zekertypesAdapter?.notifyDataSetChanged()
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

    private fun searchDatabase(query: String) {
        val searchQuery = "%$query%"


//        adapter = NoteAdapter(this@MainActivity,onItemClick,onItemDelete)

        zekerTypesViewModel.SearchViewModel(searchQuery).observe(requireActivity(), Observer {

            zekertypesAdapter.setLetters(it)
        })


    }



    override fun onQueryTextSubmit(query: String?): Boolean {
        if(query != null){
            val formattedQuery = "<font color='#FF0000'>$query</font>" // تغيير لون الكلمة إلى اللون الأحمر (#FF0000)
            searchDatabase(formattedQuery)
            searchDatabase(query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if(query != null){
            searchDatabase(query)
        }
        return true
    }

}
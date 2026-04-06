package com.mymuslem.sarrawi

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.mymuslem.sarrawi.adapter.VPagerAdapter
import com.mymuslem.sarrawi.db.viewModel.SettingsViewModel
import com.mymuslem.sarrawi.db.viewModel.ZekerViewModel
import com.mymuslem.sarrawi.models.Zekr
import com.mymuslem.sarrawi.utils.CopyText
import com.mymuslem.sarrawi.utils.ShareText
import me.relex.circleindicator.CircleIndicator3

class FragmentViewPager : Fragment() {
    private var zeker_list = mutableListOf<Zekr>()
    var view_pager2: ViewPager2? = null
    private val zekerViewModel: ZekerViewModel by lazy {
        ViewModelProvider(this, ZekerViewModel.AzkarViewModelFactory(requireActivity().application))[ZekerViewModel::class.java]
    }

    private var argsId = -1
    private var argId = -1
    private var font1: Typeface? = null
    private var font2: Typeface? = null
    private var font3: Typeface? = null
    private var font4: Typeface? = null
    private var font5: Typeface? = null
    private var font6: Typeface? = null
    private var font7: Typeface? = null
    private var Ffont: Typeface? = null
    var adView: AdView?=null

    private lateinit var settingsViewModel: SettingsViewModel
    private val adapter by lazy { VPagerAdapter(requireContext(), zeker_list, this, Ffont) }
//ss
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        argsId = FragmentViewPagerArgs.fromBundle(requireArguments()).typeID
        argId = FragmentViewPagerArgs.fromBundle(requireArguments()).typeID

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_view_pager, container, false)
        view_pager2 = rootView.findViewById(R.id.ss)
        val ind: CircleIndicator3 = rootView.findViewById(R.id.aa)

        view_pager2?.let {
            it.adapter = adapter
            it.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            ind.setViewPager(it)
            val currentPosition = view_pager2!!.currentItem ?: 0
            view_pager2!!.currentItem = currentPosition + 1
        }
        adView= rootView.findViewById(R.id.adView)
        adView?.loadAd(AdRequest.Builder().build())  // تحميل الإعلان

        zekerViewModel.getAllZeker(argsId).observe(viewLifecycleOwner) { updatedZekerList ->
            zeker_list.clear()
            zeker_list.addAll(updatedZekerList)
            view_pager2?.setCurrentItem(zeker_list.size, false)
            ind.setViewPager(view_pager2)
            adapter.notifyDataSetChanged()
        }
        initFonts()
        specifyFont()

        setHasOptionsMenu(true)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFonts()
        specifyFont()
        menu_item()
    }



    override fun onDestroyView() {
        super.onDestroyView()

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val sharedPref = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        val fontSize = sharedPref.getInt("font_size", 14)
        settingsViewModel.fontSize = fontSize

        adapter.notifyDataSetChanged()
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

        adapter?.setFont(Ffont)
        adapter?.notifyDataSetChanged()
        val editor = sp.edit()
        editor.putInt("font", fontIndex)
        editor.apply()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        // إنشاء القائمة باستخدام XML layout المحدد
        inflater.inflate(R.menu.menu_zeker, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_copay -> {
                val zekr = zeker_list[view_pager2?.currentItem ?: 0]
                val stringYouExtracted: String = zekr.Description ?: ""
                Log.d("CopyText", "Description: $stringYouExtracted")

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    Log.d("CopyText", "Using ClipboardManager")
                    val clipboard =
                        requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.text = stringYouExtracted
                } else {
                    Log.d("CopyText", "Using android.content.ClipboardManager")
                    val clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

                    val clip = ClipData.newPlainText("copied_text", stringYouExtracted)
                    clipboardManager.setPrimaryClip(clip)

                    Toast.makeText(requireContext(), "تم نسخ النص", Toast.LENGTH_SHORT).show()
                }


                // Perform the copy action here
                // ... (بقية الكود للنسخ)
                return true
            }
            R.id.action_share -> {
                val zekr = zeker_list[view_pager2?.currentItem ?: 0]
                // Perform the share action using ShareText class
                ShareText.shareText(
                    requireContext(),
                    "Share via",
                    "Zekr Header",
                    zekr.Description ?: ""
                )
                return true
            }
            // ... (قائمة من المزيد من العناصر)
            else -> return super.onOptionsItemSelected(item)
        }
    }


    private fun menu_item() {
        // The usage of an interface lets you inject your own implementation

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                // Add menu items here
                // menuInflater.inflate(R.menu.menu_zeker, menu) // هنا لا داعي لتكرار هذا السطر
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {

                when (menuItem.itemId) {
                    R.id.action_copy -> {
                        val zekr = zeker_list[view_pager2?.currentItem ?: 0]
                        // Perform the copy action here
                        // ... (بقية الكود للنسخ)
                        return true
                    }
                    R.id.action_share -> {
                        val zekr = zeker_list[view_pager2?.currentItem ?: 0]
                        // Perform the share action using ShareText class
                        ShareText.shareText(
                            requireContext(),
                            "Share via",
                            "Zekr Header",
                            zekr.Description ?: ""
                        )
                        return true
                    }
                    // ... (قائمة من المزيد من العناصر)
                }
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.menu_zeker, menu)
//        super.onCreateOptionsMenu(menu, inflater)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.action_copy -> {
//                val zekr = zeker_list[view_pager2?.currentItem ?: 0]
//                // Perform the copy action here
//                val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
//                val clip = ClipData.newPlainText("Zekr Text", zekr.Description)
//                clipboard.setPrimaryClip(clip)
//                return true
//            }
//            R.id.action_share -> {
//                val zekr = zeker_list[view_pager2?.currentItem ?: 0]
//                // Perform the share action using ShareText class
//                ShareText.shareText(
//                    requireContext(),
//                    "Share via",
//                    "Zekr Header",
//                    zekr.Description?:""
//                )
//                return true
//            }
//            else -> return super.onOptionsItemSelected(item)
//        }
//    }


}

//package com.mymuslem.sarrawi
//
//import android.content.Context
//import android.graphics.Typeface
//import android.os.Bundle
//import android.preference.PreferenceManager
//import androidx.fragment.app.Fragment
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.lifecycle.ViewModelProvider
//import androidx.viewpager2.widget.ViewPager2
//import com.mymuslem.sarrawi.adapter.VPagerAdapter
//import com.mymuslem.sarrawi.adapter.ZekerTypes_Adapter
//import com.mymuslem.sarrawi.db.viewModel.SettingsViewModel
//import com.mymuslem.sarrawi.db.viewModel.ZekerTypesViewModel
//import com.mymuslem.sarrawi.db.viewModel.ZekerViewModel
//import com.mymuslem.sarrawi.models.Zekr
//import me.relex.circleindicator.CircleIndicator3
//
//
//class FragmentViewPager : Fragment() {
//    private var zeker_list = mutableListOf<Zekr>()
//    var view_pager2: ViewPager2?=null
//    private val zekerViewModel: ZekerViewModel by lazy {
//        ViewModelProvider(this,
//            ZekerViewModel.AzkarViewModelFactory(requireActivity().application))[ZekerViewModel::class.java]
//    }
//
//    private var argsId = -1
//    private var argId = -1
//    private var font1: Typeface? = null
//    private var font2: Typeface? = null
//    private var font3: Typeface? = null
//    private var font4: Typeface? = null
//    private var font5: Typeface? = null
//    private var font6: Typeface? = null
//    private var font7: Typeface? = null
//    private var Ffont: Typeface? = null
//    private lateinit var settingsViewModel: SettingsViewModel
//    private val adapter by lazy{  VPagerAdapter(requireContext(),zeker_list,this,Ffont) }
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        argsId= FragmentViewPagerArgs.fromBundle(requireArguments()).typeID
//        argId=FragmentViewPagerArgs.fromBundle(requireArguments()).typeID
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val rootView = inflater.inflate(R.layout.fragment_view_pager, container, false)
//        view_pager2 = rootView.findViewById(R.id.ss)
////        val adapter = VPagerAdapter(requireContext(),zeker_list,this,Ffont)
//        val ind: CircleIndicator3 = rootView.findViewById(R.id.aa)
//
//        view_pager2?.let {
//            it.adapter = adapter
//            it.orientation = ViewPager2.ORIENTATION_HORIZONTAL
//            ind.setViewPager(it)
//            val currentPosition = view_pager2!!.currentItem ?: 0
//            view_pager2!!.currentItem = currentPosition + 1
////            if (currentPosition != null) {
////                view_pager2!!.currentItem = currentPosition + 1
////            }
//        }
//
//        zekerViewModel.getAllZeker(argsId).observe(viewLifecycleOwner) { updatedZekerList ->
//            zeker_list.clear()
//            zeker_list.addAll(updatedZekerList)
//            view_pager2?.setCurrentItem(zeker_list.size,false)
////            view_pager2?.currentItem = zeker_list.size
//            ind.setViewPager(view_pager2)
////            ind.createIndicators(updatedZekerList.size)
//            adapter.notifyDataSetChanged()
//        }
//
//        val sharedPref = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//
//        var settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
//        val fontSize = sharedPref.getInt("font_size", 14)
//        settingsViewModel.fontSize = fontSize
//
//        adapter.notifyDataSetChanged()
//
//        initFonts()
//        specifyFont()
//        return rootView
//    }
//
//
//    override fun onActivityCreated(savedInstanceState: Bundle?) {
//        super.onActivityCreated(savedInstanceState)
//        val sharedPref = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
//        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
//
//        val fontSize = sharedPref.getInt("font_size", 14)
//        settingsViewModel.fontSize = fontSize
//
//        adapter.notifyDataSetChanged()
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//
//    }
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
//        adapter?.setFont(Ffont)
//        adapter?.notifyDataSetChanged()
//        val editor = sp.edit()
//        editor.putInt("font", fontIndex)
//        editor.apply()
//    }
//}
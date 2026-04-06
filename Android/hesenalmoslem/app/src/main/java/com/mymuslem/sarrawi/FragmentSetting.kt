package com.mymuslem.sarrawi

import android.Manifest
import androidx.appcompat.widget.SwitchCompat
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDeepLinkBuilder
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.mymuslem.sarrawi.adapter.TypefaceChangeListener
import com.mymuslem.sarrawi.adapter.ZekerTypes_Adapter
import com.mymuslem.sarrawi.db.viewModel.SettingsViewModel
import com.mymuslem.sarrawi.notification.AlarmReceiverAM
import com.mymuslem.sarrawi.notification.AlarmReceiverPM
import java.util.*


class FragmentSetting : Fragment() {

    // Views
    private lateinit var tvSeekBarValue: TextView
    private lateinit var tv_Size: TextView
    private lateinit var tv_Type: TextView
    private lateinit var spFont: Spinner
    private lateinit var nightModeSwitch: SwitchCompat
    private lateinit var notifi: SwitchCompat
    private val NOTIFICATION_PERMISSION_CODE = 100


    private val font = arrayOf(
        "الخط الافتراضي",
        "الخط الاول",
        "الخط الثاني",
        "الخط الثالث",
        "الخط الرابع",
        "الخط الخامس",
        "الخط السادس"
    )
    private var font1: Typeface? = null
    private var font2: Typeface? = null
    private var font3: Typeface? = null
    private var font4: Typeface? = null
    private var font5: Typeface? = null
    private var font6: Typeface? = null
    private var font7: Typeface? = null
    private var Ffont: Typeface? = null




    // ViewModel
    private lateinit var settingsViewModel: SettingsViewModel

    // Shared Preferences
    private val sharedPref by lazy { requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE) }
    private lateinit var sharedPreferences: SharedPreferences



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootview = inflater.inflate(R.layout.fragment_setting, container, false)
        sharedPreferences = requireActivity().getSharedPreferences("MyPrefs",
            AppCompatActivity.MODE_PRIVATE
        )
        val sh_pref = SharedPref(requireContext())


        // Initialize Views
        tvSeekBarValue = rootview.findViewById(R.id.tvSeekBarValue)
        tv_Size = rootview.findViewById(R.id.fontSize)
        spFont = rootview.findViewById(R.id.fontTypeSpinner)
        tv_Type = rootview.findViewById(R.id.fontTypeLabel)
        nightModeSwitch = rootview.findViewById(R.id.nightModeSwitch)
        notifi = rootview.findViewById(R.id.noti)

        nightModeSwitch.isChecked = sh_pref.getThemeStatePref()

        // Initialize ViewModel
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        // SeekBar and TextView initialization
        val fontSizeSeekBar: SeekBar = rootview.findViewById(R.id.fontSizeSeekBar)
        val fontSize = sharedPref.getInt("font_size", 14)
        val fontSizetvSeekBarValue = sharedPref.getInt("tvSeekBarValue", 14)
        fontSizeSeekBar.progress = fontSize.coerceIn(12, 40)

        if (savedInstanceState != null) {
            val savedValue = savedInstanceState.getInt("tvSeekBarValue")
            tvSeekBarValue.text = savedValue.toString()
            settingsViewModel.fontSize = savedValue
            tv_Size.textSize = settingsViewModel.fontSize.toFloat()
        } else {
            tvSeekBarValue.text = fontSizetvSeekBarValue.toString()
            settingsViewModel.fontSize = fontSizetvSeekBarValue
            tv_Size.textSize = settingsViewModel.fontSize.toFloat()
        }

        // SeekBar Listener
        fontSizeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val newValue = progress.coerceIn(12, 40)
                tvSeekBarValue.text = newValue.toString()
                settingsViewModel.fontSize = newValue
                tv_Size.textSize = settingsViewModel.fontSize.toFloat()

                // Save values to SharedPreferences

                with(sharedPref.edit()) {
                    putInt("font_size", newValue)
                    putInt("tvSeekBarValue", newValue)
                    apply()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Spinner initialization
        val aFont = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, font)
        aFont.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spFont.adapter = aFont

        spFont.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                saveFontSettings(position)
                val selectedFont = when (position) {
                    0 -> font1
                    1 -> font2
                    2 -> font3
                    3 -> font4
                    4 -> font5
                    5 -> font6
                    6 -> font7
                    else -> font1
                }
                tv_Type.typeface = selectedFont
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }


        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        // SeekBar and TextView initialization

        // Get the current font size and night mode state from SharedPreferences
        fontSizeSeekBar.progress = fontSize.coerceIn(12, 40)

        // Set the font size and typeface for TextView
        tvSeekBarValue.text = fontSizetvSeekBarValue.toString()
        settingsViewModel.fontSize = fontSizetvSeekBarValue
        tv_Size.textSize = settingsViewModel.fontSize.toFloat()

        nightModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                // حفظ حالة الوضع الليلي في SharedPref
                sh_pref.saveThemeStatePref(true)
                saveFontSettings(settingsViewModel.fontSize)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                // حفظ حالة الوضع الليلي في SharedPref
                sh_pref.saveThemeStatePref(false)
                saveFontSettings(settingsViewModel.fontSize)
            }
        }

        notifi.isChecked = sharedPreferences.getBoolean("notification_enabled", true)
        notifi.setOnCheckedChangeListener { _, isChecked ->
            // حفظ حالة التبديل في SharedPreferences
            sharedPreferences.edit().putBoolean("notification_enabled", isChecked).apply()

            if (isChecked) {
                // إذا تم تفعيل الـ Switch، قم بجدولة إرسال الإشعار يوميًا في الساعة 10 صباحًا
                scheduleNotification()
//                scheduleNotification()
//                scheduleNotification2(10,1,"sss","ss")
//                scheduleNotification2(10,2,"mmm","mmmm")
            } else {
                // إذا تم إيقاف الـ Switch، قم بإلغاء جدولة إرسال الإشعار
                cancelNotification()
            }
        }


        initFonts()
        specifyFont()

        // Restore the default value of Spinner when the screen is opened
        restoreFontSetting()

        return rootview
    }

    // Override the onCreate method
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ViewModel
        if (!::settingsViewModel.isInitialized) {
            settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)
            settingsViewModel.fontSize = 14.coerceIn(12, 30) // Default value for tvSeekBarValue
        }
    }

    // Save font settings to SharedPreferences
    private fun saveFontSettings(selectedFont: Int) {
        val spEditor = PreferenceManager.getDefaultSharedPreferences(requireContext()).edit()
        spEditor.putInt("font", selectedFont)
        spEditor.apply()
    }

    // Restore the default value of Spinner when the screen is opened
    private fun restoreFontSetting() {
        val sp = PreferenceManager.getDefaultSharedPreferences(requireContext())
        spFont.setSelection(sp.getInt("font", 0))
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


        val editor = sp.edit()
        editor.putInt("font", fontIndex)
        editor.apply()
    }





    private fun scheduleNotification() {
        // الحصول على مرجع لمدير المنبهات
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // إنشاء Intent لاستدعاء BroadcastReceiver عند وقت الإشعار للصباح
        val amAlarmIntent = Intent(requireContext(), AlarmReceiverAM::class.java)
        val amPendingIntent = PendingIntent.getBroadcast(requireContext(), 0, amAlarmIntent, PendingIntent.FLAG_IMMUTABLE)

        // إنشاء Intent لاستدعاء BroadcastReceiver عند وقت الإشعار للمساء
        val pmAlarmIntent = Intent(requireContext(), AlarmReceiverPM::class.java)
        val pmPendingIntent = PendingIntent.getBroadcast(requireContext(), 0, pmAlarmIntent, PendingIntent.FLAG_IMMUTABLE)

        // تحديد وقت الإشعار اليومي عند الساعة 8 صباحًا للصباح
        val amCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 1)
            set(Calendar.MILLISECOND, 1)
            if (System.currentTimeMillis() >= timeInMillis) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        // تحديد وقت الإشعار اليومي عند الساعة المناسبة للمساء
        val pmCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 20/* وقت المساء */)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 1)
            set(Calendar.MILLISECOND, 1)
            if (System.currentTimeMillis() >= timeInMillis) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        // جدولة استدعاء BroadcastReceiver عند الساعة 8 صباحًا يوميًا
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            amCalendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            amPendingIntent
        )

        // جدولة استدعاء BroadcastReceiver عند الساعة المناسبة للمساء يوميًا
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            pmCalendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pmPendingIntent
        )

        Log.d("MainActivity", "تم جدولة الإشعار عند الساعة 8 صباحًا ووقت المساء يوميًا.")
    }



    private fun cancelNotification() {
        // الحصول على مرجع لمدير المنبهات
        val alarmManager =requireContext(). getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // إنشاء Intent لاستدعاء BroadcastReceiver عند وقت الإشعار للصباح
        val amAlarmIntent = Intent(requireContext(), AlarmReceiverAM::class.java)
        val amPendingIntent = PendingIntent.getBroadcast(requireContext(), 0, amAlarmIntent, PendingIntent.FLAG_IMMUTABLE)

        // إنشاء Intent لاستدعاء BroadcastReceiver عند وقت الإشعار للمساء
        val pmAlarmIntent = Intent(requireContext(), AlarmReceiverPM::class.java)
        val pmPendingIntent = PendingIntent.getBroadcast(requireContext(), 0, pmAlarmIntent, PendingIntent.FLAG_IMMUTABLE)

        // إلغاء جدولة استدعاء BroadcastReceiver للصباح
        alarmManager.cancel(amPendingIntent)

        // إلغاء جدولة استدعاء BroadcastReceiver للمساء
        alarmManager.cancel(pmPendingIntent)

        Log.d("MainActivity", "تم إلغاء جدولة الإشعارين.")
    }




    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.VIBRATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // لم يتم منح إذن الإشعارات، نقوم بطلبه
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.VIBRATE),
                NOTIFICATION_PERMISSION_CODE
            )
        } else {
            // تم منح إذن الإشعارات مسبقًا، نقوم بتفعيل الإشعارات
            sharedPreferences.edit().putBoolean("notification_enabled", true).apply()
            scheduleNotification()
            scheduleNotification()
//            scheduleNotification2(10,1,"sss","ss")
//            scheduleNotification2(10,2,"mmm","mmmm")
        }
        Log.d("MainActivity", "تم طلب إذن الإشعارات.")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // تم منح إذن الإشعارات، نقوم بتفعيل الإشعارات
                sharedPreferences.edit().putBoolean("notification_enabled", true).apply()
                scheduleNotification()
                scheduleNotification()
            } else {
                // تم رفض إذن الإشعارات، نقوم بإلغاء جدولة الإشعار
                notifi.isChecked = false
                sharedPreferences.edit().putBoolean("notification_enabled", false).apply()
                cancelNotification()
            }
        }
        Log.d("MainActivity", "تم معالجة نتيجة طلب إذن الإشعارات.")
    }
}

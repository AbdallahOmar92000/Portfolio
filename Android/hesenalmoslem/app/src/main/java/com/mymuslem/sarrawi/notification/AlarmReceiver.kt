package com.mymuslem.sarrawi.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiverAM : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // قم بإضافة الكود الخاص بإرسال الإشعار هنا
        NotificationUtils.showNotification(
            context,
            "حصن نفسك",
            "أذكار الاستيقاظ من النوم"
        )
    }
}
class AlarmReceiverPM: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // قم بإضافة الكود الخاص بإرسال الإشعار هنا
        NotificationUtils.showNotification(
            context,
            "حصن نفسك",
            "أذكار النوم"
        )
    }
}

/*
*   override fun onReceive(context: Context, intent: Intent) {
        val notificationTitle = intent.getStringExtra("title")
        val notificationText = intent.getStringExtra("text")

        if (notificationTitle != null && notificationText != null) {
            // عرض الإشعار باستخدام بيانات العنوان والنص
            showNotification(context, notificationTitle, notificationText)
        }
    }

    private fun showNotification(context: Context, title: String, text: String) {
        // قم بعرض الإشعار باستخدام بيانات العنوان والنص
        NotificationUtils.showNotification(context, title, text)
    }
}*/
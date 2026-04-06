package com.mymuslem.sarrawi.utils
import android.content.Context
import android.content.Intent
import android.util.Log

class ShareText {




        companion object {
            fun shareText(con: Context, dialogHeader: String, header: String, zekers: String,) {

                try {
                    val sharingIntent = Intent(Intent.ACTION_SEND)
                    sharingIntent.type = "text/plain"
                    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, header)
                    sharingIntent.putExtra(Intent.EXTRA_TEXT, zekers)

                    val chooserIntent = Intent.createChooser(sharingIntent, dialogHeader)
                    con.startActivity(chooserIntent)

                } catch (e: Exception) {
                    Log.d("Share Error", e.toString())
                }
            }
        }


}
//IntentUtils.shareText(this, "Choose an app to share", "Header", "Message to share")
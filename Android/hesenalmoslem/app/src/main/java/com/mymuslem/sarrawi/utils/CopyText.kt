package com.mymuslem.sarrawi.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.mymuslem.sarrawi.R


class CopyText {
    companion object {
        fun copy(con: Context, text: String) {
            val clipboardManager = ContextCompat.getSystemService(con, ClipboardManager::class.java)

            clipboardManager?.let {
                val clip = ClipData.newPlainText("copied_text", text)
                it.setPrimaryClip(clip)
                Toast.makeText(con, "تم النسخ", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
//CopyText.copy(this, binding.tvMsgM)
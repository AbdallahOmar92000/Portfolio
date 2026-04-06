package com.sarrawi.mysocialnetwork.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sarrawi.mysocialnetwork.R
import com.sarrawi.mysocialnetwork.databinding.ItemNotificationBinding
import com.sarrawi.mysocialnetwork.model.notif.NotificationResponse

class NotificationAdapter2 : RecyclerView.Adapter<NotificationAdapter2.ViewHolder>() {

    private var notifications: List<NotificationResponse> = emptyList()
    private var onDeleteClickListener: ((NotificationResponse) -> Unit)? = null

    // تعيين المستمع عند الضغط على زر الحذف
    fun setOnDeleteClickListener(listener: (NotificationResponse) -> Unit) {
        onDeleteClickListener = listener
    }

    // تحديث البيانات يدويًا
    fun setNotifications(newList: List<NotificationResponse>) {
        notifications = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: NotificationResponse) {
            Log.d("NotifAdapter", "Binding notification: ${notification.message} at position $adapterPosition")

            // تحميل صورة المرسل
            Glide.with(binding.root.context)
                .load(notification.from_user.profile_picture)
                .placeholder(R.drawable.ic_launcher_background)
                .circleCrop()
                .into(binding.imgFromUser)

            // نص الإشعار
            binding.tvMessage.text = notification.message ?: "Notification"

            // الوقت
            binding.tvDate.text = notification.date?.substring(0, 16) ?: ""

            // حدث الضغط على زر الحذف
            binding.btnDeleteNotification.setOnClickListener {
                onDeleteClickListener?.invoke(notification)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notifications[position])
    }

    override fun getItemCount(): Int = notifications.size
}

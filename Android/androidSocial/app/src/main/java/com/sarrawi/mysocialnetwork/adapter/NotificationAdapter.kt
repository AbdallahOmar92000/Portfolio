package com.sarrawi.mysocialnetwork.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sarrawi.mysocialnetwork.R
import com.sarrawi.mysocialnetwork.databinding.ItemNotificationBinding
import com.sarrawi.mysocialnetwork.model.PostResponse
import com.sarrawi.mysocialnetwork.model.notif.NotificationResponse
import android.view.View

import android.view.animation.AnimationUtils


class NotificationAdapter : ListAdapter<NotificationResponse, NotificationAdapter.ViewHolder>(DiffCallback()) {

    private var onDeleteClickListener: ((NotificationResponse) -> Unit)? = null

    fun setOnDeleteClickListener(listener: (NotificationResponse) -> Unit) {
        onDeleteClickListener = listener
    }

    inner class ViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: NotificationResponse) {
            // صورة المرسل
            Glide.with(binding.root.context)
                .load(notification.from_user.profile_picture)
                .placeholder(R.drawable.ic_launcher_background)
                .circleCrop()
                .into(binding.imgFromUser)

            // نص الإشعار
            binding.tvMessage.text = notification.message ?: "Notification"

            // التاريخ
            binding.tvDate.text = notification.date?.substring(0, 16) ?: ""

            // عند الضغط على زر الحذف
            binding.btnDeleteNotification.setOnClickListener {
                //animateDeletion(binding.root) {
                    onDeleteClickListener?.invoke(notification)
              //  }
            }
        }

//        private fun animateDeletion(view: View, onAnimationEnd: () -> Unit) {
//            val anim = AnimationUtils.loadAnimation(view.context, R.anim.fade_out_slide_left)
//            anim.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
//                override fun onAnimationStart(animation: android.view.animation.Animation?) {}
//                override fun onAnimationEnd(animation: android.view.animation.Animation?) {
//                    onAnimationEnd()
//                }
//                override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
//            })
//            view.startAnimation(anim)
//        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<NotificationResponse>() {
        override fun areItemsTheSame(oldItem: NotificationResponse, newItem: NotificationResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NotificationResponse, newItem: NotificationResponse): Boolean {
            return oldItem == newItem
        }
    }
}


class NotificationAdapter22 : ListAdapter<NotificationResponse, NotificationAdapter22.ViewHolder>(DiffCallback()) {

    private var onDeleteClickListener: ((NotificationResponse) -> Unit)? = null

    fun setOnDeleteClickListener(listener: (NotificationResponse) -> Unit) {
        onDeleteClickListener = listener
    }

    inner class ViewHolder(private val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: NotificationResponse) {
            Log.d("NotifAdapter", "Binding notification: ${notification.message} at position $adapterPosition")

            // صورة المرسل
            Glide.with(binding.root.context)
                .load(notification.from_user.profile_picture)
                .placeholder(R.drawable.ic_launcher_background)
                .circleCrop()
                .into(binding.imgFromUser)

            // نص الإشعار
            binding.tvMessage.text = notification.message ?: "Notification"

            // الوقت
            binding.tvDate.text = notification.date?.substring(0, 16) ?: ""

            binding.btnDeleteNotification.setOnClickListener {
                onDeleteClickListener?.invoke(notification)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<NotificationResponse>() {
        override fun areItemsTheSame(oldItem: NotificationResponse, newItem: NotificationResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NotificationResponse, newItem: NotificationResponse): Boolean {
            return oldItem.message == newItem.message &&
                    oldItem.date == newItem.date &&
                    oldItem.from_user == newItem.from_user
        }
    }
}


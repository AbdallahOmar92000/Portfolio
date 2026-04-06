package com.sarrawi.mysocialnetwork.adapter

// UsersAdapter.kt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sarrawi.mysocialnetwork.R
import com.sarrawi.mysocialnetwork.model.Post2
import com.sarrawi.mysocialnetwork.model.User


class UsersAdapter : ListAdapter<User, UsersAdapter.UserViewHolder>(DiffCallback()) {
    private var onEmailClickListener: ((User) -> Unit)? = null

    fun setOnEmail(listener: (User) -> Unit){
        onEmailClickListener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val first_name = itemView.findViewById<TextView>(R.id.first_name)
        private val last_name = itemView.findViewById<TextView>(R.id.last_name)
        private val emailText = itemView.findViewById<TextView>(R.id.userEmail)
        private val userImage = itemView.findViewById<ImageView>(R.id.userImage)
        fun bind(user: User) {
            first_name.text = user.first_name
            last_name.text = user.last_name
            emailText.text = user.email
            // تحميل الصورة باستخدام Glide
            emailText.setOnClickListener {
                onEmailClickListener?.invoke(user)
            }
            Glide.with(itemView.context)
                .load(user.picture)
                .placeholder(R.drawable.ic_user)
                .into(userImage)
        }
        }


    class DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User) = oldItem.email == newItem.email
        override fun areContentsTheSame(oldItem: User, newItem: User) = oldItem == newItem
    }
}

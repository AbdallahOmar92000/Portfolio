package com.sarrawi.mysocialnetwork.adapter

// SuggestionAdapter.kt
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
import com.sarrawi.mysocialnetwork.model.PostResponse
import com.sarrawi.mysocialnetwork.model.SuggestionResponse
import com.sarrawi.mysocialnetwork.model.User

class SuggestionsAdapter() : ListAdapter<SuggestionResponse, SuggestionsAdapter.UserViewHolder>(DiffCallback()) {
    private var onEmailClickListener: ((SuggestionResponse) -> Unit)? = null

    fun setOnEmail(listener: (SuggestionResponse) -> Unit){
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
        fun bind(user: SuggestionResponse) {
            first_name.text = user.first_name
            last_name.text = user.last_name
            emailText.text = user.email

            emailText.setOnClickListener {
                onEmailClickListener?.invoke(user) // تمرير الـ user عند النقر
            }
            // تحميل الصورة باستخدام Glide
            Glide.with(itemView.context)
                .load(user.profile_pic)
                .placeholder(R.drawable.ic_user)
                .into(userImage)

            
        }
    }


    class DiffCallback : DiffUtil.ItemCallback<SuggestionResponse>() {
        override fun areItemsTheSame(oldItem: SuggestionResponse, newItem: SuggestionResponse) = oldItem.email == newItem.email
        override fun areContentsTheSame(oldItem: SuggestionResponse, newItem: SuggestionResponse) = oldItem == newItem
    }
}

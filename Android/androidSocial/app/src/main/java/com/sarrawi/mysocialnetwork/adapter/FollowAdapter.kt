package com.sarrawi.mysocialnetwork.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sarrawi.mysocialnetwork.R
import com.sarrawi.mysocialnetwork.databinding.ItemUserBinding
import com.sarrawi.mysocialnetwork.model.UserItem

class FollowAdapter : RecyclerView.Adapter<FollowAdapter.ViewHolder>() {

    private var followersList: List<UserItem> = emptyList()
    private var followingList: List<UserItem> = emptyList()
    private var showingFollowers = true

    fun submitFollowers(list: List<UserItem>) {
        followersList = list
        showingFollowers = true
        notifyDataSetChanged()
    }

    fun submitFollowing(list: List<UserItem>) {
        followingList = list
        showingFollowers = false
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return if (showingFollowers) followersList.size else followingList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = if (showingFollowers) followersList[position] else followingList[position]
        holder.bind(item)
    }

    inner class ViewHolder(private val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserItem) {
            binding.firstName.text = user.first_name
            binding.lastName.text = user.last_name
            binding.userEmail.text = user.email

            Glide.with(binding.root.context)
                .load(user.profile_picture)
                .placeholder(R.drawable.ic_user) // أي placeholder مناسب
                .circleCrop()
                .into(binding.userImage)
        }
    }
}

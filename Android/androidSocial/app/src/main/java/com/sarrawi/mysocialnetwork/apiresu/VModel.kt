package com.sarrawi.mysocialnetwork.apiresu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sarrawi.mysocialnetwork.model.ExploreResponse2
import com.sarrawi.mysocialnetwork.model.PostResponse
import com.sarrawi.mysocialnetwork.model.ProfileModel.Profile
import com.sarrawi.mysocialnetwork.model.ProfileModel.ProfileResponse
import com.sarrawi.mysocialnetwork.model.ProfileModel.UserDetails
import com.sarrawi.mysocialnetwork.model.ProfileModel.UserInfo
import com.sarrawi.mysocialnetwork.model.SuggestionResponse
import com.sarrawi.mysocialnetwork.model.UserSearchResponse
import com.sarrawi.mysocialnetwork.model.notif.NotificationResponse
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class VModel(private val repo: SocialRepository1) : ViewModel() {

        val profile = MutableLiveData<ApiResult<ProfileResponse>>()
        val posts = MutableLiveData<ApiResult<List<PostResponse>>>()
        val suggestions = MutableLiveData<ApiResult<List<SuggestionResponse>>>()
        val searchusers = MutableLiveData<ApiResult<UserSearchResponse>>()
        val explorePosts = MutableLiveData<ApiResult<ExploreResponse2>>()
        val notifications = MutableLiveData<ApiResult<List<NotificationResponse>>>()

        // PROFILE
        fun loadProfile(token: String) {
            viewModelScope.launch {
                profile.value = repo.getProfile(token)
            }
        }

        fun loadProfileWithId(id: Int, token: String) {
            viewModelScope.launch {
                profile.value = repo.getProfileWithId(id, token)
            }
        }

        // POSTS
        fun loadPosts(token: String) {
            viewModelScope.launch {
                posts.value = repo.getPosts(token)
            }
        }

        fun createPost(token: String, body: RequestBody, sharedBody: RequestBody?, images: List<MultipartBody.Part>?) {
            viewModelScope.launch {
                val result = repo.createPost(token, body, sharedBody, images)
                // بعد الإنشاء يمكنك تحديث _posts أو أي LiveData حسب الحاجة
            }
        }

        fun likePost(token: String, postId: Int) {
            viewModelScope.launch {
                repo.likePost(token, postId)
            }
        }

        fun dislikePost(token: String, postId: Int) {
            viewModelScope.launch {
                repo.dislikePost(token, postId)
            }
        }

        fun sharePost(token: String, postId: Int, body: Map<String,String>) {
            viewModelScope.launch {
                repo.sharePost(token, postId, body)
            }
        }

        // USERS
        fun searchUsers(token: String, query: String) {
            viewModelScope.launch {
                searchusers.value = repo.searchUsers(token, query)
            }
        }


    fun getSuggestions(token: String) {
            viewModelScope.launch {
                suggestions.value = repo.getSuggestions(token)
            }
        }

        // EXPLORE
        fun getExplorePosts(token: String) {
            viewModelScope.launch {
                explorePosts.value = repo.getExplorePosts(token)
            }
        }

        fun searchTags(token: String, query: String?) {
            viewModelScope.launch {
                explorePosts.value = repo.searchTags(token, query)
            }
        }

        // NOTIFICATIONS
        fun loadNotifications(token: String) {
            viewModelScope.launch {
                notifications.value = repo.getAllNotifications(token)
            }
        }

        fun deleteNotification(notificationPk: Int) {
            viewModelScope.launch {
                repo.removeNotification(notificationPk)
            }
        }

        fun deleteNotification2(notificationId: Int, token: String) {
            viewModelScope.launch {
                repo.removeNotification2(notificationId, token)
            }
        }
    }




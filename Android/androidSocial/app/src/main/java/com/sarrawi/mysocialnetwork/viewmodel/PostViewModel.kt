package com.sarrawi.mysocialnetwork.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.annotations.SerializedName
import com.sarrawi.mysocialnetwork.model.*
import com.sarrawi.mysocialnetwork.model.ProfileModel.ProfileResponse
import com.sarrawi.mysocialnetwork.model.ProfileModel.ProfileState
import com.sarrawi.mysocialnetwork.model.notif.NotificationResponse
import com.sarrawi.mysocialnetwork.model.postdetails.Comment
import com.sarrawi.mysocialnetwork.model.postdetails.PostDetails
import com.sarrawi.mysocialnetwork.model.postdetails.Reply
import com.sarrawi.mysocialnetwork.repository.PostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import retrofit2.HttpException
import java.io.File

class PostViewModel(private val repo: PostRepository,val context: Context) : ViewModel() {

    private val _posts = MutableLiveData<List<PostResponse>>()
    val posts: LiveData<List<PostResponse>> = _posts

    private val _postsdet = MutableLiveData<List<PostDetails>>()
    val postsdet: LiveData<List<PostDetails>> = _postsdet

    private val _posts2 = MutableLiveData<List<Post2>>()
    val posts2: LiveData<List<Post2>> = _posts2


    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    private val _likesCount = MutableLiveData<Int>()
    val likesCount: LiveData<Int> = _likesCount

    private val _dislikesCount = MutableLiveData<Int>()
    val dislikesCount: LiveData<Int> = _dislikesCount

    private val _isLiked = MutableLiveData<Boolean>()
    val isLiked: LiveData<Boolean> = _isLiked

    private val _isDisliked = MutableLiveData<Boolean>()
    val isDisliked: LiveData<Boolean> = _isDisliked


    private val _sharedPost = MutableLiveData<PostResponse?>()
    val sharedPost: LiveData<PostResponse?> = _sharedPost

    private val _searchResults = MutableLiveData<UserSearchResponse?>()
    val searchResults: LiveData<UserSearchResponse?> = _searchResults

    private val _suggestions = MutableLiveData<List<SuggestionResponse>>()
    val suggestions: LiveData<List<SuggestionResponse>> = _suggestions

    private val _tagsState = MutableLiveData<List<Tag>>()
    val tagsState: LiveData<List<Tag>> = _tagsState

    private val _notifications = MutableLiveData<List<NotificationResponse>>()
    val notifications: LiveData<List<NotificationResponse>> = _notifications



    private val _profile =MutableLiveData<ProfileResponse>()
    val profile: LiveData<ProfileResponse> get() = _profile


    private val _profileState_w_id = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val profileState_w_id: StateFlow<ProfileState> = _profileState_w_id


    private val _errorState = MutableLiveData<String>()
    val errorState: LiveData<String> = _errorState
    private val _tagsState2 = MutableLiveData<List<Tag>>()
    val tagsState2: LiveData<List<Tag>> = _tagsState2

    private val _removeStatus = MutableLiveData<String>()
    val removeStatus: LiveData<String> get() = _removeStatus

    private val _followStatus = MutableLiveData<FollowingResponse>()
    val followStatus: LiveData<FollowingResponse> = _followStatus

    private val _currentUserId = MutableLiveData<Int>()
    val currentUserId: LiveData<Int> get() = _currentUserId

    private val _followers = MutableLiveData<List<UserItem>>()
    val followers: LiveData<List<UserItem>> = _followers

    private val _following = MutableLiveData<List<UserItem>>()
    val following: LiveData<List<UserItem>> = _following

    private val _postDetails = MutableLiveData<PostDetails>()
    val postDetails: LiveData<PostDetails> = _postDetails

    private val _comm_like = MutableLiveData<Comment?>()
    val comm_like: LiveData<Comment?> = _comm_like

    private val _discomm_like = MutableLiveData<Comment?>()
    val discomm_like: LiveData<Comment?> = _discomm_like


    fun loadTags(token: String, query: String? = null) {
        viewModelScope.launch {
            try {
                val tokenHeader = "Token $token"
                val tags = repo.getTags(tokenHeader, query)
                _tagsState.value = tags
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Error loading tags"
            }
        }}



    fun loadTags2(token: String) {
        viewModelScope.launch {
            try {
                val tokenHeader = "Token $token"
                val response = repo.getTags2(tokenHeader) // يجيب ExploreResponse
                val tagsList: List<Tag> = response.posts
                    .flatMap { it.tags }
                    .distinctBy { it.id } // إزالة التكرار
                    .filter { it.name.isNotEmpty() }
                _tagsState2.value = tagsList
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Error loading tags"
            }
        }
    }


    fun loadPosts(authToken: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val data = repo.fetchPosts(authToken)
                Log.d("apiRES","Postdata$data")
                _posts.value = data
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Error loading posts"
            } finally {
                _loading.value = false
            }
        }
    }

    fun addPost(
        authToken: String,
        body: String,
        sharedBody: String?,
        imageParts: List<MultipartBody.Part>?,
        onSuccess: (PostResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val newPost = repo.addPost(authToken, body, sharedBody, imageParts)
                onSuccess(newPost)
                loadPosts(authToken)
                //fetchExplore(authToken)
                // 3️⃣ تحويل newPost إلى Post2 لتجنب Type mismatch
                val post2 = Post2(
                    id = newPost.id,
                    body = newPost.body ?: "",
                    shared_body = newPost.shared_body,
                    created_on = newPost.created_on,
                    shared_on = null, // إذا لم يكن لديك، ضع null
                    author = Author2(
                        id = newPost.author.id,
                        email = newPost.author.email,
                        first_name = newPost.author.first_name,
                        last_name  = newPost.author.last_name,
                    ),
                    author_image = newPost.author_image,
                    shared_user = null, // إذا لم يكن لديك مشاركة، ضع null
                    image = newPost.image,
                    tags = null, // أو newPost.tags إذا متوفرة
                    likes_count = newPost.likes_count,
                    dislikes_count = newPost.dislikes_count,
                    is_liked_by_user = newPost.isLikedByUser,
                    is_disliked_by_user = newPost.isDislikedByUser
                )



                // 4️⃣ تحديث قائمة Explore مباشرة بدون استدعاء API جديد
                _exploreData.value = _exploreData.value?.copy(
                    posts = listOf(post2) + _exploreData.value!!.posts
                )

            } catch (e: Exception) {
                onError(e.localizedMessage ?: "خطأ في إضافة المنشور")
            } finally {
                _loading.value = false
            }
        }
    }








    private fun updatePostInListDet(updatedPost: PostDetails) {
        val currentList = _postsdet.value?.toMutableList() ?: return
        val index = currentList.indexOfFirst { it.id == updatedPost.id }
        if (index != -1) {
            currentList[index] = updatedPost
            _postsdet.value = currentList
        }
    }


    private fun updatePostInList(updatedPost: PostResponse) {
        val currentList = _posts.value?.toMutableList() ?: return
        val index = currentList.indexOfFirst { it.id == updatedPost.id }
        if (index != -1) {
            currentList[index] = updatedPost
            _posts.value = currentList
        }
    }

    private fun updatePostInList2(updatedPost2: Post2) {
        val currentList = _posts2.value?.toMutableList() ?: return
        val index = currentList.indexOfFirst { it.id == updatedPost2.id }
        if (index != -1) {
            currentList[index] = updatedPost2
            _posts2.value = currentList
        }
    }

    // 1. تغيير البارامتر إلى Int


    fun likePost(token: String, post: PostResponse) {
        viewModelScope.launch {
            try {
                val response = repo.likePost(token, post)
                val updatedPost = post.copy(
                    likes_count = response.post.likes_count,
                    dislikes_count = response.post.dislikes_count,
                    isLikedByUser = response.liked ?: true,
                    isDislikedByUser = false
                )
                updatePostInList(updatedPost)
                fetchNotifications(token)
            } catch (e: Exception) { /* handle error */ }
        }
    }

    fun likePost2(token: String, post: Post2) {
        viewModelScope.launch {
            try {
                val response = repo.likePost2(token, post)
                val updatedPost = post.copy(
                    likes_count = response.post.likes_count,
                    dislikes_count = response.post.dislikes_count,
                    is_liked_by_user = response.liked ?: true,
                    is_disliked_by_user = false
                )
                updatePostInList2(updatedPost)
                fetchNotifications(token)
            } catch (e: Exception) { /* handle error */ }
        }
    }

    fun dislikePost(token: String, post: PostResponse) {
        viewModelScope.launch {
            try {
                val response = repo.dislikePost(token, post)
                val updatedPost = post.copy(
                    likes_count = response.post.likes_count,
                    dislikes_count = response.post.dislikes_count,
                    isLikedByUser = false,
                    isDislikedByUser = response.disliked ?: true
                )
                updatePostInList(updatedPost)
            } catch (e: Exception) { /* handle error */ }
        }
    }

    fun dislikePost2(token: String, post: Post2) {
        viewModelScope.launch {
            try {
                val response = repo.dislikePost2(token, post)
                val updatedPost = post.copy(
                    likes_count = response.post.likes_count,
                    dislikes_count = response.post.dislikes_count,
                    is_liked_by_user = false,
                    is_disliked_by_user = response.disliked ?: true
                )
                updatePostInList2(updatedPost)
            } catch (e: Exception) { /* handle error */ }
        }
    }


//    fun sharePost(postId: Int, body: String) {
//        viewModelScope.launch {
//            val response = repo.sharePost(postId, body)
//            if (response.isSuccessful) {
//                _sharedPost.value = response.body()
//            } else {
//                _sharedPost.value = null // أو حط error handling
//            }
//        }
//    }



    fun sharePost(token: String,postId: Int, body: String) {
        viewModelScope.launch {
            try {
                val response = repo.sharePost(token,postId, body)
                _sharedPost.value = response
                Log.d("PostViewModel", "API Response: $response")

            }
            catch (e: Exception) {
                Log.e("PostViewModel", "Error sharing post", e)
                _sharedPost.value = null
            }

        }
    }

    fun searchUsers(token: String,query: String) {
        viewModelScope.launch {
            try {
                val response = repo.searchUsers(token,query)
                Log.d("PostViewModel", "API Response: $response")
                _searchResults.value = response
            } catch (e: HttpException) {
                Log.e("PostViewModel", "HTTP Exception: ${e.code()} ${e.message()}")
                _searchResults.value = UserSearchResponse(emptyList())
            } catch (e: Exception) {
                Log.e("PostViewModel", "Search failed", e)
                _searchResults.value = UserSearchResponse(emptyList())
            }
        }
    }

    fun getSuggestions(token: String) {
        viewModelScope.launch {
            try {
                val tokenHeader = "Token $token"
                val response: List<SuggestionResponse> = repo.getSuggestions(tokenHeader)
                Log.d("PostViewModel", "API Response: ${response.size} suggestions")
                Log.d("PostViewModel", "API Response: $response")

                _suggestions.value = response
            } catch (e: Exception) {
                _error.value = e.localizedMessage ?: "Error loading suggestions"
                Log.e("PostViewModel", "Error fetching suggestions", e)
            }
        }
    }


///////////////////
private val _exploreData = MutableLiveData<ExploreResponse2>()
    val exploreData: LiveData<ExploreResponse2> get() = _exploreData


    private fun updateExplorePost(updatedPost2: Post2) {
        val currentExplore = _exploreData.value ?: return
        val updatedPosts = currentExplore.posts.toMutableList()
        val index = updatedPosts.indexOfFirst { it.id == updatedPost2.id }

        if (index != -1) {
            updatedPosts[index] = updatedPost2
            _exploreData.value = currentExplore.copy(posts = updatedPosts.toList()) // ✅ تحديث مباشر
        }
    }


    fun likeexp2(token: String, post: Post2) {
        viewModelScope.launch {
            try {
                val response = repo.likePost2(token, post)
                val updatedPost = post.copy(
                    likes_count = response.post.likes_count,
                    dislikes_count = response.post.dislikes_count,
                    is_liked_by_user = response.liked ?: true,
                    is_disliked_by_user = false
                )
                updateExplorePost(updatedPost)
                fetchNotifications(token)
            } catch (e: Exception) { /* handle error */ }
        }
    }

    fun dislikeexp2(token: String, post: Post2) {
        viewModelScope.launch {
            try {
                val response = repo.dislikePost2(token, post)
                val updatedPost = post.copy(
                    likes_count = response.post.likes_count,
                    dislikes_count = response.post.dislikes_count,
                    is_liked_by_user = false,
                    is_disliked_by_user = response.disliked ?: true
                )
                updateExplorePost(updatedPost)
            } catch (e: Exception) { /* handle error */ }
        }
    }


    fun fetchExplore(token: String) {
        try {
            viewModelScope.launch {
                val tokenHeader = "Token $token"
                val data = repo.getExplore(tokenHeader) // بدون query
                data?.let {
                    val updated = it.copy(
                        posts = it.posts.map { post -> post.copy() },
                        tags = it.tags.map { tag -> tag.copy() }
                    )
//                    _exploreData.postValue(it)
                    _exploreData.postValue(updated)
                }
//                Log.d("PostViewModel", "API Response: ${data!!.posts.size} aa")
//                Log.d("PostViewModel", "API Response: ${data.tags.size} aa")
//                Log.d("PostViewModel", "API Response: $data")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("PostViewModel", "Error fetching tags", e)
        }
    }




    fun searchExplore(token: String, query: String) {
        try {
            viewModelScope.launch {
                val tokenHeader = "Token $token"
                val data = repo.searchExplore(tokenHeader,query) // مع query
                data?.let { _exploreData.postValue(it) }
                if(data!=null) {
                    Log.d("PostViewModel", "API Response: ${data.posts?.size?:0} aa")
                    Log.d("PostViewModel", "API Response: ${data.tags?.size?:0} aa")
                    Log.d("PostViewModel", "API Response: $data")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("PostViewModel", "Error fetching tags", e)

        }
    }

    fun fetchNotifications2(token: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val result = repo.getNotifications(token)
                _notifications.value = result
                Log.d("notifff", "API Response: $result")


            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("notifff", "Error fetching notiff", e)
            } finally {
                _loading.value = false
            }
        }
    }
    fun fetchNotifications(token: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = repo.getNotifications(token)
                val list = result ?: emptyList()
                _notifications.value = list.toList() // نسخة جديدة لتفعيل DiffUtil
                Log.d("notifff", "API Response: $list")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("notifff", "Error fetching notifications", e)
                _notifications.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }







    fun removeNotification(notificationId: Int, token: String) {
        viewModelScope.launch {
            val result = repo.removeNotification2(notificationId, token)
            result.onSuccess { response ->
                _removeStatus.value = response.message ?: response.error ?: "تم حذف الإشعار ✅"

                val currentList = _notifications.value?.toMutableList() ?: mutableListOf()
                val updatedList = currentList.filter { it.id != notificationId }.map { it.copy() } // نسخة جديدة
                _notifications.value = updatedList // هذا سيحدث RecyclerView مباشرة

            }.onFailure { exception ->
                _removeStatus.value = "حدث خطأ: ${exception.message}"
            }
        }
    }

    fun removeNotification3(notificationId: Int, token: String) {
        viewModelScope.launch {
            val result = repo.removeNotification2(notificationId, token)
            result.onSuccess { response ->
                // تحديث حالة الحذف
                _removeStatus.value = response.message ?: response.error ?: "تم حذف الإشعار ✅"

                // إزالة الإشعار من القائمة مباشرة
                val currentList = _notifications.value?.toMutableList() ?: mutableListOf()
                val updatedList = currentList.filter { it.id != notificationId }
                _notifications.value = updatedList
            }.onFailure { exception ->
                _removeStatus.value = "حدث خطأ: ${exception.message}"
            }
        }
    }

    fun removeNotification2(notificationId: Int, token: String) {
        viewModelScope.launch {
            val result = repo.removeNotification2(notificationId, token)
            result.onSuccess { response ->
                // تحديث حالة الحذف
                _removeStatus.value = response.message ?: response.error ?: "تم حذف الإشعار ✅"

                // إزالة الإشعار من القائمة مباشرة مع إنشاء نسخة جديدة لكل عنصر
                val currentList = _notifications.value?.toMutableList() ?: mutableListOf()
                val updatedList = currentList
                    .filter { it.id != notificationId }
                    .map { it.copy() } // نسخة جديدة لضمان تحديث DiffUtil
                _notifications.value = updatedList
            }.onFailure { exception ->
                _removeStatus.value = "حدث خطأ: ${exception.message}"
            }
        }
    }







    fun loadProfile(token: String) {
        viewModelScope.launch {
            try {
                val response = repo.getProfile(token) // Response<ProfileResponse>
                if (response.isSuccessful) {
                    response.body()?.let {
                        _profile.postValue(it)  // تحديث LiveData
                        _currentUserId.postValue(it.user.id) // هنا نخزن ID المستخدم

                    } ?: run {
                        _error.postValue("Empty response body")
                    }
                } else {
                    _error.postValue("Error code: ${response.code()}")
                }
            } catch (e: Exception) {
                _error.postValue(e.message ?: "Unknown error")
            }
        }
    }



    // دالة لتحميل بروفايل المستخدم
    fun loadUserProfile1(userId: Int,token: String) {
        viewModelScope.launch {
            _profileState_w_id.value = ProfileState.Loading
            val result = repo.getUserProfile(userId,token)
            _profileState_w_id.value = result.fold(
                onSuccess = { ProfileState.Success(it) },
                onFailure = { ProfileState.Error(it.message ?: "Unknown error") }
            )
        }
    }

    fun loadUserProfile(userId: Int, token: String) {
        viewModelScope.launch {
            _profileState_w_id.value = ProfileState.Loading
            val result = repo.getUserProfile(userId, token)
            result.fold(
                onSuccess = { profileResponse ->
                    // تحديث حالة البروفايل
                    _profileState_w_id.value = ProfileState.Success(profileResponse)

                    // تحديث LiveData حالة follow مباشرة بعد الحصول على البيانات
                    _followStatus.value = FollowingResponse(is_following = profileResponse.is_following)
                },
                onFailure = { throwable ->
                    _profileState_w_id.value = ProfileState.Error(throwable.message ?: "Unknown error")
                }
            )
        }
    }





    fun loadFollowStatus(userId: Int, token: String) {
        viewModelScope.launch {
            try {
                val status = repo.getFollowStatus(userId, token)
                _followStatus.postValue(status)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // تبديل حالة المتابعة
    fun toggleFollow1(userId: Int, token: String) {
        viewModelScope.launch {
            try {
                val result = repo.toggleFollow(userId, "Token $token")
                _followStatus.postValue(result) // ← تحديث مباشر للحالة
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // تبديل حالة المتابعة مع تحديث عدد المتابعين في نفس الوقت
    fun toggleFollow(userId: Int, token: String) {
        viewModelScope.launch {
            try {
                val response = repo.toggleFollow(userId, "Token $token")

                // تحديث حالة المتابعة مباشرة
                _followStatus.postValue(response)

                // تحديث عدد المتابعين داخل الحالة الحالية للبروفايل
                val currentState = _profileState_w_id.value
                if (currentState is ProfileState.Success) {
                    val currentProfileResponse = currentState.profile

                    // حساب العدد الجديد بناءً على نتيجة الـ API
                    val newFollowersCount = if (response.is_following) {
                        currentProfileResponse.profile.followers_count + 1
                    } else {
                        (currentProfileResponse.profile.followers_count - 1).coerceAtLeast(0)
                    }

                    // إنشاء نسخة جديدة من البروفايل محدثة بالعدد الجديد
                    val updatedProfileResponse = currentProfileResponse.copy(
                        profile = currentProfileResponse.profile.copy(
                            followers_count = newFollowersCount
                        )
                    )

                    // تحديث الحالة في LiveData حتى يتحدث الـ UI فورًا
                    _profileState_w_id.value = ProfileState.Success(updatedProfileResponse)
                    fetchNotifications(token)
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }





        fun loadFollowers(userId: Int?, token: String) {
            viewModelScope.launch {
                try {
                    val response = if (userId == null) {
                        repo.getCurrentUserFollower(token)
                    } else {
                        repo.getOtherUserFollower(userId, token)
                    }
                    if (response.isSuccessful) {
                        _followers.value = response.body()?.followers ?: emptyList()
                        Log.d("PostViewModel", "API Response: $response")

                    } else {
                        _error.value = "Error: ${response.code()}"
                    }
                } catch (e: Exception) {
                    _error.value = e.message
                }
            }
        }

        fun loadFollowing(userId: Int?, token: String) {
            viewModelScope.launch {
                try {
                    val response = if (userId == null) {
                        repo.getCurrentUserFollowing(token)
                    } else {
                        repo.getOtherUserFollowing(userId, token)
                    }
                    if (response.isSuccessful) {
                        _following.value = response.body()?.following ?: emptyList()
                        Log.d("PostViewModel", "API Response: $response")

                    } else {
                        _error.value = "Error: ${response.code()}"
                    }
                } catch (e: Exception) {
                    _error.value = e.message
                }
            }
        }


    ////////////////////////

//    val replyResult = MutableLiveData<Reply?>()
//
//    fun sendReply(postId: Int, commentId: Int, comment: String) {
//        viewModelScope.launch {
//            try {
//                val newReply = repo.sendReply(postId, commentId, comment)
//                replyResult.postValue(newReply)
//            } catch (e: Exception) {
//                e.printStackTrace()
//                _error.postValue("فشل إرسال الرد")
//            }
//        }
//    }





    fun loadPostDetails(postId: Int, token: String) {
        Log.d("PostViewModel", "DetailloadPostDetails called with postId=$postId, token=$token")

        viewModelScope.launch {
            repo.getPostDetails(postId, token)
                .onSuccess { postDetails ->
                    Log.d("PostViewModel", "DetailPost details loaded successfully: $postDetails")
                    _postDetails.value = postDetails
                }
                .onFailure { throwable ->
                    Log.e("PostViewModel", "DetailFailed to load post details", throwable)
                    _error.value = throwable.message
                }
        }
    }


    fun addComment(postId: Int, body: String, token: String) {
        viewModelScope.launch {
            repo.addComment(postId, body, token).onSuccess { newComment ->
                val current = _postDetails.value
                if (current != null) {
                    val updatedComments = current.comments.toMutableList()
                    updatedComments.add(newComment)
                    _postDetails.value = current.copy(comments = updatedComments)
                }
            }.onFailure {
                _error.value = it.message
            }
        }
    }
    val commentResult = MutableLiveData<Result<Comment>>()

    fun postComment(postId: Int, commentText: String, imageFile: File?, token: String) {
        viewModelScope.launch {
            try {
                val response = repo.postComment(postId, commentText, imageFile,token)
                if (response.isSuccessful) {
                    commentResult.postValue(Result.success(response.body()!!))
                } else {
                    commentResult.postValue(Result.failure(Exception("Error ${response.code()}")))
                }
            } catch (e: Exception) {
                commentResult.postValue(Result.failure(e))
                Log.d("commVM","commVM")
            }
        }
    }


//    fun add2Reply(postId: Int, commentId: Int, body: String, token: String) {
//        viewModelScope.launch {
//            repo.addReply(postId, commentId, body, token)
//                .onSuccess { newReply ->
//                    val current = _postDetails.value ?: return@launch
//
//                    val updatedComments = current.comments.map { comment ->
//                        if (comment.id == commentId) {
//                            val replyAsComment = newReply.toComment()
//
//                            comment.copy(
//                                replies = (comment.replies ?: emptyList()) + replyAsComment
//                            )
//                        } else comment
//                    }
//
//                    _postDetails.value = current.copy(comments = updatedComments)
//                }
//                .onFailure {
//                    _error.value = it.message
//                    Log.d("commVM", it.message ?: "error")
//                }
//        }
//    }

//    fun addReply(postId: Int, parentId: Int, body: String, token: String) {
//        viewModelScope.launch {
//            repo.addReply(postId, parentId, body, token)
//                .onSuccess { newReply ->
//
//                    val current = _postDetails.value ?: return@launch
//
//                    fun addReplyRecursively(comments: List<Comment>): List<Comment> {
//                        return comments.map { comment ->
//                            if (comment.id == parentId) {
//                                // الرد الأب تم إيجاده
//                                val replyAsComment = newReply.toComment()
//                                comment.copy(
//                                    replies = (comment.replies ?: emptyList()) + replyAsComment
//                                )
//                            } else {
//                                comment.copy(
//                                    replies = addReplyRecursively(comment.replies ?: emptyList())
//                                )
//                            }
//                        }
//                    }
//
//                    val updatedComments = addReplyRecursively(current.comments)
//                    _postDetails.value = current.copy(comments = updatedComments)
//                }
//                .onFailure {
//                    _error.value = it.message
//                    Log.d("commVM", it.message ?: "error")
//                }
//        }
//    }

    fun addReply(postId: Int, parentId: Int, body: String, imageFile: File?, token: String) {
        viewModelScope.launch {
            Log.d("ReplyDebug", "VM: Launching coroutine...")
            repo.addReply(postId, parentId, body, imageFile, token)
                .onSuccess { newReply ->
                    Log.d("ReplyDebug", "VM: Processing UI update for parent: $parentId")

                    val currentDetails = _postDetails.value ?: return@launch

                    // تحويل الرد الجديد إلى كائن Comment ليقبله التصميم
                    val newCommentAsReply = newReply.toComment()

                    fun updateComments(comments: List<Comment>): List<Comment> {
                        return comments.map { comment ->
                            if (comment.id == parentId) {
                                // وجدنا الأب! نضيف الرد الجديد لقائمة ردوده
                                val updatedReplies = (comment.replies ?: emptyList()) + newCommentAsReply
                                comment.copy(replies = updatedReplies)
                            } else {
                                // إذا لم يكن هو الأب، نبحث داخل ردوده (تداخل مستمر)
                                val nestedUpdated = updateComments(comment.replies ?: emptyList())
                                comment.copy(replies = nestedUpdated)
                            }
                        }
                    }

                    val newCommentsList = updateComments(currentDetails.comments)

                    // تحديث الحالة لكي يراقبها الـ Fragment
                    _postDetails.value = currentDetails.copy(comments = newCommentsList)
                    Log.d("ReplyDebug", "VM: UI State updated successfully")
                }
        }
    }



    // تحويل Reply إلى Comment مؤقتًا للعرض في شجرة التعليقات



    fun Reply.toComment(): Comment {
        return Comment(
            id = id,
            comment = comment ?: "",
            image = image,
            image_url = image_url,
            author = author,
            created_on = created_on,
            likes_count = likes_count,
            dislikes_count = dislikes_count,
            is_disliked_by_user = is_disliked_by_user,
            is_liked_by_user = is_liked_by_user,
            replies = replies.map { it.toComment() }
        )
    }

    fun likeComment2(postId: Int, commentId: Int, token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repo.likeComment(postId, commentId, token)

            _comm_like.postValue(result)
        }
    }
    fun likeComment(postId: Int, commentId: Int, token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repo.likeComment(postId, commentId, token)
                val currentComment = _comm_like.value

                // نحدث فقط إذا كان التعليق الموجود هو نفس الذي ضغطنا عليه
                if (currentComment?.id == commentId) {
                    if (result != null) {
                        _comm_like.postValue(currentComment.copy(
                            likes_count = result.likes_count,
                            is_liked_by_user = result.is_liked_by_user ?: true, // استخدم نتيجة السيرفر
                            is_disliked_by_user = false
                        ))
                    }
                } else {
                    // في حال كان الـ LiveData فارغاً أو لتعليق آخر، نضع النتيجة الجديدة
                    _comm_like.postValue(result)
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Error liking comment", e)
            }
        }
    }

    fun dislikeComment(postId: Int, commentId: Int, token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = repo.dislikeComment(postId, commentId, token)

                // استخدم نفس الـ LiveData الخاص بالتعليقات (_comm_like)
                val currentComment = _comm_like.value

                if (currentComment?.id == commentId) {
                    if (result != null) {
                        // التحديث الصحيح لحالة الديسلايك
                        val updated = currentComment.copy(
                            likes_count = result.likes_count,
                            dislikes_count = result.dislikes_count, // لا تنسى عداد الديسلايك
                            is_liked_by_user = result.is_liked_by_user ?: false,
                            is_disliked_by_user = result.is_disliked_by_user ?: true // هنا التعديل
                        )
                        _comm_like.postValue(updated)
                    }
                } else {
                    _comm_like.postValue(result)
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Error disliking comment", e)
            }
        }
    }

    fun dislikeComment2(postId: Int, commentId: Int, token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repo.dislikeComment(postId, commentId, token)
            _discomm_like.postValue(result)
        }
    }

    fun likePostDetails(token: String, postId: Int) {
        viewModelScope.launch {
            try {
                // 2. الحصول على الكائن الحالي من الـ LiveData
                val currentPost = _postDetails.value ?: return@launch

                // 3. نرسل الـ ID للمستودع (تأكد أن المستودع يقبل Int أيضاً)
                val response = repo.likePostdet(token,postId)

                // 4. تحديث الكائن الحالي بالقيم الجديدة القادمة من السيرفر
                val updatedPost = currentPost.copy(
                    likes_count = response.post.likes_count,
                    dislikes_count = response.post.dislikes_count,
                    is_liked_by_user = response.liked ?: true,
                    is_disliked_by_user = false
                )

                // 5. تحديث الـ LiveData لتعكس التغييرات على الشاشة فوراً
                _postDetails.value = updatedPost

                fetchNotifications(token)
            } catch (e: Exception) {
                Log.e("Error", "Like failed: ${e.message}")
            }
        }
    }



    fun dislikePostDetails(token: String, postId: Int) {
        viewModelScope.launch {
            try {
                // 1. استدعاء المستودع بالـ ID
                val response = repo.dislikePostdet(token, postId)

                // 2. الحصول على الحالة الحالية للبوست من الـ LiveData
                val currentPost = _postDetails.value

                // 3. التحقق من أننا نحدث البوست الصحيح
                if (currentPost != null && currentPost.id == postId) {
                    // ملاحظة: إذا ظهر خطأ في .post احذفها واستخدم response.likes_count مباشرة
                    val updatedPost = currentPost.copy(
                        likes_count = response.post.likes_count,
                        dislikes_count = response.post.dislikes_count,
                        is_liked_by_user = false,
                        is_disliked_by_user = response.disliked ?: true
                    )

                    // 4. تحديث الـ LiveData لعكس التغيير في الواجهة
                    _postDetails.postValue(updatedPost)

                    // إذا كنت تملك دالة لتحديث القائمة الرئيسية استدعها هنا
                    // updatePostInListDet(updatedPost)
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Dislike Error: ${e.message}")
            }
        }
    }

}




//نعم، فكرتك صحيحة وكلاهما يؤدي نفس الغرض، الفرق فقط في أسلوب التعريف:
//
//الأسلوب الأول (باستخدام getter):
//
//private val _followers = MutableLiveData<List<UserItem>>()
//val followers: LiveData<List<UserItem>> get() = _followers
//
//
//هنا followers قراءة فقط من الخارج، ولا يمكن تعديل _followers إلا داخل الـ ViewModel.
//
//استخدام get() يجعلها lazy read-only من أي مكان خارج الـ ViewModel.
//
//الأسلوب الثاني (direct assignment):
//
//private val _followers = MutableLiveData<List<UserItem>>()
//val followers: LiveData<List<UserItem>> = _followers
//
//
//نفس النتيجة عمليًا، followers لا يمكن تعديلها من خارج الـ ViewModel لأنه نوعها LiveData.
//
//أبسط وأوضح للكثير من المطورين، غالبًا تُستخدم كثيرًا في Android MVVM.
//
//✅ الخلاصة: كلاهما صحيح، الأسلوب الثاني أسهل للقراءة والفهم، والأسلوب الأول يعطي مرونة أكثر إذا أردت إضافة منطق عند الوصول للبيانات (مثلاً تعديل أو تصفية قبل الإرجاع).
//
//إذا أحببت، أقدر أعطيك نسخة كاملة للـ ViewModel + Repository + Retrofit جاهزة مع LiveData بهذا الشكل لتعمل مباشرة مع Fragments التي صنعناها.
//
//هل تريد أن أفعل ذلك
    
    //يخبرك إذا كانت حالة HTTP بين 200 و 299.
    //
    //code() → رمز الحالة HTTP (مثلاً 200، 401، 500…)
//ad non-zero length response 47 204
//لما اضغط يحذف من api
//لكن لا يحذف من التطبيق الا عند الخروج









package com.sarrawi.mysocialnetwork.apiresu


import com.sarrawi.mysocialnetwork.model.*
import com.sarrawi.mysocialnetwork.model.ProfileModel.ProfileResponse
import com.sarrawi.mysocialnetwork.model.notif.NotificationResponse
import com.sarrawi.mysocialnetwork.model.notif.RemoveNotificationResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response


/**
 * Repository موحد لكل استدعاءات الشبكة
 * يعيد ApiResult بدل Response مباشرة
 */
class SocialRepository1(private val api: ApiPost1) {


        // PROFILE
        suspend fun getProfile(token: String): ApiResult<ProfileResponse> = safeApiCall {
            api.getProfile("token $token")
        }

        suspend fun getProfileWithId(id: Int, token: String): ApiResult<ProfileResponse> = safeApiCall {
            api.getProfileById(id, "token $token")
        }

        // POSTS
        suspend fun getPosts(token: String): ApiResult<List<PostResponse>> = safeApiCall {
            api.getPosts("token $token")
        }

        suspend fun createPost(
            token: String,
            body: RequestBody,
            sharedBody: RequestBody?,
            images: List<MultipartBody.Part>?
        ): ApiResult<PostResponse> = safeApiCall {
            api.createPost("token $token", body, sharedBody, images)
        }

        suspend fun likePost(token: String, postId: Int): ApiResult<LikeDislikeResponse> = safeApiCall {
            api.likePost("token $token", postId)
        }

        suspend fun dislikePost(token: String, postId: Int): ApiResult<LikeDislikeResponse> = safeApiCall {
            api.dislikePost("token $token", postId)
        }

        suspend fun sharePost(token: String, postId: Int, body: Map<String, String>): ApiResult<PostResponse> = safeApiCall {
            api.sharePost("token $token", postId, body)
        }

        // USERS
        suspend fun searchUsers(token: String, query: String): ApiResult<UserSearchResponse> = safeApiCall {
            api.searchUsers("token $token", query)
        }

        suspend fun getSuggestions(token: String): ApiResult<List<SuggestionResponse>> = safeApiCall {
            api.getSuggestions("token $token")
        }

        // EXPLORE
        suspend fun getExploreTags(token: String, query: String? = null): ApiResult<List<Tag>> = safeApiCall {
            api.getExploreTags("token $token", query)
        }


    suspend fun getExploreTags2(token: String, query: String? = null): ApiResult<ExploreResponse> = safeApiCall {
            api.getExploreTags2("token $token", query)
        }

        suspend fun getExplorePosts(token: String): ApiResult<ExploreResponse2> = safeApiCall {
            api.getExplorePosts("token $token")
        }

        suspend fun searchTags(token: String, query: String? = null): ApiResult<ExploreResponse2> = safeApiCall {
            api.searchTags("token $token", query)
        }

        // NOTIFICATIONS
        suspend fun getAllNotifications(token: String): ApiResult<List<NotificationResponse>> = safeApiCall {
            api.getAllNotifications("token $token")
        }

        suspend fun removeNotification(notificationPk: Int): ApiResult<RemoveNotificationResponse> = safeApiCall {
            api.removeNotification(notificationPk)
        }

        suspend fun removeNotification2(notificationId: Int, token: String): ApiResult<RemoveNotificationResponse> = safeApiCall {
            api.removeNotification2(notificationId, "token $token")
        }

        // Helper function لتوحيد التعامل مع Response
        private suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): ApiResult<T> {
            return try {
                val response = apiCall()
                if (response.isSuccessful && response.body() != null) {
                    ApiResult.Success(response.body()!!)
                } else {
                    ApiResult.Error(response.errorBody()?.string() ?: "Unknown Error")
                }
            } catch (e: Exception) {
                ApiResult.Error(e.localizedMessage ?: "Exception occurred")
            }
        }
    }

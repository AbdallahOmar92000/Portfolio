package com.sarrawi.mysocialnetwork.repository

import android.content.Context
import android.util.Log
import com.sarrawi.mysocialnetwork.api.ApiPost
import com.sarrawi.mysocialnetwork.model.*
import com.sarrawi.mysocialnetwork.model.ProfileModel.ProfileResponse
import com.sarrawi.mysocialnetwork.model.notif.NotificationResponse
import com.sarrawi.mysocialnetwork.model.notif.RemoveNotificationResponse
import com.sarrawi.mysocialnetwork.model.postdetails.Comment
import com.sarrawi.mysocialnetwork.model.postdetails.PostDetails
import com.sarrawi.mysocialnetwork.model.postdetails.Reply
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File

class PostRepository(private val api: ApiPost,private val context: Context) {

    suspend fun fetchPosts(authToken: String): List<PostResponse> =
        api.getPosts("Token $authToken")

    suspend fun getSuggestions(token: String):List<SuggestionResponse> =
        api.getsuggestions(token)


    suspend fun add2Post(
        authToken: String,
        body: String,
        sharedBody: String?,
        imageFiles: List<File>?
    ): PostResponse {
        val bodyPart = body.toRequestBody("text/plain".toMediaTypeOrNull())
        val sharedBodyPart = sharedBody?.toRequestBody("text/plain".toMediaTypeOrNull())

        val imagesParts = imageFiles?.map {
            val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("image", it.name, requestFile)
        }

        return api.createPost("Token $authToken", bodyPart, sharedBodyPart, imagesParts)
    }

    suspend fun addPost(
        authToken: String,
        body: String,
        sharedBody: String?,
        imageParts: List<MultipartBody.Part>?
    ): PostResponse {
        val bodyPart = body.toRequestBody("text/plain".toMediaTypeOrNull())
        val sharedBodyPart = sharedBody?.toRequestBody("text/plain".toMediaTypeOrNull())

        return api.createPost("Token $authToken", bodyPart, sharedBodyPart, imageParts)
    }

//    suspend fun likePost(post: PostResponse): LikeDislikeResponse {
//        val response = api.likePost(post.id)
//        if (response.isSuccessful) {
//            return response.body() ?: throw Exception("Empty response body")
//        } else {
//            throw Exception("Error: ${response.code()} - ${response.message()}")
//        }
//    }
//
//    suspend fun dislikePost(post: PostResponse): LikeDislikeResponse {
//        val response = api.dislikePost(post.id)
//        if (response.isSuccessful) {
//            return response.body() ?: throw Exception("Empty response body")
//        } else {
//            throw Exception("Error: ${response.code()} - ${response.message()}")
//        }
//    }


    suspend fun likePost(token: String, post: PostResponse): LikeDislikeResponse {
        val response = api.likePost("Token $token", post.id)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("Error: ${response.code()} - ${response.message()}")
        }
    }


    suspend fun likePostdet(token: String, post: PostDetails): LikeDislikeResponse {
        val response = api.likePost("Token $token", post.id)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("Error: ${response.code()} - ${response.message()}")
        }
    }

    // في ملف PostRepository.kt

    // التعديل: نغير البارامتر ليكون postId من نوع Int
    suspend fun likePostdet(token: String, postId: Int): LikeDislikeResponse {
        // نمرر الـ ID فقط للـ API

        val response = api.likePost( "Token $token",postId)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("Error: ${response.code()} - ${response.message()}")
        }
    }

    // التعديل هنا: جعل المعامل الثاني postId من نوع Int
    suspend fun dislikePostdet(token: String, postId: Int): LikeDislikeResponse {
        // نمرر postId مباشرة لـ Retrofit
        val response = api.dislikePost("Token $token", postId)

        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("Error: ${response.code()} - ${response.message()}")
        }
    }

    suspend fun likePost2(token: String, post: Post2): LikeDislikeResponse {
        val response = api.likePost("Token $token", post.id)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("Error: ${response.code()} - ${response.message()}")
        }
    }

    suspend fun dislikePost(token: String, post: PostResponse): LikeDislikeResponse {
        val response = api.dislikePost("Token $token", post.id)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("Error: ${response.code()} - ${response.message()}")
        }
    }

    suspend fun dislikePost2(token: String, post: Post2): LikeDislikeResponse {
        val response = api.dislikePost("Token $token", post.id)
        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            throw Exception("Error: ${response.code()} - ${response.message()}")
        }
    }



    suspend fun sharePost(token: String,postId: Int, body: String): PostResponse {
        val tokenHeader = "Token $token"
        return api.sharepost(tokenHeader,postId, mapOf("body" to body))
    }




        fun getToken(): String? {
            val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            return prefs.getString("auth_token", null)
        }



    suspend fun searchUsers(token: String, query: String): UserSearchResponse {
        return api.searchUsers(token, query) // token بالفعل يحتوي "Token "
    }


    suspend fun getTags(token: String, query: String? = null): List<Tag> {
        return api.getExploreTags(token, query)
    }

    suspend fun getTags2(token: String, query: String? = null): ExploreResponse {
        return api.getExploreTags2(token, query)
    }

    //////////////
    suspend fun getExplore(token: String): ExploreResponse2? {
        val response = api.getExplorePosts(token)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun searchExplore(token: String,query: String): ExploreResponse2? {
        val response = api.searchtags(token,query)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun getNotifications(token: String): List<NotificationResponse> {
        return api.getAllNotifications(token)
    }


    suspend fun removeNotification2(notificationId: Int, token: String): Result<RemoveNotificationResponse> {
        return try {
            val response = api.removeNotification2(notificationId, token)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed with code: ${response.code()}")
                )
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProfile(token: String): Response<ProfileResponse> =
        api.getprofile("token $token")

    suspend fun getProfile2(token: String): ProfileResponse? {
        val response = api.getprofile(token)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun getUserProfile(userId: Int,token: String): Result<ProfileResponse> {
        return try {
            val response = api.getprofile_withid(userId, "Token $token")
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFollowStatus(profileId: Int,token: String):FollowingResponse{
        return api.getFollowStatus(profileId,token)
    }

    suspend fun toggleFollow(profileId: Int,token: String): FollowingResponse {
        return api.toggleFollow(profileId,token)
    }

//    suspend fun getFollowStatus(userId: Int, token: String): FollowingResponse {
//        val response = api.getFollowStatus("$token", userId)
//        if (response.isSuccessful) {
//            return response.body()!!
//        } else {
//            Log.e("FollowRepo", "Failed to get follow status: ${response.code()}")
//            throw Exception("Failed to get follow status")
//        }
//    }
//
//    suspend fun toggleFollow(userId: Int, token: String): FollowingResponse {
//        val response = api.toggleFollow("$token", userId)
//        if (response.isSuccessful) {
//            return response.body()!!
//        } else {
//            Log.e("FollowRepo", "Failed to toggle follow: ${response.code()}")
//            throw Exception("Failed to toggle follow")
//        }
//    }

    suspend fun getCurrentUserFollower(token: String):Response<FollowersResponse>{
        return api.getCurrentUserFollowers("Token $token")
    }

    suspend fun getOtherUserFollower(userId: Int,token: String):Response<FollowersResponse>{
        return api.getOtherUserFollowers(userId, "Token $token")
    }

    suspend fun getCurrentUserFollowing(token: String):Response<FollowingResponse2>{
        return api.getCurrentUserFollowing("Token $token")
    }

    suspend fun getOtherUserFollowing(userId: Int,token: String):Response<FollowingResponse2>{
        return api.getOtherUserFollowing(userId, "Token $token")
    }





    /////////////////

//    suspend fun sendReply(postId: Int, commentId: Int, comment: String): Reply {
//        return api.addReply(postId, commentId, mapOf("comment" to comment))
//    }

    suspend fun getPostDetails(postId: Int, token: String): Result<PostDetails> {
        return try {
            val response = api.getPostDetails(postId, "Token $token")
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addComment(postId: Int, body: String, token: String): Result<Comment> {
        return try {
            val map = mapOf("comment" to body)
            val response = api.addComment(postId, map, "Token $token")
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }




//    suspend fun addReply(postId: Int, commentId: Int, body: String, token: String): Result<Reply> {
//        return try {
//            val payload = mapOf("comment" to body)
//            val response = api.addReply(postId, commentId, payload, "Token $token")
//            Result.success(response)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }

    suspend fun addReply(
        postId: Int,
        commentId: Int,
        body: String,
        imageFile: File?,
        token: String
    ): Result<Reply> {
        return try {
            Log.d("ReplyDebug", "Repo: Preparing parts...")

            // استخدام toMediaTypeOrNull() أكثر أماناً
            val commentBody = body.toRequestBody("text/plain".toMediaTypeOrNull())

            val imagePart = imageFile?.let {
                Log.d("ReplyDebug", "Repo: Processing image: ${it.name}")
                val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("image", it.name, requestFile)
            }

            Log.d("ReplyDebug", "Repo: Calling API...")
            val response = api.addReply(postId, commentId, commentBody, imagePart, "Token $token")

            Log.d("ReplyDebug", "Repo: API Response received")
            Result.success(response)

        } catch (e: Exception) {
            Log.e("ReplyDebug", "Repo: Exception caught: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun postComment(postId: Int, commentText: String, imageFile: File?, token: String): Response<Comment> {
        val commentBody = RequestBody.create("text/plain".toMediaTypeOrNull(), commentText)

        val imagePart = imageFile?.let {
            val reqFile = RequestBody.create("image/*".toMediaTypeOrNull(), it)
            MultipartBody.Part.createFormData("image", it.name, reqFile)
        }

        return api.postComment(postId, commentBody, imagePart,"Token $token")
    }

    suspend fun likeComment(postId: Int, commentId: Int, token: String): Comment? {
        val response = api.likeComment(postId, commentId, "Token $token")
        return if (response.isSuccessful) response.body()?.comment else null
    }

    suspend fun dislikeComment(postId: Int, commentId: Int, token: String): Comment? {
        val response = api.dislikeComment(postId, commentId, "Token $token")
        return if (response.isSuccessful) response.body()?.comment else null
    }

}
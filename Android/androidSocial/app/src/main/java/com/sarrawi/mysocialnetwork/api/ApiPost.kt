package com.sarrawi.mysocialnetwork.api

import com.sarrawi.mysocialnetwork.model.*
import com.sarrawi.mysocialnetwork.model.ProfileModel.ProfileResponse
import com.sarrawi.mysocialnetwork.model.Tag
import com.sarrawi.mysocialnetwork.model.notif.NotificationResponse
import com.sarrawi.mysocialnetwork.model.notif.RemoveNotificationResponse
import com.sarrawi.mysocialnetwork.model.postdetails.Comment
import com.sarrawi.mysocialnetwork.model.postdetails.CommentLikeResponse
import com.sarrawi.mysocialnetwork.model.postdetails.PostDetails
import com.sarrawi.mysocialnetwork.model.postdetails.Reply
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiPost {

    @GET("api/posts/")
    suspend fun getPosts(@Header("Authorization") token: String): List<PostResponse>

    @Multipart
    @POST("api/posts/")
    suspend fun createPost(
        @Header("Authorization") token: String,
        @Part("body") body: RequestBody,
        @Part("shared_body") sharedBody: RequestBody?,
        @Part images: List<MultipartBody.Part>?
    ): PostResponse

    @POST("api/like/{id}/")
    suspend fun likePost(
        @Header("Authorization") token: String,

        @Path("id") postId: Int,
    ): Response<LikeDislikeResponse>

    @POST("api/dislike/{id}/")
    suspend fun dislikePost(
        @Header("Authorization") token: String,
        @Path("id") postId: Int, ): Response<LikeDislikeResponse>

    @POST("api/post/{post_pk}/comment/{comment_pk}/like/")
    suspend fun likeComment(
        @Path("post_pk") postId: Int,
        @Path("comment_pk") commentId: Int,
        @Header("Authorization") token: String
    ): Response<CommentLikeResponse>

    @POST("api/post/{post_pk}/comment/{comment_pk}/dislike/")
    suspend fun dislikeComment(
        @Path("post_pk") postId: Int,
        @Path("comment_pk") commentId: Int,
        @Header("Authorization") token: String
    ): Response<CommentLikeResponse>

    @POST("api/share/{pk}/")
    suspend fun sharepost(
        @Header("Authorization") token: String,
        @Path("pk") postId:Int,
        @Body body :Map<String,String>
    ):PostResponse

    @GET("api/search/")
    suspend fun searchUsers(
        @Header("Authorization") token: String,  // Bearer token

        @Query("query") query: String

    ):UserSearchResponse

    @GET("api/suggestions/")
    suspend fun getsuggestions(
        @Header("Authorization") token: String,
    ): List<SuggestionResponse>


    @GET("api/explore2/")
    suspend fun getExploreTags(
        @Header("Authorization") token: String,
        @Query("query") query: String? = null
    ): List<Tag>

    @GET("api/explore/")
    suspend fun getExploreTags2(
        @Header("Authorization") token: String,
        @Query("query") query: String? = null
    ): ExploreResponse

    @GET("api/explore222/")
    suspend fun getExplorePosts(
        @Header("Authorization") token: String,
    ): Response<ExploreResponse2>

    @POST("api/explore222/")
    @FormUrlEncoded
    suspend fun searchtags(
        @Header("Authorization") token: String,
        @Field("query") query: String? = null
    ): Response<ExploreResponse2>

    @GET("api/notif/")
    suspend fun getAllNotifications(
        @Header("Authorization") token: String,
        ): List<NotificationResponse>


    @DELETE("notification/delete/{notification_pk}/")
    suspend fun removeNotification(
        @Path("notification_pk") notificationPk: Int
    ): Response<RemoveNotificationResponse>

    @DELETE("notifications2/{id}/delete/")
    suspend fun removeNotification2(
        @Path("id") notificationId: Int,
        @Header("Authorization") token: String
    ): Response<RemoveNotificationResponse>

    @GET("api/profile/")
    suspend fun getprofile(
        @Header("Authorization") token: String
    ):Response<ProfileResponse>

    @GET("api/profile/{id}")
    suspend fun getprofile_withid(
        @Path("id") userId: Int,
        @Header("Authorization") token: String
    ): Response<ProfileResponse>

    // لجلب حالة المتابعة (GET)
    @GET("api/toggle-follow/{profileId}/")
    suspend fun getFollowStatus(
        @Path("profileId") profileId: Int,
        @Header("Authorization") token: String

    ): FollowingResponse

    // لتغيير الحالة (POST)
    @POST("api/toggle-follow/{profileId}/")
    suspend fun toggleFollow(
        @Path("profileId") profileId: Int,
        @Header("Authorization") token: String
    ): FollowingResponse

    @GET("api/followers/")
    suspend fun getCurrentUserFollowers(
        @Header("Authorization") token: String
    ):Response<FollowersResponse>

    @GET("api/followers/{id}/")
    suspend fun getOtherUserFollowers(
        @Path("id") userId: Int,
        @Header("Authorization") token: String
    ):Response<FollowersResponse>

    @GET("api/following/")
    suspend fun getCurrentUserFollowing(
        @Header("Authorization") token: String
    ):Response<FollowingResponse2>

    @GET("api/following/{id}/")
    suspend fun getOtherUserFollowing(
        @Path("id")userId: Int,
        @Header("Authorization") token: String
    ):Response<FollowingResponse2>

    // جلب تفاصيل البوست + التعليقات + الردود
    @GET("api/postdetail/{id}/")
    suspend fun getPostDetails(
        @Path("id") postId: Int,
        @Header("Authorization") token: String
    ): PostDetails

    // إرسال تعليق
    @POST("api/postdetail/{id}/")
    suspend fun addComment(
        @Path("id") postId: Int,
        @Body body: Map<String, String>,
        @Header("Authorization") token: String
    ): Comment

    // إرسال رد
    @Multipart
    @POST("api/postdetail/{postId}/comment/{commentId}/reply/")
    suspend fun addReply2(
        @Path("postId") postId: Int,
        @Path("commentId") commentId: Int,
        @Body body: Map<String, String>,
        @Part image: MultipartBody.Part? = null,
        @Header("Authorization") token: String
    ): Reply

    @Multipart
    @POST("api/postdetail/{postId}/comment/{commentId}/reply/")
    suspend fun addReply(
        @Path("postId") postId: Int,
        @Path("commentId") commentId: Int,
        @Part("comment") comment: RequestBody,
        @Part image: MultipartBody.Part? = null,
        @Header("Authorization") token: String
    ): Reply


    /////
@Multipart
@POST("api/postdetail/{post_id}/")
suspend fun postComment(
    @Path("post_id") postId: Int,
    @Part("comment") comment: RequestBody,
    @Part image: MultipartBody.Part? = null,   // الصورة اختيارية
    @Header("Authorization") token: String

): Response<Comment>



    // إرسال رد




    companion object {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.HEADERS
        }

        var retrofitService: ApiPost? = null

        fun provideRetrofitInstance(): ApiPost {
            if (retrofitService == null) {
                // إعداد OkHttpClient مع الـ logging interceptor
                val client = OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build()

                val retrofit = Retrofit.Builder()
                    .baseUrl("https://networksocial.xyz/social/")
                    .client(client) // ← مهم جدًا
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                retrofitService = retrofit.create(ApiPost::class.java)
            }
            return retrofitService!!
        }
    }

}

package com.sarrawi.mysocialnetwork.apiresu

import com.sarrawi.mysocialnetwork.api.ApiPost
import com.sarrawi.mysocialnetwork.model.*
import com.sarrawi.mysocialnetwork.model.ProfileModel.ProfileResponse
import com.sarrawi.mysocialnetwork.model.Tag
import com.sarrawi.mysocialnetwork.model.notif.NotificationResponse
import com.sarrawi.mysocialnetwork.model.notif.RemoveNotificationResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ApiPost1 {

    // ✅ جلب المنشورات
    @GET("api/posts/")
    suspend fun getPosts(
        @Header("Authorization") token: String
    ): Response<List<PostResponse>>

    // ✅ إنشاء منشور جديد
    @Multipart
    @POST("api/posts/")
    suspend fun createPost(
        @Header("Authorization") token: String,
        @Part("body") body: RequestBody,
        @Part("shared_body") sharedBody: RequestBody?,
        @Part images: List<MultipartBody.Part>?
    ): Response<PostResponse>

    // ✅ لايك
    @POST("api/like/{id}/")
    suspend fun likePost(
        @Header("Authorization") token: String,
        @Path("id") postId: Int
    ): Response<LikeDislikeResponse>

    // ✅ ديسلايك
    @POST("api/dislike/{id}/")
    suspend fun dislikePost(
        @Header("Authorization") token: String,
        @Path("id") postId: Int
    ): Response<LikeDislikeResponse>

    // ✅ مشاركة
    @POST("api/share/{pk}/")
    suspend fun sharePost(
        @Header("Authorization") token: String,
        @Path("pk") postId: Int,
        @Body body: Map<String, String>
    ): Response<PostResponse>

    // ✅ البحث عن مستخدمين
    @GET("api/search/")
    suspend fun searchUsers(
        @Header("Authorization") token: String,
        @Query("query") query: String
    ): Response<UserSearchResponse>

    // ✅ اقتراحات
    @GET("api/suggestions/")
    suspend fun getSuggestions(
        @Header("Authorization") token: String
    ): Response<List<SuggestionResponse>>

    // ✅ التاغز للاستكشاف (النوع الأول)
    @GET("api/explore2/")
    suspend fun getExploreTags(
        @Header("Authorization") token: String,
        @Query("query") query: String? = null
    ): Response<List<Tag>>

    // ✅ التاغز للاستكشاف (النوع الثاني)
    @GET("api/explore/")
    suspend fun getExploreTags2(
        @Header("Authorization") token: String,
        @Query("query") query: String? = null
    ): Response<ExploreResponse>

    // ✅ منشورات الاستكشاف
    @GET("api/explore222/")
    suspend fun getExplorePosts(
        @Header("Authorization") token: String
    ): Response<ExploreResponse2>

    // ✅ البحث في التاغز
    @POST("api/explore222/")
    @FormUrlEncoded
    suspend fun searchTags(
        @Header("Authorization") token: String,
        @Field("query") query: String? = null
    ): Response<ExploreResponse2>

    // ✅ الإشعارات
    @GET("api/notif/")
    suspend fun getAllNotifications(
        @Header("Authorization") token: String
    ): Response<List<NotificationResponse>>

    // ✅ حذف إشعار
    @DELETE("notification/delete/{notification_pk}/")
    suspend fun removeNotification(
        @Path("notification_pk") notificationPk: Int
    ): Response<RemoveNotificationResponse>

    // ✅ حذف إشعار (النوع الثاني)
    @DELETE("notifications2/{id}/delete/")
    suspend fun removeNotification2(
        @Path("id") notificationId: Int,
        @Header("Authorization") token: String
    ): Response<RemoveNotificationResponse>

    // ✅ جلب الملف الشخصي
    @GET("api/profile/")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<ProfileResponse>

    // ✅ جلب ملف شخصي عبر ID
    @GET("api/profile/{id}")
    suspend fun getProfileById(
        @Path("id") userId: Int,
        @Header("Authorization") token: String
    ): Response<ProfileResponse>

    // ✅ Retrofit Instance
    companion object {
        private var retrofitService: ApiPost? = null

        fun provideRetrofitInstance(): ApiPost {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl("https://networksocial.xyz/social/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(ApiPost::class.java)
            }
            return retrofitService!!
        }
    }
}

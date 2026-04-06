from django.urls import path,include
from .views import *

urlpatterns = [
    path('', PostListView.as_view(), name='post-list'),
    path('post/<int:pk>/', PostDetailView.as_view(), name='post-detail'),
    path('post/edit/<int:pk>/', PostEditView.as_view(), name='post-edit'),
    path('post/delete/<int:pk>/', PostDeleteView.as_view(), name='post-delete'),
    path('post/<int:post_pk>/comment/delete/<int:pk>/', CommentDeleteView.as_view(), name='comment-delete'),
    path('post/<int:post_pk>/comment/<int:pk>/like', AddCommentLikeview.as_view(), name='comment-like'),
    path('post/<int:post_pk>/comment/<int:pk>/dislike', AddCommentDislikeview.as_view(), name='comment-dislike'),
    path('post/<int:post_pk>/comment/<int:pk>/reply', CommentReplyView.as_view(), name='comment-reply'),
    

    #path('post/<int:pk>/like', AddLike.as_view(), name='like'),
    #path('post/<int:pk>/dislike', AddDislike.as_view(), name='dislike'),
    path('like/<int:pk>/', AddLike.as_view(), name='like'),
    path('dislike/<int:pk>/', AddDislike.as_view(), name='dislike'),
    path('post/<int:pk>/share', SharedPostView.as_view(), name='share-post'),

    path('profile/<int:pk>/', ProfileView.as_view(), name='profile'),
    path('profile/edit/<int:pk>/', ProfileEditView.as_view(), name='profile-edit'),
    path('profile/<int:pk>/followers/', ListFollowers.as_view(), name='list-followers'),
    path('profile/<int:pk>/followers/add', AddFollower.as_view(), name='add-follower'),
    path('profile/<int:pk>/followers/remove', RemoveFollower.as_view(), name='remove-follower'),
    path('search/', UserSearch.as_view(), name='profile-search'),
    path('profile/<int:pk>/following/', FollowingListView.as_view(), name='following_list'),
    path('notification/<int:notification_pk>/post/<int:post_pk>', PostNotification.as_view(), name='post-notification'),
    path('notification/<int:notification_pk>/profile/<int:profile_pk>', FollowNotification.as_view(), name='follow-notification'),
    path('notification/delete/<int:notification_pk>', RemoveNotification.as_view(), name='notification-delete'),
    path('notification/<int:notification_pk>/thread/<int:object_pk>', ThreadNotification.as_view(), name='thread-notification'),
    path('inbox/',ListThreads.as_view(),name='inbox'),
    path('inbox/create-thread',CreateThread.as_view(),name='create-thread'),
    path('inbox/<int:pk>/', ThreadView.as_view(), name='thread'),
   # path('inbox/<int:pk>/create-message/', CreateMessage.as_view(), name='create-message'),
    path('explore/',Explore.as_view(),name='explore'),
    path('check_availability/', check_availability, name='check_availability'),
    path('suggestions/', suggestions_view, name='suggestions'),
    #path('thread/update-read-status/<int:pk>/', UpdateReadStatusView.as_view(), name='update-read-status'),
    path('create-message/<int:pk>/', CreateMessage.as_view(), name='create-message'),
    
    path('update-read-status/<int:pk>/', UpdateReadStatusView.as_view(), name='update-read-status'),

    ###############################
    
    

    #path('api/auth/', include('dj_rest_auth.urls')),  # ?? ???? ???? ?? ??? login/logout/...
    path('api/auth/reg/', CustomRegisterView.as_view(), name='custom_register'), 
    path('api/auth/login/', CustomLoginView.as_view(), name='custom_login'),
    path('api/auth/logout/', LogoutView.as_view(), name='rest_logout'),
    path('api/auth/logout2/', LogoutView2.as_view(), name='api_logout'),  
    path('api/auth/login2/', CustomLoginView2.as_view(), name='custom_login'),
    path('api/posts/', PostListAPIView.as_view(), name='post-list-api'),
    path('api/like/<int:pk>/', LikePostAPI.as_view(), name='api-like-post'),
    path('api/dislike/<int:pk>/', DislikePostAPI.as_view(), name='api-dislike-post'),
    path('api/share/<int:pk>/', SharedPostAPIView.as_view(), name='api-share-post'),
    path('api/search/', UserSearchAPI.as_view(), name='user-search-api'),
    path("api/suggestions/", SuggestionsApiView.as_view(), name="suggestions_api"),
    path('api/explore/', Explores.as_view(), name='api-explore'),
    path('api/explore2/', Explores2.as_view(), name='api-explore'),
    path('api/explore222/', Explore222.as_view(), name='api-explore'),
    path('api/notification/<int:notification_pk>/post/<int:post_pk>/', PostNotificationAPIView.as_view(), name='post-notification-api'),
    path(
        'api/follow-notification/<int:notification_pk>/<int:profile_pk>/',
        FollowNotificationApi.as_view(),
        name='follow-notification-api'
    ),

    
    path(
        'api/thread-notification/<int:notification_pk>/<int:thread_pk>/',
        ThreadNotificationApi.as_view(),
        name='thread-notification-api'
    ),
    
    path("api/notif/", AllNotificationsApi.as_view(), name="all_notifications_api"),
    path('notifications/<int:notification_pk>/delete/', RemoveNotification2.as_view(), name='remove_notification2'),
    path('notifications2/<int:notification_pk>/delete/', RemoveNotificationAPIView2.as_view(), name='remove_notification_api'),
    
     # ???????? ?????? (???? ??????)
    path('api/profile/', ProfileAPIView.as_view(), name='current-user-profile'),

    # ??? ?????? ??? ??? ??? pk
    path('api/profile/<int:pk>/', ProfileAPIView.as_view(), name='other-user-profile'),

    path('api/toggle-follow/<int:pk>/', ToggleFollowerAPI.as_view(), name='api-toggle-follower'),

    path('api/followers/', ListFollowersAPIView.as_view(), name='current_user_followers'),

    # متابعي مستخدم آخر عبر الـ pk
    path('api/followers/<int:pk>/', ListFollowersAPIView.as_view(), name='other_user_followers'),

    path('api/following/', FollowingListAPIView.as_view(), name='current_user_following'),
    path('api/following/<int:pk>/', FollowingListAPIView.as_view(), name='other_user_following'),
    path('api/following/', FollowingListAPIView.as_view(), name='current_user_following'),
    path('api/following/<int:pk>/', FollowingListAPIView.as_view(), name='other_user_following'),
    path('api/postdetail/<int:pk>/', PostDetailsApiView2.as_view(), name='api-post-detail'),
    path('api/postdetail/<int:post_pk>/comment/<int:pk>/reply/', CommentReplyViewApiView.as_view(), name='ddcomment-replyhggg'),
    path('api/post/<int:post_pk>/comment/<int:pk>/like/', AddCommentLike.as_view(), name='acomment-like'),
    path('api/post/<int:post_pk>/comment/<int:pk>/dislike/', AddCommentDislike.as_view(), name='acomment-dislike'),
    
    
    
    


    #path('', include('posts.web_urls')),   # صفحات الويب
   # path('api/', include('posts.api_urls')),  # واجهة الـ API




]


#| Endpoint                                 | ???????                                  |
#| ---------------------------------------- | ---------------------------------------- |
#| `POST /api/auth/login/`                  | ????? ????                               |
#| `POST /api/auth/logout/`                 | ????? ????                               |
#| `POST /api/auth/password/reset/`         | ??? ????? ????? ???? ?????? (???? ?????) |
#| `POST /api/auth/password/reset/confirm/` | ????? ????? ???????                      |
#| `POST /api/auth/registration/`           | ????? ?????? ????                        |
#| `POST /api/auth/password/change/`        | ????? ???? ??????                        |



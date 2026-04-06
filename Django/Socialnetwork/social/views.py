from django.shortcuts import render, redirect, get_object_or_404
from django.db.models import Q
from django.utils import timezone
from django.urls import reverse_lazy
from django.http import HttpResponseRedirect
from django.contrib.auth.mixins import UserPassesTestMixin, LoginRequiredMixin
from django.views import View
from .models import *
from .forms import *
from django.views.generic.edit import UpdateView, DeleteView
from .models import CustomUser

#from django.contrib.auth.models import User
from django.contrib import messages
from django.http import JsonResponse
from django.http import HttpResponse
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework.permissions import IsAuthenticated
from rest_framework import status, parsers
from dj_rest_auth.registration.views import RegisterView
from social.serializers2 import *
from .serializers import *
from dj_rest_auth.views import LoginView

from django.contrib.auth import get_user_model
User = get_user_model()

from rest_framework.authentication import TokenAuthentication
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status, permissions
from rest_framework.authtoken.models import Token
from rest_framework.permissions import IsAuthenticated
from rest_framework.response import Response
from rest_framework import status, parsers
from rest_framework.views import APIView
from django.db.models import Q


from rest_framework import status


from django.shortcuts import get_object_or_404






#https://chatgpt.com/c/6727bffc-3928-8010-bdb5-eaf84d4eaeb9


class P1ostListView(LoginRequiredMixin, View):
    #def get(self, request, *args, **kwargs):
  #  posts = Post.objects.all().order_by('-created_on')
 #   form = PostForm()

#    context = {
#        'post_list': posts,
#        'form': form,
#    }
#    return render(request, 'social/post_list.html', context)

    
    def get(self, request, *args, **kwargs):
        logged_in_user = request.user
        posts = Post.objects.filter(
            author__profile__followers__in=[logged_in_user.id]
        ).order_by('-created_on')
        form = PostForm()
        share_form =ShareForm()

        context = {
            'post_list': posts,
            'form': form,
        }

        return render(request, 'social/post_list.html', context)

    def post(self, request, *args, **kwargs):
        #posts = Post.objects.all().order_by('-created_on')
        #add
        logged_in_user = request.user
        posts = Post.objects.filter(
            author__profile__followers__in=[logged_in_user.id]
        ).order_by('-created_on')
        #form = PostForm(request.POST)
        #add
        form = PostForm(request.POST,request.FILES)

        if form.is_valid():
            new_post = form.save(commit=False)
            new_post.author = request.user
            new_post.save()

        context = {
            'post_list': posts,
            'form': form,
        }

        return render(request, 'social/post_list.html', context)
        
        
        
class PostListView(LoginRequiredMixin, View):

    #def get(self, request, *args, **kwargs):
  #  posts = Post.objects.all().order_by('-created_on')
 #   form = PostForm()

#    context = {
#        'post_list': posts,
#        'form': form,
#    }
#    return render(request, 'social/post_list.html', context)
    def get(self, request, *args, **kwargs):
        logged_in_user = request.user
        posts = Post.objects.filter(
            author__profile__followers__in=[logged_in_user.id]
        )
        form = PostForm()
        share_form = ShareForm()

        context = {
            'post_list': posts,
            'shareform': share_form,
            'form': form,
        }

        return render(request, 'social/post_list.html', context)

    def post(self, request, *args, **kwargs):
        logged_in_user = request.user
        posts = Post.objects.filter(
            author__profile__followers__in=[logged_in_user.id]
        )
        form = PostForm(request.POST, request.FILES)
        files = request.FILES.getlist('images')
        share_form = ShareForm()

        if form.is_valid():
            new_post = form.save(commit=False)
            new_post.author = request.user
            new_post.save()
            
            new_post.create_tags()

            for f in files:
                img = Image(image=f)
                img.save()
                new_post.image.add(img)

            new_post.save()

        context = {
            'post_list': posts,
            'shareform': share_form,
            'form': form,
        }

        return render(request, 'social/post_list.html', context)
        



class PostListAPIView(APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request, *args, **kwargs):
        # جلب المنشورات الخاصة بالمستخدمين الذين يتابعهم المستخدم الحالي
        posts = Post.objects.filter(
            author__profile__followers__in=[request.user.id]
        ).order_by('-created_on')

        serializer = PostSerializer(posts, many=True, context={'request': request})
        return Response(serializer.data)

    def post(self, request, *args, **kwargs):
        # استقبال بيانات البوست الجديد
        body = request.data.get('body')
        shared_body = request.data.get('shared_body', None)
        files = request.FILES.getlist('images')

        if not body and not files:
            return Response({"error": "يجب إضافة نص أو صورة على الأقل."},
                            status=status.HTTP_400_BAD_REQUEST)

        # إنشاء المنشور
        post = Post.objects.create(
            author=request.user,
            body=body,
            shared_body=shared_body
        )

        # إنشاء الوسوم إذا كانت هناك دالة create_tags
        if hasattr(post, 'create_tags'):
            post.create_tags()

        # حفظ الصور
        for f in files:
            img = Image.objects.create(image=f)
            post.image.add(img)

        post.save()

        serializer = PostSerializer(post, context={'request': request})
        return Response(serializer.data, status=status.HTTP_201_CREATED)
        

        




        
class PostListAPIView2(APIView):
    permission_classes = [IsAuthenticated]
    parser_classes = [parsers.MultiPartParser, parsers.FormParser]

    def get(self, request, *args, **kwargs):
        user = request.user

        following_profiles = UserProfile.objects.filter(followers=user)
        following_users = CustomUser.objects.filter(profile__in=following_profiles)

        posts = Post.objects.filter(author__in=following_users).distinct().order_by('-created_on')

        # تمرير request للسيريالايزر
        serializer = PostSerializer(posts, many=True, context={'request': request})
        return Response(serializer.data)

    def post(self, request, *args, **kwargs):
        user = request.user
        body = request.data.get("body", "")
        shared_body = request.data.get("shared_body", "")
        images = request.FILES.getlist('image')

        if not body and not shared_body:
            return Response({"error": "body or shared_body is required."}, status=status.HTTP_400_BAD_REQUEST)

        post = Post.objects.create(author=user, body=body, shared_body=shared_body)
        post.create_tags()

        for f in images:
            img = Image.objects.create(image=f)
            post.image.add(img)

        post.save()

        # تمرير request للسيريالايزر
        serializer = PostSerializer(post, context={'request': request})
        return Response(serializer.data, status=status.HTTP_201_CREATED)
        
        



class PostDetailView2(LoginRequiredMixin, View):
    def get(self, request, pk, *args, **kwargs):
        post = Post.objects.get(pk=pk)
        form = CommentForm()
        
        #add
        comments = Comment.objects.filter(post=post).order_by('-created_on')
        #add
        notification = Notification.objects.create(notification_type=2,from_user=request.user,to_user=post.author,post=post)

        context = {
            'post': post,
            'form': form,
            'comments': comments,
        }

        return render(request, 'social/post_detail.html', context)
    def post(self, request, pk, *args, **kwargs):
        post = Post.objects.get(pk=pk)
        #form = CommentForm(request.POST)
        #add image
        form = CommentForm(request.POST, request.FILES) 

        if form.is_valid():
            new_comment = form.save(commit=False)
            new_comment.author = request.user
            new_comment.post = post
            new_comment.save()
            
            new_comment.create_tags()

        comments = Comment.objects.filter(post=post).order_by('-created_on')

        context = {
            'post': post,
            'form': form,
            'comments': comments,
        }
        

        return render(request, 'social/post_detail.html', context)
        
class PostDetailView(LoginRequiredMixin, View):
    def get(self, request, pk, *args, **kwargs):
        post = Post.objects.get(pk=pk)
        form = CommentForm()
        reply_form = CommentRepForm()
        #comments = Comment.objects.filter(post=post).order_by('-created_on')
        comments = Comment.objects.filter(post=post, parent=None).order_by('-created_on')

        context = {
            'post': post,
            'form': form,
            'reply_form': reply_form,
            'comments': comments,
        }

        return render(request, 'social/post_detail.html', context)

    def post(self, request, pk, *args, **kwargs):
        post = Post.objects.get(pk=pk)
        form = CommentForm(request.POST, request.FILES)  # ????? request.FILES

        if form.is_valid():
            new_comment = form.save(commit=False)
            new_comment.author = request.user
            new_comment.post = post
            new_comment.save()

            new_comment.create_tags()

            # ??? ????? ????
            #messages.success(request, "?? ????? ??????? ?????.")
        else:
            # ??? ????? ??? ?? ?????? ???????
            #messages.error(request, "??? ??? ????? ????? ???????.")
            for error in form.errors.values():
                messages.error(request, error)
        
        comments = Comment.objects.filter(post=post).order_by('-created_on')
        notification = Notification.objects.create(notification_type=2, from_user=request.user, to_user=post.author, post=post)

        context = {
            'post': post,
            'form': form,
            'comments': comments,
        }

        return render(request, 'social/post_detail.html', context)

        


# views.py


class PostDetailsApiView2(APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request, pk, *args, **kwargs):
        try:
            post = Post.objects.get(pk=pk)
        except Post.DoesNotExist:
            return Response({"detail": "Post not found."}, status=status.HTTP_404_NOT_FOUND)

        serializer = PostSerializeraa(post, context={'request': request})
        return Response(serializer.data, status=status.HTTP_200_OK)

    def post(self, request, pk, *args, **kwargs):
        try:
            post = Post.objects.get(pk=pk)
        except Post.DoesNotExist:
            return Response({"detail": "Post not found."}, status=status.HTTP_404_NOT_FOUND)

        serializer = CommentSerializer2(data=request.data, context={'request': request})
        if serializer.is_valid():
            serializer.save(author=request.user, post=post)
            return Response(serializer.data, status=status.HTTP_201_CREATED)

        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)







              


    

class CommentReplyView(LoginRequiredMixin, View):
    def post(self, request, post_pk, pk, *args, **kwargs):
        post = Post.objects.get(pk=post_pk)
        parent_comment = Comment.objects.get(pk=pk)
        form = CommentRepForm(request.POST, request.FILES)

        if form.is_valid():
            new_comment = form.save(commit=False)
            new_comment.author = request.user
            new_comment.post = post
            new_comment.parent = parent_comment
            new_comment.save()

            print("Comment Image:", new_comment.image)
            if new_comment.image:
                print("Image URL:", new_comment.image.url)

            # هذه الأسطر الآن داخل الـ if
            new_comment.create_tags()

            Notification.objects.create(
                notification_type=2,
                from_user=request.user,
                to_user=parent_comment.author,
                comment=new_comment
            )

        else:
            print("Form errors:", form.errors)

        return redirect('post-detail', pk=post_pk)
        
        
class CommentReplyViewApiView(APIView):
    permission_classes = [IsAuthenticated]
    
    def post(self ,request, post_pk, pk, *args, **kwargs):
        try:
            post = Post.objects.get(pk=post_pk)
        except Post.DoesNotExist:
            return Response({"detail": "Post not found."}, status=status.HTTP_404_NOT_FOUND)

        try:
            parent_comment = Comment.objects.get(pk=pk)
        except Comment.DoesNotExist:
            return Response({"detail": "Parent comment not found."}, status=status.HTTP_404_NOT_FOUND)

        serializer = CommentSerializer2(data=request.data, context={'request': request})
        
        if serializer.is_valid():
            serializer.save(
                author=request.user,
                post=post,
                parent=parent_comment
            )

            Notification.objects.create(
                notification_type=2,
                from_user=request.user,
                to_user=parent_comment.author,
                comment=serializer.instance
            )

            return Response(serializer.data, status=status.HTTP_201_CREATED)
        
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)

        
        



class PostCommentsApiView(APIView):
    """
    عرض جميع التعليقات الخاصة بمنشور محدد
    وإنشاء تعليق جديد على المنشور
    """
    
    def get(self, request, pk, *args, **kwargs):
        # جلب المنشور بناءً على PK
        try:
            post = Post.objects.get(pk=pk)
        except Post.DoesNotExist:
            return Response({"detail": "Post not found."}, status=status.HTTP_404_NOT_FOUND)
        
        # جلب التعليقات الرئيسية (parent=None) فقط
        comments = Comment.objects.filter(post=post, parent=None).order_by('-created_on')
        
        # تحويل التعليقات إلى JSON باستخدام Serializer
        serializer = CommentSerializer2(comments, many=True, context={'request': request})
        
        return Response(serializer.data, status=status.HTTP_200_OK)
    
    def post(self, request, pk, *args, **kwargs):
        # جلب المنشور
        try:
            post = Post.objects.get(pk=pk)
        except Post.DoesNotExist:
            return Response({"detail": "Post not found."}, status=status.HTTP_404_NOT_FOUND)
        
        # إنشاء Serializer للتعليق الجديد
        serializer = CommentSerializer2(data=request.data, context={'request': request})
        
        if serializer.is_valid():
            # حفظ التعليق وربطه بالـ author والـ post
            serializer.save(author=request.user, post=post)
            return Response(serializer.data, status=status.HTTP_201_CREATED)
        
        return Response(serializer.errors, status=status.HTTP_400_BAD_REQUEST)



class PostEditView(LoginRequiredMixin, UserPassesTestMixin, UpdateView):
    model = Post
    fields = ['body']
    template_name = 'social/post_edit.html'

    def get_success_url(self):
        pk = self.kwargs['pk']
        return reverse_lazy('post-detail', kwargs={'pk': pk})

    def test_func(self):
        post = self.get_object()
        return self.request.user == post.author

class PostDeleteView(LoginRequiredMixin, UserPassesTestMixin, DeleteView):
    model = Post
    template_name = 'social/post_delete.html'
    success_url = reverse_lazy('post-list')

    def test_func(self):
        post = self.get_object()
        return self.request.user == post.author

class CommentDeleteView(LoginRequiredMixin, UserPassesTestMixin, DeleteView):
    model = Comment
    template_name = 'social/comment_delete.html'

    def get_success_url(self):
        pk = self.kwargs['post_pk']
        return reverse_lazy('post-detail', kwargs={'pk': pk})

    def test_func(self):
        post = self.get_object()
        return self.request.user == post.author

class ProfileView2(View):
    def get(self, request, pk, *args, **kwargs):
        profile = UserProfile.objects.get(pk=pk)
        user = profile.user
        posts = Post.objects.filter(author=user).order_by('-created_on')

        followers = profile.followers.all()
        following_count = UserProfile.objects.filter(followers=profile.user).count()

        if len(followers) == 0:
            is_following = False

        for follower in followers:
            if follower == request.user:
                is_following = True
                break
            else:
                is_following = False

        number_of_followers = len(followers)

        context = {
            'user': user,
            'profile': profile,
            'posts': posts,
            'number_of_followers': number_of_followers,
            'following_count': following_count,
            'is_following': is_following,
        }

        return render(request, 'social/profile.html', context)
        
class ProfileView(View):
    def get(self, request, pk, *args, **kwargs):
        # ??????? get_object_or_404 ?????? ??? ??????
        profile = get_object_or_404(UserProfile, pk=pk)
        user = profile.user
        posts = Post.objects.filter(author=user).order_by('-created_on')

        followers = profile.followers.all()
        following_count = UserProfile.objects.filter(followers=profile.user).count()

        # ?????? ??? ??? ??? ???????? ????? ????? ??????
        is_following = request.user in followers

        number_of_followers = followers.count()

        context = {
            'user': user,
            'profile': profile,
            'posts': posts,
            'number_of_followers': number_of_followers,
            'following_count': following_count,
            'is_following': is_following,
            'first_name': user.first_name,
            'last_name': user.last_name,
            'gender': user.gender,
        }

        return render(request, 'social/profile.html', context)

class ProfileEditView2(LoginRequiredMixin, UserPassesTestMixin, UpdateView):
    model = UserProfile
    fields = ['name', 'bio', 'birth_date', 'location', 'picture']
    template_name = 'social/profile_edit.html'

    def get_success_url(self):
        pk = self.kwargs['pk']
        return reverse_lazy('profile', kwargs={'pk': pk})

    def test_func(self):
        profile = self.get_object()
        return self.request.user == profile.user
        
        
        


class ProfileEditView(LoginRequiredMixin, UserPassesTestMixin, UpdateView):

    
    model = UserProfile
    form_class = UserProfileForm
    template_name = 'social/profile_edit.html'

    def get_form_kwargs(self):
        kwargs = super().get_form_kwargs()
        kwargs['user'] = self.object.user  # ????? ???????? ?????? ??? ???????
        return kwargs

    def form_valid(self, form):
        user = form.instance.user
        #user.username = form.cleaned_data['username']
        user.email = form.cleaned_data['email']
        user.gender = form.cleaned_data.get('gender', user.gender)  # حفظ gender
        

        user.save()
        return super().form_valid(form)

    def get_success_url(self):
        return reverse_lazy('profile', kwargs={'pk': self.object.pk})

    def test_func(self):
        profile = self.get_object()
        return self.request.user == profile.user



    def form_invalid(self, form):
        print(form.errors)  # ????? ??????? ??????
        return super().form_invalid(form)



    def get_success_url(self):
        return reverse_lazy('profile', kwargs={'pk': self.object.pk})



    def test_func(self):
        profile = self.get_object()
        return self.request.user == profile.user


    
def check_availability(request):
    field_type = request.GET.get('field_type', None)
    value = request.GET.get('value', None)

  #  if field_type == 'username':
    #    is_available = not User.objects.filter(username=value).exists()
    if field_type == 'email':
        is_available = not User.objects.filter(email=value).exists()
    else:
        is_available = False

    return JsonResponse({'is_available': is_available})




class AddFollower(LoginRequiredMixin, View):
    def post(self, request, pk, *args, **kwargs):
        profile = UserProfile.objects.get(pk=pk)
        profile.followers.add(request.user)
        
        #add
        notification = Notification.objects.create(notification_type=3, from_user=request.user, to_user=profile.user)

        return redirect('profile', pk=profile.pk)

class RemoveFollower(LoginRequiredMixin, View):
    def post(self, request, pk, *args, **kwargs):
        profile = UserProfile.objects.get(pk=pk)
        profile.followers.remove(request.user)

        return redirect('profile', pk=profile.pk)
        

#@csrf_exempt
#@api_view(['GET', 'POST'])
#@authentication_classes([TokenAuthentication])
#@permission_classes([IsAuthenticated])        
class ToggleFollowerAPI(APIView):
    """
    API لتبديل متابعة المستخدم الحالي لملف شخصي معين.
    POST → إذا يتابع بالفعل → إزالة المتابعة
           إذا لا يتابع → إضافة المتابعة + إنشاء إشعار
    GET  → إرجاع حالة المتابعة فقط
    """
    authentication_classes = [TokenAuthentication]

    permission_classes = [IsAuthenticated]

    def post(self, request, pk, format=None):
        try:
            profile = UserProfile.objects.get(pk=pk)
        except UserProfile.DoesNotExist:
            return Response({"detail": "Profile not found"}, status=status.HTTP_404_NOT_FOUND)
        
        user = request.user

        if user in profile.followers.all():
            # إزالة المتابعة
            profile.followers.remove(user)
            is_following = False
        else:
            # إضافة المتابعة
            profile.followers.add(user)
            is_following = True

            # إنشاء إشعار
            Notification.objects.create(
                notification_type=3,
                from_user=user,
                to_user=profile.user
            )

        serializer = FollowerSerializer({
            "profile_id": profile.pk,
            "is_following": is_following
        })
        return Response(serializer.data, status=status.HTTP_200_OK)

    def get(self, request, pk, format=None):
        """
        إرجاع حالة المتابعة فقط بدون تعديل
        """
        try:
            profile = UserProfile.objects.get(pk=pk)
        except UserProfile.DoesNotExist:
            return Response({"detail": "Profile not found"}, status=status.HTTP_404_NOT_FOUND)
        
        is_following = request.user in profile.followers.all()
        serializer = FollowerSerializer({
            "profile_id": profile.pk,
            "is_following": is_following
        })
        return Response(serializer.data, status=status.HTTP_200_OK)

class AddLike2(LoginRequiredMixin, View):
    def post(self, request, pk, *args, **kwargs):
        # نحصل على المنشور الذي نريد إضافة الإعجاب إليه بناءً على المفتاح الأساسي (pk).
        post = Post.objects.get(pk=pk)

        # نتحقق إذا كان المستخدم قد قام بالفعل بعدم الإعجاب بالمنشور.
        is_dislike = False
        for dislike in post.dislikes.all():
            if dislike == request.user:
                is_dislike = True
                break

        # إذا كان المستخدم قد قام بعدم الإعجاب سابقًا، نزيل عدم الإعجاب.
        if is_dislike:
            post.dislikes.remove(request.user)

        # نتحقق إذا كان المستخدم قد أعجب بالفعل بالمنشور.
        is_like = False
        for like in post.likes.all():
            if like == request.user:
                is_like = True
                break

        # إذا لم يقم المستخدم بالإعجاب سابقًا، نضيف الإعجاب.
        if not is_like:
            post.likes.add(request.user)
            notification = Notification.objects.create(notification_type=1, from_user=request.user, to_user=post.author, post=post)


        # إذا كان المستخدم قد قام بالإعجاب سابقًا، نزيل الإعجاب.
        if is_like:
            post.likes.remove(request.user)

        # نعيد توجيه المستخدم إلى الصفحة السابقة بعد إتمام العملية.
        next = request.POST.get('next', '/')
        return HttpResponseRedirect(next)


class AddDislike2(LoginRequiredMixin, View):
    def post(self, request, pk, *args, **kwargs):
        # الحصول على المنشور باستخدام المعرف (pk)
        post = Post.objects.get(pk=pk)

        # التحقق مما إذا كان المستخدم قد أعجب بالمنشور بالفعل
        is_like = False
        for like in post.likes.all():
            if like == request.user:
                is_like = True
                break

        # إذا كان المستخدم قد أعجب بالمنشور بالفعل، قم بإزالة الإعجاب
        if is_like:
            post.likes.remove(request.user)

        # التحقق مما إذا كان المستخدم قد أضاف "عدم إعجاب" (Dislike) للمنشور بالفعل
        is_dislike = False
        for dislike in post.dislikes.all():
            if dislike == request.user:
                is_dislike = True
                break

        # إذا لم يكن المستخدم قد أضاف "عدم إعجاب" بالفعل، قم بإضافته
        if not is_dislike:
            post.dislikes.add(request.user)

        # إذا كان المستخدم قد أضاف "عدم إعجاب" بالفعل، قم بإزالته
        if is_dislike:
            post.dislikes.remove(request.user)

        # إعادة التوجيه إلى الصفحة السابقة أو الصفحة الرئيسية
        next = request.POST.get('next', '/')
        return HttpResponseRedirect(next)
        
from django.http import JsonResponse, HttpResponseRedirect
from django.contrib.auth.mixins import LoginRequiredMixin
from django.views import View
from .models import Post, Notification

from django.shortcuts import get_object_or_404

class AddLike(LoginRequiredMixin, View):
    def post(self, request, pk, *args, **kwargs):
        post = get_object_or_404(Post, pk=pk)
        user = request.user

        if user in post.dislikes.all():
            post.dislikes.remove(user)

        liked = False
        if user in post.likes.all():
            post.likes.remove(user)
        else:
            post.likes.add(user)
            liked = True
            Notification.objects.create(
                notification_type=1,
                from_user=user,
                to_user=post.author,
                post=post
            )

        if request.headers.get('x-requested-with') == 'XMLHttpRequest':
            return JsonResponse({
                'likes_count': post.likes.count(),
                'dislikes_count': post.dislikes.count(),
                'liked': liked,
            })

        next_url = request.POST.get('next', '/')
        return HttpResponseRedirect(next_url)

class AddDislike(LoginRequiredMixin, View):
    def post(self, request, pk, *args, **kwargs):
        post = get_object_or_404(Post, pk=pk)
        user = request.user

        if user in post.likes.all():
            post.likes.remove(user)

        disliked = False
        if user in post.dislikes.all():
            post.dislikes.remove(user)
        else:
            post.dislikes.add(user)
            disliked = True
            Notification.objects.create(
                notification_type=3,
                from_user=user,
                to_user=post.author,
                post=post
            )

        if request.headers.get('x-requested-with') == 'XMLHttpRequest':
            return JsonResponse({
                'likes_count': post.likes.count(),
                'dislikes_count': post.dislikes.count(),
                'disliked': disliked,
            })

        next_url = request.POST.get('next', '/')
        return HttpResponseRedirect(next_url)




class LikePostAPI22(APIView):
    permission_classes = [IsAuthenticated]

    def post(self, request, pk, format=None):
        post = get_object_or_404(Post, pk=pk)
        user = request.user

        if user in post.dislikes.all():
            post.dislikes.remove(user)

        liked = False
        if user in post.likes.all():
            post.likes.remove(user)
        else:
            post.likes.add(user)
            liked = True
            Notification.objects.create(
                notification_type=1,
                from_user=user,
                to_user=post.author,
                post=post
            )
        serializer = PostSerializer(post, context={'request': request})
        return Response({
            'likes_count': post.likes.count(),
            'dislikes_count': post.dislikes.count(),
            'liked': liked
        }, status=status.HTTP_200_OK)

class DislikePostAPI22(APIView):
    permission_classes = [IsAuthenticated]

    def post(self, request, pk, format=None):
        post = get_object_or_404(Post, pk=pk)
        user = request.user

        if user in post.likes.all():
            post.likes.remove(user)

        disliked = False
        if user in post.dislikes.all():
            post.dislikes.remove(user)
        else:
            post.dislikes.add(user)
            disliked = True
            Notification.objects.create(
                notification_type=3,
                from_user=user,
                to_user=post.author,
                post=post
            )
        serializer = PostSerializer(post, context={'request': request})
        return Response({
            'likes_count': post.likes.count(),
            'dislikes_count': post.dislikes.count(),
            'disliked': disliked
        }, status=status.HTTP_200_OK)



# views.py

class LikePostAPI(APIView):
    permission_classes = [IsAuthenticated]

    def post(self, request, pk, format=None):
        post = get_object_or_404(Post, pk=pk)
        user = request.user

        if user in post.dislikes.all():
            post.dislikes.remove(user)

        liked = False
        if user in post.likes.all():
            post.likes.remove(user)
        else:
            post.likes.add(user)
            liked = True
            if user != post.author:
                Notification.objects.create(
                    notification_type=1,
                    from_user=user,
                    to_user=post.author,
                    post=post
                )

        serializer = PostSerializer(post, context={'request': request})
        return Response({
            'liked': liked,
            'disliked': False,
            'post': serializer.data,
        }, status=status.HTTP_200_OK)


class DislikePostAPI(APIView):
    permission_classes = [IsAuthenticated]

    def post(self, request, pk, format=None):
        post = get_object_or_404(Post, pk=pk)
        user = request.user

        if user in post.likes.all():
            post.likes.remove(user)

        disliked = False
        if user in post.dislikes.all():
            post.dislikes.remove(user)
        else:
            post.dislikes.add(user)
            disliked = True
            if user != post.author:
                Notification.objects.create(
                    notification_type=3,
                    from_user=user,
                    to_user=post.author,
                    post=post
                )

        serializer = PostSerializer(post, context={'request': request})
        return Response({
            'liked': False,
            'disliked': disliked,
            'post': serializer.data,
        }, status=status.HTTP_200_OK)

        

class AddCommentLikeview(LoginRequiredMixin, View):
    def post(self, request, pk, *args, **kwargs):
        comment = Comment.objects.get(pk=pk)

        is_dislike = False

        for dislike in comment.dislikes.all():
            if dislike == request.user:
                is_dislike = True
                break

        if is_dislike:
            comment.dislikes.remove(request.user)

        is_like = False

        for like in comment.likes.all():
            if like == request.user:
                is_like = True
                break

        if not is_like:
            comment.likes.add(request.user)
            #add
            notification = Notification.objects.create(notification_type=1, from_user=request.user, to_user=comment.author, comment=comment)

        if is_like:
            comment.likes.remove(request.user)

        next = request.POST.get('next', '/')
        return HttpResponseRedirect(next)

class AddCommentDislikeview(LoginRequiredMixin, View):
    def post(self, request, pk, *args, **kwargs):
        comment = Comment.objects.get(pk=pk)

        is_like = False

        for like in comment.likes.all():
            if like == request.user:
                is_like = True
                break

        if is_like:
            comment.likes.remove(request.user)

        is_dislike = False

        for dislike in comment.dislikes.all():
            if dislike == request.user:
                is_dislike = True
                break

        if not is_dislike:
            comment.dislikes.add(request.user)
            #add
            notification = Notification.objects.create(notification_type=3, from_user=request.user, to_user=comment.author, comment=comment)


        if is_dislike:
            comment.dislikes.remove(request.user)

        next = request.POST.get('next', '/')
        return HttpResponseRedirect(next)
        

from rest_framework.authentication import TokenAuthentication
from rest_framework.permissions import IsAuthenticated
from rest_framework.views import APIView
from rest_framework.response import Response

class AddCommentLike(APIView):
    #authentication_classes = [TokenAuthentication]
    permission_classes = [IsAuthenticated]

    def post(self, request, post_pk, pk, format=None):
        # pk هنا هو comment ID
        comment = Comment.objects.get(pk=pk)

        # إزالة dislike إذا موجود
        if request.user in comment.dislikes.all():
            comment.dislikes.remove(request.user)

        # Toggle like
        if request.user in comment.likes.all():
            comment.likes.remove(request.user)
            liked = False
        else:
            comment.likes.add(request.user)
            liked = True
            Notification.objects.create(
                notification_type=1,
                from_user=request.user,
                to_user=comment.author,
                comment=comment
            )

        serializer = CommentSerializer2(comment, context={'request': request})
        return Response({
            "liked": liked,
            "comment": serializer.data
        })


class AddCommentDislike(APIView):
    #authentication_classes = [TokenAuthentication]
    permission_classes = [IsAuthenticated]

    def post(self, request, post_pk, pk, format=None):
        comment = Comment.objects.get(pk=pk)

        if request.user in comment.likes.all():
            comment.likes.remove(request.user)

        if request.user in comment.dislikes.all():
            comment.dislikes.remove(request.user)
            disliked = False
        else:
            comment.dislikes.add(request.user)
            disliked = True
            Notification.objects.create(
                notification_type=3,
                from_user=request.user,
                to_user=comment.author,
                comment=comment
            )

        serializer = CommentSerializer2(comment, context={'request': request})
        return Response({
            "disliked": disliked,
            "comment": serializer.data
        })
                 

class SharedPostView(View):
    def post(self, request,pk, *args, **kwargs):
        original_post = Post.objects.get(pk=pk)
        form = ShareForm(request.POST)
        
        if form.is_valid():
            new_post = Post(
                shared_body=self.request.POST.get('body'),
                body = original_post.body,
                author = original_post.author,
                created_on = original_post.created_on,
                shared_user = request.user,
                shared_on = timezone.now(),
                
            ) 
            
            new_post.save()
            
            for img in original_post.image.all():
                new_post.image.add(img)
            new_post.save()
        return redirect('post-list')
        


class SharedPostAPIView(APIView):
    permission_classes = [IsAuthenticated]

    def post(self, request, pk):

        try:
            original_post = Post.objects.get(pk=pk)
        except Post.DoesNotExist:
            return Response(
                {"error": "Post not found"},
                status=status.HTTP_404_NOT_FOUND
            )

        shared_body = request.data.get("body", "")

        # إنشاء البوست الجديد
        new_post = Post.objects.create(
            shared_body=shared_body,
            body=original_post.body,
            author=original_post.author,
            created_on=original_post.created_on,
            shared_user=request.user,
            shared_on=timezone.now()
        )

        # نسخ الصور
        new_post.image.set(original_post.image.all())

        serializer = SharedPostSerializer(new_post)

        return Response(serializer.data, status=status.HTTP_201_CREATED)

            


class UserSearch(View):
    def get(self, request, *args, **kwargs):
        query = self.request.GET.get('query')
        profile_list = UserProfile.objects.filter(
            Q(user__first_name__icontains=query) | 
            Q(user__last_name__icontains=query) | 
            Q(user__email__icontains=query)
        ).exclude(user=request.user)

        context = {
            'profile_list': profile_list,
        }

        return render(request, 'social/search.html', context)
        
#class UserSearchAPI(View):

        
class UserSearchAPI(APIView):  # هنا نستخدم APIView
    def get(self, request, *args, **kwargs):
        query = request.GET.get('query', '')

        profiles = UserProfile.objects.filter(
            Q(user__first_name__icontains=query) |
            Q(user__last_name__icontains=query) |
            Q(user__email__icontains=query)
        ).exclude(user=request.user)

        results = []
        for profile in profiles:
            picture_url = request.build_absolute_uri(profile.picture.url) if profile.picture else None
            results.append({
                "id": profile.user.id,
                
                "first_name": profile.user.first_name,
                "last_name": profile.user.last_name,
                "email": profile.user.email,
                "bio": profile.bio,
                "birth_date": profile.birth_date,
                "location": profile.location,
                "picture": picture_url,
            })

        return Response({"profiles": results})                

        #return JsonResponse({"profiles": results}, safe=False)

class ListFollowers(View):
    def get(self, request, pk, *args, **kwargs):
        profile = UserProfile.objects.get(pk=pk)
        followers = profile.followers.all()

        context = {
            'profile': profile,
            'followers': followers,
        }

        return render(request, 'social/followers_list.html', context)
        
        
class FollowingListView(View):
    def get(self, request, pk):
        # ??????? ??? ??????? ????? ????????? ??????
        profile = get_object_or_404(UserProfile, pk=pk)
        
        # ??????? ????? ??????? ????? ??????? ????????
        following_list = UserProfile.objects.filter(followers=profile.user)
        
        # ????? ?????? ??????? ??? ??????
        context = {
            'profile': profile,
            'following_list': following_list,
        }
        
        return render(request, 'social/following_list.html', context)
        
        
class ListFollowersAPIView2(APIView):
    permission_classes = [permissions.IsAuthenticated]  # المستخدم يجب أن يكون مسجل دخول

    def get(self, request, *args, **kwargs):
        # نحصل على بروفايل المستخدم الحالي
        profile = get_object_or_404(UserProfile, user=request.user)

        # نحصل على جميع المتابعين
        followers = profile.followers.all()

        followers_data = []
        for follower in followers:
            picture_field = getattr(follower.profile, "picture", None)
            if picture_field and picture_field.url:
                picture_url = request.build_absolute_uri(picture_field.url)
            else:
                picture_url = None

            followers_data.append({
                "id": follower.id,
                "email": follower.email,
                "first_name": follower.first_name,
                "last_name": follower.last_name,
                "profile_picture": picture_url,
            })

        # نرجع النتيجة بصيغة JSON
        return Response({
            "user_id": request.user.id,
            "email": request.user.email,
            "first_name": request.user.first_name,
            "last_name": request.user.last_name,
            "followers_count": followers.count(),
            "followers": followers_data,
        }, status=status.HTTP_200_OK)
        

        




class ListFollowersAPIView(APIView):
    permission_classes = [permissions.IsAuthenticated]

    def get_current_user_followers(self, request):
        """عرض متابعي المستخدم الحالي"""
        profile = get_object_or_404(UserProfile, user=request.user)
        followers = profile.followers.all()

        data = []
        for follower in followers:
            profile_pic = getattr(follower.profile, "picture", None)
            picture_url = request.build_absolute_uri(profile_pic.url) if profile_pic and profile_pic.url else None

            data.append({
                "id": follower.id,
                "email": follower.email,
                "first_name": follower.first_name,
                "last_name": follower.last_name,
                "profile_picture": picture_url,
            })

        return Response({
            "user_id": request.user.id,
            "email": request.user.email,
            "first_name": request.user.first_name,
            "last_name": request.user.last_name,
            "followers_count": followers.count(),
            "followers": data,
        }, status=status.HTTP_200_OK)

    def get_other_user_followers(self, request, pk):
        """عرض متابعي مستخدم آخر عبر pk"""
        profile = get_object_or_404(UserProfile, pk=pk)
        followers = profile.followers.all()

        data = []
        for follower in followers:
            profile_pic = getattr(follower.profile, "picture", None)
            picture_url = request.build_absolute_uri(profile_pic.url) if profile_pic and profile_pic.url else None

            data.append({
                "id": follower.id,
                "email": follower.email,
                "first_name": follower.first_name,
                "last_name": follower.last_name,
                "profile_picture": picture_url,
            })

        return Response({
            "user_id": profile.user.id,
            "email": profile.user.email,
            "first_name": profile.user.first_name,
            "last_name": profile.user.last_name,
            "followers_count": followers.count(),
            "followers": data,
        }, status=status.HTTP_200_OK)

    def get(self, request, *args, **kwargs):
        pk = kwargs.get("pk")
        if pk:
            return self.get_other_user_followers(request, pk)
        return self.get_current_user_followers(request)




class FollowingListAPIView(APIView):
    permission_classes = [permissions.IsAuthenticated]

    def get_current_user_following(self, request):
        """قائمة المستخدمين الذين يتابعهم المستخدم الحالي"""
        profile = get_object_or_404(UserProfile, user=request.user)
        following_list = UserProfile.objects.filter(followers=profile.user)

        data = []
        for user_profile in following_list:
            picture = getattr(user_profile, "picture", None)
            picture_url = request.build_absolute_uri(picture.url) if picture and picture.url else None

            data.append({
                "id": user_profile.user.id,
                "email": user_profile.user.email,
                "first_name": user_profile.user.first_name,
                "last_name": user_profile.user.last_name,
                "profile_picture": picture_url,
            })

        return Response({
            "user_id": request.user.id,
            "email": profile.user.email,
            "first_name": profile.user.first_name,
            "last_name": profile.user.last_name,
            "following_count": following_list.count(),
            "following": data,
        }, status=status.HTTP_200_OK)

    def get_other_user_following(self, request, pk):
        """قائمة المستخدمين الذين يتابعهم مستخدم آخر عبر pk"""
        profile = get_object_or_404(UserProfile, pk=pk)
        following_list = UserProfile.objects.filter(followers=profile.user)

        data = []
        for user_profile in following_list:
            picture = getattr(user_profile, "picture", None)
            picture_url = request.build_absolute_uri(picture.url) if picture and picture.url else None

            data.append({
                "id": user_profile.user.id,
                "email": user_profile.user.email,
                "first_name": user_profile.user.first_name,
                "last_name": user_profile.user.last_name,
                "profile_picture": picture_url,
            })

        return Response({
            "user_id": profile.user.id,
            "email": profile.user.email,
            "first_name": profile.user.first_name,
            "last_name": profile.user.last_name,
            "following_count": following_list.count(),
            "following": data,
        }, status=status.HTTP_200_OK)

    def get(self, request, *args, **kwargs):
        pk = kwargs.get("pk")
        if pk:
            return self.get_other_user_following(request, pk)
        return self.get_current_user_following(request)



class PostNotification(View):
    def get(self,request,notification_pk,post_pk,*args,**kwargs):
        notification = Notification.objects.get(pk = notification_pk)
        post = Post.objects.get(pk=post_pk)
        
        notification.user_has_seen =True
        notification.save()
        
        return redirect('post-detail',pk=post_pk)
        


class PostNotificationAPIView(APIView):
    def get(self, request, notification_pk, post_pk):
        notification = get_object_or_404(Notification, pk=notification_pk)
        post = get_object_or_404(Post, pk=post_pk)

        notification.user_has_seen = True
        notification.save()

        serializer = NotificationPostSerializer(notification, context={'request': request})
        return Response(serializer.data, status=status.HTTP_200_OK)

        
        
        
class FollowNotification(View):
    def get(self,request,notification_pk,profile_pk,*args,**kwargs):
        notification = Notification.objects.get(pk = notification_pk)
        profile = UserProfile.objects.get(pk=profile_pk)
        
        notification.user_has_seen =True
        notification.save()
        
        return redirect('profile',pk=profile_pk)
        
class FollowNotificationApi(APIView):
    def get(self, request, notification_pk, profile_pk, *args, **kwargs):
        notification = get_object_or_404(Notification, pk=notification_pk)
        profile = get_object_or_404(UserProfile, pk=profile_pk)
        
        notification.user_has_seen = True
        notification.save()
        
        serializer = FollowNotificationSerializer(notification, context={'request': request})
        return Response(serializer.data, status=status.HTTP_200_OK)

        
        
class ThreadNotification2(View):
    def get(self, request, notification_pk, object_pk, *args, **kwargs):
        notification = Notification.objects.get(pk=notification_pk)
        thread = ThreadModel.objects.get(pk=object_pk)

        notification.user_has_seen = True
        notification.save()

        return redirect('thread', pk=object_pk)
        
class ThreadNotification(View):
    def get(self, request, notification_pk, thread_pk, *args, **kwargs):
        notification = get_object_or_404(Notification, pk=notification_pk)
        thread = get_object_or_404(ThreadModel, pk=thread_pk)

        notification.user_has_seen = True
        notification.save()

        return redirect('thread', pk=thread_pk)

        
class ThreadNotificationApi(APIView):
    def get(self, request, notification_pk, thread_pk, *args, **kwargs):
        notification = get_object_or_404(Notification, pk=notification_pk)
        thread = get_object_or_404(ThreadModel, pk=thread_pk)
        
        notification.user_has_seen = True
        notification.save()
        
        serializer = ThreadNotificationSerializer(notification, context={'request': request})
        return Response(serializer.data, status=status.HTTP_200_OK)

        
        
        
        
        
class RemoveNotification(View):
    def delete(self, request, notification_pk, *args, **kwargs):
        notification = Notification.objects.get(pk=notification_pk)

        notification.user_has_seen = True
        notification.save()

        return HttpResponse('Success', content_type='text/plain')        
        

class RemoveNotification2(View):
    def delete(self, request, notification_pk, *args, **kwargs):
        try:
            # جلب الإشعار حسب المعرف
            notification = Notification.objects.get(pk=notification_pk)
            
            # وضع علامة أنه تم مشاهدته
            notification.user_has_seen = True
            notification.save()
            
            # إرسال رد نجاح
            return HttpResponse('Notification marked as seen', content_type='text/plain', status=200)
        
        except Notification.DoesNotExist:
            # إذا لم يتم العثور على الإشعار
            return HttpResponseNotFound('Notification not found')
            
class RemoveNotificationApi(APIView):
    def delete (self,request,notification_pk,*args, **kwargs):
        try:
            notification = Notification.objects.get(pk=notification_pk)

            notification.user_has_seen = True
            notification.save()
            
            # إرسال رد نجاح
            return Response(
                {'message': 'Notification marked as seen'},
                status=status.HTTP_200_OK
            )
        
        except Notification.DoesNotExist:
            # إذا لم يتم العثور على الإشعار
            return Response(
                {'error': 'Notification not found'},
                status=status.HTTP_404_NOT_FOUND
            )
            
class RemoveNotificationAPIView2(APIView):
    """
    API endpoint لحذف إشعار معين (حذف فعلي من قاعدة البيانات)
    """
    permission_classes = [IsAuthenticated]  # ✅ هنا داخل الكلاس

    def delete(self, request, notification_pk, *args, **kwargs):
        try:
            # 🔒 التحقق أن الإشعار يعود للمستخدم الحالي
            notification = Notification.objects.get(pk=notification_pk, to_user=request.user)

            notification.delete()

            return Response(
                {"message": "Notification deleted successfully"},
                status=status.HTTP_204_NO_CONTENT
            )

        except Notification.DoesNotExist:
            # إذا لم يتم العثور على الإشعار أو لا يتبع المستخدم الحالي
            return Response(
                {"error": "Notification not found or not yours"},
                status=status.HTTP_404_NOT_FOUND
            )
        
        
class ListThreads(View):
    def get(self, request, *args, **kwargs):
        user = request.user
        threads = ThreadModel.objects.filter(Q(user=request.user) | Q(receiver=request.user))
        #add
        for thread in threads:
            if thread.user == user:
                thread.other_user = thread.receiver
            else:
                thread.other_user = thread.user
                ##
                
            #add
            unread_count = MessageModel.objects.filter(
                thread=thread,
                is_read=False
            ).exclude(sender_user=user).count()

            thread.unread_count = unread_count

        context = {
            'threads': threads
        }

        return render(request, 'social/inbox.html', context)

class CreateThread2(View):
    def get(self, request, *args, **kwargs):
        form = ThreadForm()

        context = {
            'form': form
        }

        return render(request, 'social/create_thread.html', context)

    def post(self, request, *args, **kwargs):
        form = ThreadForm(request.POST)

        email = request.POST.get('email')

        try:
            receiver = User.objects.get(username=username)
            if ThreadModel.objects.filter(user=request.user, receiver=receiver).exists():
                thread = ThreadModel.objects.filter(user=request.user, receiver=receiver)[0]
                return redirect('thread', pk=thread.pk)
            elif ThreadModel.objects.filter(user=receiver, receiver=request.user).exists():
                thread = ThreadModel.objects.filter(user=receiver, receiver=request.user)[0]
                return redirect('thread', pk=thread.pk)

            if form.is_valid():
                thread = ThreadModel(
                    user=request.user,
                    receiver=receiver
                )
                thread.save()

                return redirect('thread', pk=thread.pk)
        except:
            messages.error(request, 'Invalid username')
            return redirect('create-thread')
            
from django.views import View
from django.shortcuts import redirect
from django.contrib import messages
from django.db.models import Q
from .models import ThreadModel
from django.contrib.auth import get_user_model

User = get_user_model()

class CreateThread(View):
    def get(self, request, *args, **kwargs):
        form = ThreadForm()

        context = {
            'form': form
        }

        return render(request, 'social/create_thread.html', context)

    def post(self, request, *args, **kwargs):
        search_input = request.POST.get('search_input')  # البريد أو الاسم

        if not search_input:
            messages.error(request, "لم يتم تحديد المستخدم")
            return redirect('create-thread')

        try:
            # البحث باستخدام first_name, last_name, email
            receiver = User.objects.filter(
                Q(first_name__icontains=search_input) |
                Q(last_name__icontains=search_input) |
                Q(email__icontains=search_input)
            ).first()

            if not receiver:
                messages.error(request, "المستخدم غير موجود")
                return redirect('create-thread')

            if receiver == request.user:
                messages.error(request, "لا يمكنك فتح محادثة مع نفسك")
                return redirect('create-thread')

            # تحقق إذا المحادثة موجودة مسبقًا
            thread = ThreadModel.objects.filter(
                Q(user=request.user, receiver=receiver) |
                Q(user=receiver, receiver=request.user)
            ).first()

            if thread:
                return redirect('thread', pk=thread.pk)

            # إنشاء محادثة جديدة
            thread = ThreadModel(user=request.user, receiver=receiver)
            thread.save()
            return redirect('thread', pk=thread.pk)

        except Exception as e:
            messages.error(request, f"حدث خطأ: {str(e)}")
            return redirect('create-thread')

                
        
        
class ThreadView(View):
    def get(self,request,pk,*args,**kwargs):
        form = MessageForm()
        thread =ThreadModel.objects.get(pk=pk)
        
        #
                # ????? ???? ????? ??????? ???? ??????? ???????? ??????.
        MessageModel.objects.filter(receiver_user=request.user, thread=thread).update(is_read=True)
        
        
        
        message_list = MessageModel.objects.filter(thread__pk__contains=pk).order_by('date')
        
        context = {
            'thread':thread,
            'form':form,
            'message_list':message_list
        }
        
        return render(request,'social/thread.html',context)        
        
        
        
        
        
        
      
        
        
        
class UpdateReadStatusView(LoginRequiredMixin, View):
    def post(self, request, pk):
        thread = get_object_or_404(ThreadModel, pk=pk)
        # ????? ???? ??????? ??? ??????? ???? ??????? ???????? ?????? (????????)
        MessageModel.objects.filter(
            receiver_user=request.user,
            thread=thread,
            is_read=False
        ).update(is_read=True)
        return JsonResponse({'status': 'success'})
        


            
from django.http import JsonResponse

from django.shortcuts import redirect

class CreateMessage(View):
    def post(self, request, pk):
        form = MessageForm(request.POST, request.FILES)
        thread = get_object_or_404(ThreadModel, pk=pk)

        receiver = thread.receiver if thread.receiver != request.user else thread.user

        if form.is_valid():
            message = form.save(commit=False)
            message.thread = thread
            message.sender_user = request.user
            message.receiver_user = receiver
            message.save()

            Notification.objects.create(
                notification_type=4,
                from_user=request.user,
                to_user=receiver,
                thread=thread
            )

            return redirect('thread', pk=thread.pk)  # ???? 'thread-view' ???? url ??????
        # ??? ??? ???? ??? ?? ??????? ???? ????? ????? ?? ??????? ?? ??? ???
        # ????:
        context = {'form': form, 'thread': thread, 'errors': form.errors}
        return render(request, 'social/thread.html', context)


        



class Explore(View):

        
    def get(self, request, *args, **kwargs):
        explore_form = ExploreForm()
        query = request.GET.get('query', '').strip()

        if query:
        # ??? ?? ?????? ???? ????? ??? ???? ????? (??? ?? ?????)
            tags = Tag.objects.filter(name__icontains=query)
        # ??? ?? ????????? ???? ????? ??? ?? ?? ??? ??????
            posts = Post.objects.filter(tags__in=tags).distinct()
        else:
            tags = Tag.objects.none()  # ?????? ????? ??? ?? ???????
            posts = Post.objects.all()

        context = {
            'tags': tags if query else None,
            'posts': posts,
            'explore_form': explore_form,
            'no_results': not posts.exists() and not query,
        }
        return render(request, 'social/explore.html', context)


    def post(self, request, *args, **kwargs):
        explore_form = ExploreForm(request.POST)
        if explore_form.is_valid():
            query = explore_form.cleaned_data['query'].strip()  # ????? ???????? ???????
            if query:
                tag = Tag.objects.filter(name__icontains=query).first()
                if tag:
                    posts = Post.objects.filter(tags__in=[tag])

                    if not posts.exists():
                        messages.error(request, 'No posts found for this tag.')
                        return redirect('explore')  # ????? ??????? ??? ???? ?????
                else:
                    messages.error(request, 'Tag not found.')
                    return redirect('explore')  # ????? ??????? ??? ???? ?????
                
                # ????? ??????? ?? ????????? ?? URL
                return redirect(f'/social/explore?query={query}')
            else:
                messages.error(request, 'Invalid search query.')
                return redirect('explore')
        
        # ??? ???? ???????? ??????? ?? ??????? ??? ?????
        messages.error(request, 'Invalid search query.')
        return redirect('explore')



class Explores(APIView):
    
    def get(self, request, format=None):
        query = request.GET.get('query', '').strip()
    
        if query:
            tags = Tag.objects.filter(name__icontains=query)
            posts = Post.objects.filter(tags__in=tags).distinct()
        else:
            posts = Post.objects.exclude(tags=None)

        posts_with_tags = [post for post in posts if post.tags.exists()]

    # تمرير request للـ serializer
        posts_data = PostTagsSerializer(
            posts_with_tags, 
            many=True, 
            context={'request': request}  # ✅ هذا يضمن أن author_image يعطي رابط كامل
        ).data

        return Response({
            'posts': posts_data,
            'no_results': not posts_with_tags and not query
        })

        
    def post(self, request, format=None):
        query = request.data.get('query', '').strip()
        if not query:
            return Response({'error': 'Invalid search query.'}, status=status.HTTP_400_BAD_REQUEST)

        tag = Tag.objects.filter(name__icontains=query).first()
        if not tag:
            return Response({'error': 'Tag not found.'}, status=status.HTTP_404_NOT_FOUND)

        posts = Post.objects.filter(tags__in=[tag])
        #posts_data = PostSerializer(posts, many=True).data
        posts_data = PostSerializer(posts, many=True, context={'request': request}).data

        return Response({
            'query': query,
            'posts': posts_data,
            'no_results': not posts.exists()
        }, status=status.HTTP_200_OK)


class Explores2(APIView):
    def get(self, request, format=None):
        query = request.GET.get('query', '').strip()
        if query:
            tags = Tag.objects.filter(name__icontains=query)
        else:
            tags = Tag.objects.all()

        serializer = TagSerializer(tags, many=True , context={'request': request})
        return Response(serializer.data)

    def post(self, request, format=None):
        query = request.data.get('query', '').strip()
        if not query:
            return Response({'error': 'Invalid search query.'}, status=400)

        tag = Tag.objects.filter(name__icontains=query).first()
        if not tag:
            return Response({'error': 'Tag not found.'}, status=404)

        serializer = TagSerializer(tag, context={'request': request})
        return Response(serializer.data, status=200)
        

class Explore222(APIView):
    def get(self, request, format=None):
        query = request.GET.get('query', '').strip()
        
        if query:
            tags = Tag.objects.filter(name__icontains=query)
            posts = Post.objects.filter(tags__in=tags).distinct()
        else:
            tags = Tag.objects.none()
            #posts = Post.objects.all()
            posts = Post.objects.exclude(tags=None)

        # تمرير request في context لتجنب NoneType
        tags_data = TagSerializer222(tags, many=True).data
        posts_data = PostSerializer222(posts, many=True, context={'request': request}).data  # <<< هنا

        return Response({
            'tags': tags_data,
            'posts': posts_data,
            'no_results': not posts.exists() and not query
        }, status=status.HTTP_200_OK)
        
    def post(self, request, format=None):
        query = request.data.get('query', '').strip()
        if not query:
            return Response({'error': 'Invalid search query.'}, status=400)

        tag = Tag.objects.filter(name__icontains=query).first()
        if not tag:
            return Response({'error': 'Tag not found.'}, status=404)

        posts = Post.objects.filter(tags__in=[tag])
        posts_data = PostSerializer222(posts, many=True, context={'request': request}).data

        return Response({
            'query': query,
            'posts': posts_data,
            'no_results': not posts.exists()
        }, status=status.HTTP_200_OK)




def get_suggestionsa(user, limit=10):
    followings = user.profile.followers.all()  # ????????? ???????? ????????
    suggestions = User.objects.exclude(pk__in=followings).exclude(pk=user.pk).order_by("?")[:limit]  # ??????? ?????????? ????????? ????????
    return suggestions
    
def get_suggestions(user, limit=10):
    # احصل على من يتابعهم المستخدم
    my_following = user.following.all()

    # احصل على من يتابعون المستخدم
    my_followers = User.objects.filter(profile__followers=user)

    # استبعد المستخدمين الذين يتابعهم المستخدم أو يتابعونه
    suggestions = User.objects.exclude(pk__in=my_following) \
                              .exclude(pk__in=my_followers) \
                              .exclude(pk=user.pk) \
                              .order_by('?')[:limit]

    return suggestions



def suggestions_view(request):
    suggestions = get_suggestions(request.user)  # ??????? ?????? ???? ???????? ??????????
    return render(request, 'social/suggestions.html', {'suggestions': suggestions})  # ????? ?????????? ??? ??????
    

class SuggestionsApiView(APIView):
    permission_classes = [IsAuthenticated]

    def get(self, request, format=None):
        suggestions = get_suggestions(request.user, limit=10)
        serializer = UserSuggestionSerializer(
            suggestions, many=True, context={'request': request}  # ✅ تمرير request
        )
        return Response(serializer.data)



#    def get(self, request, *args, **kwargs):
 #       explore_form = ExploreForm()
  #      query = self.request.GET.get('query', '').strip()  # ????? ???????? ???????

   #     if query:  # ??? ??? ???? ???????
    #        tag = Tag.objects.filter(name__icontains=query).first()
     #       if tag:
      #          posts = Post.objects.filter(tags__in=[tag])
       #     else:
        #        posts = Post.objects.all()  # ?? ??? ?? ??? ?????? ??? ??? ?????
       # else:
        #    posts = Post.objects.all()  # ??? ?? ??? ???? ???????? ??? ?? ?????????

        #context = {
         #   'tag': tag if query else None,  # ????? ????? ??? ??? ??? ???? ???????
          #  'posts': posts,
           # 'explore_form': explore_form,
            #'no_results': not posts.exists() and not query,  # ??? ?? ??? ???? ?????
       # }

        #return render(request, 'social/explore.html', context)






###api
class CustomRegisterView(RegisterView):
    serializer_class = CustomRegisterSerializer




class CustomLoginView2(LoginView):
    serializer_class = CustomLoginSerializer
    
from rest_framework.authtoken.models import Token
from rest_framework.response import Response
from rest_framework import status

class CustomLoginView(LoginView):
    serializer_class = CustomLoginSerializer

    def post(self, request, *args, **kwargs):
        serializer = self.get_serializer(data=request.data, context={'request': request})
        serializer.is_valid(raise_exception=True)
        user = serializer.validated_data['user']

        # احصل على التوكن أو انشئه إذا لم يكن موجودًا
        token, created = Token.objects.get_or_create(user=user)

        # هنا تعيد التوكن في الاستجابة
        return Response({
            "token": token.key,
            "user_id": user.id,
            "email": user.email,
            "first_name": user.first_name,
            "last_name": user.last_name,
        }, status=status.HTTP_200_OK)

    

    




class LogoutView(APIView):
    permission_classes = [permissions.IsAuthenticated]

    def post(self, request):
        #try:
            #request.user.auth_token.delete()
        #except (AttributeError, Token.DoesNotExist):
          #  return Response({"detail": "Token not found or already deleted."}, status=status.HTTP_400_BAD_REQUEST)
        return Response({"detail": "Successfully logged out."}, status=status.HTTP_200_OK)

        
class LogoutView2(APIView):
    authentication_classes = [TokenAuthentication]
    permission_classes = [IsAuthenticated]

    def post(self, request):
        #request.user.auth_token.delete()  # حذف التوكن من قاعدة البيانات
        return Response({"message": "Logged out successfully."}, status=status.HTTP_200_OK)





from rest_framework import generics, permissions
from .models import CustomUser
from .serializers import UserProfileSerializer
from rest_framework.response import Response
from rest_framework.views import APIView
from django.shortcuts import get_object_or_404

# ? ??? ??????? ???????? ??????
class CurrentUserProfileView(APIView):
    permission_classes = [permissions.IsAuthenticated]

    def get(self, request):
        serializer = UserProfileSerializer(request.user)
        return Response(serializer.data)

# ? ??? ??????? ?? ?????? ??? ID ?? email
class UserProfileDetailView(generics.RetrieveAPIView):
    queryset = CustomUser.objects.all()
    serializer_class = UserProfileSerializer
    permission_classes = [permissions.AllowAny]  # ?? IsAuthenticated ??? ??? ????? ???
    lookup_field = 'pk'  # ?? 'email' ?? ???? ?????? ??????










class AllNotificationsApi2(APIView):
    permission_classes = [permissions.IsAuthenticated]

    def get(self, request):
        user = request.user
        notifications = Notification.objects.filter(to_user=user).order_by('-date')
        serializer = NotificationSerializer2(notifications, many=True, context={'request': request})
        return Response(serializer.data, status=status.HTTP_200_OK)


class AllNotificationsApi(APIView):
    permission_classes = [permissions.IsAuthenticated]

    def get(self, request):
        user = request.user
        notifications = Notification.objects.filter(to_user=user).order_by('-date')
        serializer = NotificationSerializer3(notifications, many=True, context={'request': request})
        return Response(serializer.data)
        

            



class ProfileAPIView2(APIView):

    def get_current_user_profile(self, request):
        """المستخدم الحالي: الملف الشخصي + المنشورات + عدد المتابعين والمتابعين"""
        if not request.user.is_authenticated:
            return Response({'detail': 'Authentication required.'}, status=status.HTTP_401_UNAUTHORIZED)

        profile = get_object_or_404(UserProfile, user=request.user)
        user = profile.user

        # المنشورات
        posts = Post.objects.filter(author=user).order_by('-created_on')

        data = {
            'user': UserSerializer(user).data,
            'profile': UserProfileSerializer2(profile, context={'request': request}).data,
            'posts': PostSerializer222(posts, many=True, context={'request': request}).data,
        }

        return Response(data, status=status.HTTP_200_OK)

    def get_other_user_profile(self, request, pk):
        """مستخدم آخر: الملف الشخصي + المنشورات + عدد المتابعين والمتابعين"""
        profile = get_object_or_404(UserProfile, pk=pk)
        user = profile.user

        posts = Post.objects.filter(author=user).order_by('-created_on')

        is_following = request.user in profile.followers.all() if request.user.is_authenticated else False

        data = {
            'user': UserSerializer(user).data,
            'profile': UserProfileSerializer2(profile, context={'request': request}).data,
            'posts': PostSerializer222(posts, many=True, context={'request': request}).data,
            'is_following': is_following,
        }

        return Response(data, status=status.HTTP_200_OK)

    def post(self, request, pk, *args, **kwargs):
        """Follow / Unfollow لمستخدم آخر"""
        if not request.user.is_authenticated:
            return Response({'detail': 'Authentication required.'}, status=status.HTTP_401_UNAUTHORIZED)

        profile = get_object_or_404(UserProfile, pk=pk)
        user = profile.user

        if request.user == user:
            return Response({'detail': 'Cannot follow/unfollow yourself.'}, status=status.HTTP_400_BAD_REQUEST)

        if request.user in profile.followers.all():
            profile.followers.remove(request.user)
            action = 'unfollowed'
        else:
            profile.followers.add(request.user)
            action = 'followed'

        number_of_followers = profile.followers.count()
        is_following = request.user in profile.followers.all()

        return Response({
            'action': action,
            'number_of_followers': number_of_followers,
            'is_following': is_following
        }, status=status.HTTP_200_OK)

    def get(self, request, pk=None, *args, **kwargs):
        """GET endpoint"""
        if pk is None:
            return self.get_current_user_profile(request)
        else:
            return self.get_other_user_profile(request, pk)
            
            

class ProfileAPIView(APIView):

    def get_current_user_profile(self, request):
        """المستخدم الحالي: الملف الشخصي + المنشورات + عدد المتابعين والمتابعين"""
        if not request.user.is_authenticated:
            return Response({'detail': 'Authentication required.'}, status=status.HTTP_401_UNAUTHORIZED)

        profile = get_object_or_404(UserProfile, user=request.user)
        user = profile.user

        # جلب المنشورات مباشرة بدون related_name
        posts = Post.objects.filter(author=user).order_by('-created_on')

        data = {
            'id': user.id,
            'user': UserSerializer(user).data,
            'profile': UserProfileSerializer2(profile, context={'request': request}).data,
            'posts': PostSerializer222(posts, many=True, context={'request': request}).data,
        }

        return Response(data, status=status.HTTP_200_OK)

    def get_other_user_profile(self, request, pk):
        """مستخدم آخر: الملف الشخصي + المنشورات + عدد المتابعين والمتابعين"""
        profile = get_object_or_404(UserProfile, pk=pk)
        user = profile.user

        posts = Post.objects.filter(author=user).order_by('-created_on')

        is_following = request.user in profile.followers.all() if request.user.is_authenticated else False

        data = {
            'user': UserSerializer(user).data,
            'profile': UserProfileSerializer2(profile, context={'request': request}).data,
            'posts': PostSerializer222(posts, many=True, context={'request': request}).data,
            'is_following': is_following,
        }

        return Response(data, status=status.HTTP_200_OK)

    def post(self, request, pk, *args, **kwargs):
        """Follow / Unfollow لمستخدم آخر"""
        if not request.user.is_authenticated:
            return Response({'detail': 'Authentication required.'}, status=status.HTTP_401_UNAUTHORIZED)

        profile = get_object_or_404(UserProfile, pk=pk)
        user = profile.user

        if request.user == user:
            return Response({'detail': 'Cannot follow/unfollow yourself.'}, status=status.HTTP_400_BAD_REQUEST)

        if request.user in profile.followers.all():
            profile.followers.remove(request.user)
            action = 'unfollowed'
        else:
            profile.followers.add(request.user)
            action = 'followed'

        number_of_followers = profile.followers.count()
        is_following = request.user in profile.followers.all()

        return Response({
            'action': action,
            'number_of_followers': number_of_followers,
            'is_following': is_following
        }, status=status.HTTP_200_OK)

    def get(self, request, pk=None, *args, **kwargs):
        """GET endpoint"""
        if pk is None:
            return self.get_current_user_profile(request)
        else:
            return self.get_other_user_profile(request, pk)





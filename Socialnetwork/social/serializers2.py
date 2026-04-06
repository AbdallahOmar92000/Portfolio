print("?? CustomRegisterSerializer loaded!")
from rest_framework import serializers
from .models import Post, Image, Tag
#from django.contrib.auth.models import User
from dj_rest_auth.registration.serializers import RegisterSerializer
from rest_framework import serializers
from django.contrib.auth import authenticate
from rest_framework import serializers
from dj_rest_auth.registration.serializers import RegisterSerializer
from rest_framework import serializers
import datetime
import re
from .models import CustomUser as User
from rest_framework import serializers
from .models import CustomUser
from .models import *
from rest_framework import serializers
from django.conf import settings



class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = CustomUser
        fields = ['id', 'email', 'first_name', 'last_name', 'birth_date']  # بدون profile_pic
        
        
    def get_gender(self, obj):
        if obj.gender:
            return obj.get_gender_display()  # "Male", "Female", "Other"
        return None

class UserProfileSerializer2(serializers.ModelSerializer):
    user = UserSerializer(read_only=True)
    picture = serializers.SerializerMethodField()  # صورة الملف الشخصي من UserProfile
    followers_count = serializers.SerializerMethodField()
    following_count = serializers.SerializerMethodField()
    gender = serializers.SerializerMethodField()    # من CustomUser
    location = serializers.SerializerMethodField()  # من UserProfile

    class Meta:
        model = UserProfile
        fields = ['user', 'name', 'bio', 'picture', 'followers_count', 'following_count', 'gender', 'location']

    def get_picture(self, obj):
        request = self.context.get('request')
        if obj.picture:
            if request:
                return request.build_absolute_uri(obj.picture.url)
            else:
                return obj.picture.url
        return None

    def get_followers_count(self, obj):
        return obj.followers.count()

    def get_following_count(self, obj):
        return obj.user.following.count()  # related_name من الـ M2M
        
    def get_gender(self, obj):
        if obj.user and obj.user.gender:
            return obj.user.get_gender_display()  # يعطي "Male", "Female", "Other"
        return None

    def get_location(self, obj):
        return obj.location if obj.location else "Unknown"
 


class CommentSerializer2(serializers.ModelSerializer):
    author = UserProfileSerializer2(source='author.profile', read_only=True)
    image_url = serializers.SerializerMethodField()
    replies = serializers.SerializerMethodField()
    likes_count = serializers.SerializerMethodField()
    dislikes_count = serializers.SerializerMethodField()
    is_liked_by_user = serializers.SerializerMethodField()
    is_disliked_by_user = serializers.SerializerMethodField()
   
    class Meta:
        model = Comment
        fields = [
            'id',
            'comment',
            'image',
            'image_url',
            'author',
            'created_on',
            'likes_count',
            'dislikes_count',
            'replies',
            'is_liked_by_user',
            'is_disliked_by_user',
        ]

    # 🔥 إرجاع رابط الصورة كامل
    def get_image_url(self, obj):
        request = self.context.get('request')
        if obj.image:
            if request:
                return request.build_absolute_uri(obj.image.url)
            return obj.image.url
        return None

    # 🔁 الردود (children) بشكل متكرر
    def get_replies(self, obj):
        children = obj.children
        if children.exists():
            return CommentSerializer2(
                children,
                many=True,
                context=self.context
            ).data
        return []

    # 👍 عدد الإعجابات
    def get_likes_count(self, obj):
        return obj.likes.count()

    # 👎 عدد الديسلايك
    def get_dislikes_count(self, obj):
        return obj.dislikes.count()

    # ✅ هل المستخدم الحالي قام باللايك؟
    def get_is_liked_by_user(self, obj):
        request = self.context.get('request', None)
        if request and request.user.is_authenticated:
            return obj.likes.filter(id=request.user.id).exists()
        return False

    # ❌ هل المستخدم الحالي قام بالديسلايك؟
    def get_is_disliked_by_user(self, obj):
        request = self.context.get('request', None)
        if request and request.user.is_authenticated:
            return obj.dislikes.filter(id=request.user.id).exists()
        return False





class FollowerSerializer(serializers.Serializer):
    """
    Serializer لإرجاع حالة متابعة المستخدم الحالي لملف شخصي معين.
    """
    profile_id = serializers.IntegerField(read_only=True)   # الـ pk الخاص بالملف الشخصي
    is_following = serializers.BooleanField(read_only=True) # هل يتابع المستخدم الحالي هذا الملف؟


class NotificationPostSerializer(serializers.ModelSerializer):
    from_user_username = serializers.CharField(source='from_user.first_name', read_only=True)
    to_user_username = serializers.CharField(source='to_user.first_name', read_only=True)
    from_user_profile_pic = serializers.SerializerMethodField()
    message = serializers.SerializerMethodField()

    class Meta:
        model = Notification
        fields = [
            'id',
            'notification_type',
            'from_user',
            'from_user_username',
            'from_user_profile_pic',
            'to_user',
            'to_user_username',
            'post',
            'comment',
            'thread',
            'user_has_seen',
            'date',
            'message',
        ]

    def get_from_user_profile_pic(self, obj):
        request = self.context.get('request')
        if obj.from_user.profile.picture:
            return request.build_absolute_uri(obj.from_user.profile.picture.url)
        return None

    def get_message(self, obj):
        if obj.notification_type == 1 and obj.post:  # Like
            return f"{obj.from_user.first_name} liked your post."
        elif obj.notification_type == 2 and obj.comment:  # Comment
            return f"{obj.from_user.first_name} commented on your post."
        elif obj.notification_type == 3:  # Follow
            return f"{obj.from_user.first_name} started following you."
        elif obj.thread:
            return f"{obj.from_user.first_name} sent you a message."
        return "You have a new notification."
        
        


class NotificationSerializer2(serializers.ModelSerializer):
    from_user_username = serializers.CharField(source='from_user.first_name', read_only=True)
    to_user_username = serializers.CharField(source='to_user.first_name', read_only=True)
    from_user_profile_pic = serializers.ImageField(source='from_user.profile.picture', read_only=True)
    message = serializers.SerializerMethodField()
    target_id = serializers.SerializerMethodField()  # id ??????? ?? ??????? ?? ????????

    class Meta:
        model = Notification
        fields = [
            'id',
            'notification_type',
            'from_user',
            'from_user_username',
            'from_user_profile_pic',
            'to_user',
            'to_user_username',
            'post',
            'comment',
            'thread',
            'user_has_seen',
            'date',
            'message',
            'target_id',
        ]

    def get_message(self, obj):
        """????? ????? ?????? ??? ??? ?????"""
        if obj.notification_type == 1:  # Like
            if obj.post:
                return f"{obj.from_user.first_name} liked your post."
            elif obj.comment:
                return f"{obj.from_user.first_name} liked your comment."
        elif obj.notification_type == 2:  # Comment
            if obj.post:
                return f"{obj.from_user.first_name} commented on your post."
            elif obj.comment:
                return f"{obj.from_user.first_name} replied to your comment."
        elif obj.notification_type == 3:  # Follow
            return f"{obj.from_user.first_name} started following you."
        elif obj.thread:
            return f"{obj.from_user.first_name} sent you a message."
        return "You have a new notification."

    def get_target_id(self, obj):
        """????? id ????? ????? ?? ??????"""
        if obj.post:
            return obj.post.pk
        elif obj.comment:
            return obj.comment.pk
        elif obj.thread:
            return obj.thread.pk
        elif obj.notification_type == 3:
            return obj.from_user.profile.pk
        return None



class FollowNotificationSerializer(serializers.ModelSerializer):
    from_user_username = serializers.CharField(source='from_user.first_name', read_only=True)
    to_user_username = serializers.CharField(source='to_user.first_name', read_only=True)
    from_user_profile_pic = serializers.SerializerMethodField()
    message = serializers.SerializerMethodField()

    class Meta:
        model = Notification
        fields = [
            'id',
            'notification_type',
            'from_user',
            'from_user_username',
            'from_user_profile_pic',
            'to_user',
            'to_user_username',
            'user_has_seen',
            'date',
            'message',
        ]

    def get_from_user_profile_pic(self, obj):
        request = self.context.get('request')
        if obj.from_user.profile.picture:
            return request.build_absolute_uri(obj.from_user.profile.picture.url)
        return None

    def get_message(self, obj):
        return f"{obj.from_user.first_name} started following you."


class ThreadNotificationSerializer(serializers.ModelSerializer):
    from_user_username = serializers.CharField(source='from_user.first_name', read_only=True)
    to_user_username = serializers.CharField(source='to_user.first_name', read_only=True)
    from_user_profile_pic = serializers.SerializerMethodField()
    thread_id = serializers.IntegerField(source='thread.id', read_only=True)
    last_message = serializers.SerializerMethodField()

    class Meta:
        model = Notification
        fields = [
            'id',
            'notification_type',
            'from_user',
            'from_user_username',
            'from_user_profile_pic',
            'to_user',
            'to_user_username',
            'thread',
            'thread_id',
            'user_has_seen',
            'date',
            'last_message',
        ]

    def get_from_user_profile_pic(self, obj):
        request = self.context.get('request')
        if hasattr(obj.from_user, "profile") and obj.from_user.profile.picture:
            return request.build_absolute_uri(obj.from_user.profile.picture.url)
        return None

    def get_last_message(self, obj):
        if obj.thread:
            last_msg = MessageModel.objects.filter(thread=obj.thread).order_by('-date').first()
            if last_msg:
                if last_msg.body:
                    return last_msg.body
                elif last_msg.image:
                    request = self.context.get('request')
                    return request.build_absolute_uri(last_msg.image.url)
        return "لا توجد رسائل بعد"







class UserSuggestionSerializer(serializers.ModelSerializer):
    profile_pic = serializers.SerializerMethodField()

    class Meta:
        model = CustomUser
        fields = ['id', 'email', 'first_name', 'last_name', 'birth_date', 'profile_pic']

    def get_profile_pic(self, obj):
        request = self.context.get('request')  # ??? ???? ??? request
        profile = getattr(obj, "profile", None)
        if request and profile and getattr(profile, "picture", None):
            return request.build_absolute_uri(profile.picture.url)
        return None


        
class TagSerializer(serializers.ModelSerializer):
    name = serializers.SerializerMethodField()
    class Meta:
        model = Tag
        fields = ['id', 'name']
        
    def get_name(self, obj):
        # ????? # ??? ?????
        return f"#{obj.name}"


        
class PostTagsSerializer2(serializers.ModelSerializer):
    tags = TagSerializer(many=True, read_only=True)
    author_email = serializers.EmailField(source='author.email', read_only=True)
    author_image = serializers.SerializerMethodField()

    class Meta:
        model = Post
        fields = ['tags', 'author_email', 'author_image']

    def get_author_image(self, obj):
        if hasattr(obj.author, 'profile') and obj.author.profile.picture:
            request = self.context.get('request')
            if request:  # ? ???? ?? request ?? None
                return request.build_absolute_uri(obj.author.profile.picture.url)
            return obj.author.profile.picture.url  # fallback ???? request
        return None

    
class PostTagsSerializer(serializers.ModelSerializer):
    tags = TagSerializer(many=True, read_only=True)

    class Meta:
        model = Post
        fields = ['tags']







class ImageSerializer(serializers.ModelSerializer):
    class Meta:
        model = Image
        fields = ['id', 'image']
        







class TagSerializer2(serializers.ModelSerializer):
    class Meta:
        model = Tag
        fields = ['id', 'name']
        
class TagSerializer(serializers.ModelSerializer):
    name = serializers.SerializerMethodField()
    author_email = serializers.EmailField(source='post_set.first.author.email', read_only=True)
    author_image = serializers.SerializerMethodField()

    class Meta:
        model = Tag
        fields = ['id', 'name', 'author_email', 'author_image']

    def get_name(self, obj):
        # ??? # ??? ?????
        return f"#{obj.name}"

    def get_author_image(self, obj):
        # ?????? ??? ??? post ????? ??????
        post = obj.post_set.first()
        if post and hasattr(post.author, 'profile') and post.author.profile.picture:
            request = self.context.get('request')
            if request:
                return request.build_absolute_uri(post.author.profile.picture.url)
            return post.author.profile.picture.url
        return None



class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = CustomUser
        fields = ['id', 'email']


class PostSerializer(serializers.ModelSerializer):
    author = UserSerializer(read_only=True)
    author_image = serializers.SerializerMethodField()
    shared_user = UserSerializer(read_only=True)
    image = ImageSerializer(many=True, read_only=True)
    tags = TagSerializer(many=True, read_only=True)
    likes_count = serializers.SerializerMethodField()
    dislikes_count = serializers.SerializerMethodField()
    is_liked_by_user = serializers.SerializerMethodField()
    is_disliked_by_user = serializers.SerializerMethodField()

    class Meta:
        model = Post
        fields = [
            'id',
            'body',
            'shared_body',
            'created_on',
            'shared_on',
            'author',
            'author_image',
            'shared_user',
            'image',
            'tags',
            'likes_count',
            'dislikes_count',
            'is_liked_by_user',
             'is_disliked_by_user'
        ]
        
    def get_is_liked_by_user(self, obj):
        request = self.context.get('request')
        if request and request.user.is_authenticated:
            return request.user in obj.likes.all()
        return False

    def get_is_disliked_by_user(self, obj):
        request = self.context.get('request')
        if request and request.user.is_authenticated:
            return request.user in obj.dislikes.all()
        return False
        
    def get_images(self, obj):
        request = self.context.get('request')
        return ImageSerializer(obj.image.all(), many=True, context={'request': request}).data


    def get_likes_count(self, obj):
        return obj.likes.count()

    def get_dislikes_count(self, obj):
        return obj.dislikes.count()
        
    def get_author_image(self, obj):
    # تحقق من وجود profile وصورة قبل الوصول إليها
        if hasattr(obj.author, 'profile') and getattr(obj.author.profile, 'picture', None):
            request = self.context.get('request')
            return request.build_absolute_uri(obj.author.profile.picture.url)
        return None



        
#if request and request.user.is_authenticated:
 #       return 1 if request.user in obj.likes.all() else 0
  #  return 0






# serializers.py
from rest_framework import serializers
from django.contrib.auth import get_user_model
from .models import Post, Image

User = get_user_model()

class AuthorSerializer(serializers.ModelSerializer):
    class Meta:
        model = User
        fields = ['id','email','first_name','last_name']


class SharedPostSerializer(serializers.ModelSerializer):

    author = AuthorSerializer(read_only=True)
    shared_user = serializers.PrimaryKeyRelatedField(read_only=True)

    class Meta:
        model = Post
        fields = [
            'id',
            'body',
            'shared_body',
            'author',
            'shared_user',
            'created_on',
            'shared_on'
        ]
        read_only_fields = [
            'id',
            'body',
            'author',
            'shared_user',
            'created_on',
            'shared_on'
        ]







GENDER_CHOICES = (
    ('M', 'Male'),
    ('F', 'FeMale'),
    ('O', 'Other'),
)

YEARS = range(datetime.date.today().year - 100, datetime.date.today().year + 1)

class CustomRegisterSerializer(RegisterSerializer):
    first_name = serializers.CharField(required=True)
    last_name = serializers.CharField(required=True)
    birth_year = serializers.ChoiceField(choices=[(str(y), str(y)) for y in YEARS], required=True)
    birth_month = serializers.ChoiceField(choices=[(str(m), str(m)) for m in range(1, 13)], required=True)
    birth_day = serializers.ChoiceField(choices=[(str(d), str(d)) for d in range(1, 32)], required=True)
    gender = serializers.ChoiceField(choices=GENDER_CHOICES, required=True)

    def __init__(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.fields.pop('username', None)

    def validate(self, data):
        data.pop('username', None)
        errors = {}

        # ? Email check
        email = data.get('email')
        if not email:
            errors['email'] = "Email is required."
        elif '@' not in email or '.' not in email:
            errors['email'] = "Email is not valid."

        # ? Name checks
        if not data.get('first_name') or data.get('first_name').strip() == '':
            errors['first_name'] = "First name is required."

        if not data.get('last_name') or data.get('last_name').strip() == '':
            errors['last_name'] = "Last name is required."

        # ? Gender check
        if data.get('gender') not in dict(GENDER_CHOICES):
            errors['gender'] = "Gender must be one of: M, F, O."

        # ? Password validation
        password1 = data.get('password1')
        password2 = data.get('password2')

        if not password1:
            errors['password1'] = "Password is required."
        else:
            if password1 != password2:
                errors['password2'] = "Passwords do not match."
            if len(password1) < 8:
                errors['password1'] = "Password must be at least 8 characters long."
            if not re.search(r'[A-Z]', password1):
                errors['password1'] = "Password must contain at least one uppercase letter."
            if not re.search(r'[a-z]', password1):
                errors['password1'] = "Password must contain at least one lowercase letter."
            if not re.search(r'\d', password1):
                errors['password1'] = "Password must contain at least one number."
            if not re.search(r'[!@#$%^&*()_+=\[\]{};:"\\|,.<>/?-]', password1):
                errors['password1'] = "Password must contain at least one special character."

        # ? Validate date
        try:
            birth_date = datetime.date(
                int(data['birth_year']),
                int(data['birth_month']),
                int(data['birth_day'])
            )
            data['birth_date'] = birth_date
        except ValueError:
            errors['birth_date'] = "Date of birth is incorrect."

        # ? ??? ??? ?????? ?????? ???? ???? ????
        if errors:
            raise serializers.ValidationError(errors)

        return data

    def get_cleaned_data(self):
        return {
            'password1': self.validated_data['password1'],
            'password2': self.validated_data['password2'],
            'email': self.validated_data['email'],
            'first_name': self.validated_data['first_name'],
            'last_name': self.validated_data['last_name'],
            'birth_date': self.validated_data['birth_date'],
            'gender': self.validated_data['gender'],
        }

    def save(self, request):
        user = super().save(request)
        user.first_name = self.validated_data['first_name']
        user.last_name = self.validated_data['last_name']
        user.birth_date = self.validated_data['birth_date']
        user.gender = self.validated_data['gender']
        user.save()
        return user





class CustomLoginSerializer(serializers.Serializer):
    email = serializers.EmailField()
    password = serializers.CharField(write_only=True)

    def validate(self, data):
        email = data.get('email')
        password = data.get('password')

        if email and password:
            user = authenticate(request=self.context.get('request'), email=email, password=password)

            if not user:
                raise serializers.ValidationError({
                    "non_field_errors": ["Invalid email or password."]
                })

            if not user.is_active:
                raise serializers.ValidationError({
                    "non_field_errors": ["This account is inactive."]
                })

        else:
            raise serializers.ValidationError({
                "non_field_errors": ["Email and password are required."]
            })

        data['user'] = user
        return data












class TagSerializer222(serializers.ModelSerializer):
    class Meta:
        model = Tag
        fields = ['id', 'name']


class PostSerializer222(serializers.ModelSerializer):
    author = UserSerializer(read_only=True)
    author_image = serializers.SerializerMethodField()  # ??? ?????
    shared_user = UserSerializer(read_only=True)
    image = ImageSerializer(many=True, read_only=True)
    tags = TagSerializer(many=True, read_only=True)
    likes_count = serializers.SerializerMethodField()
    dislikes_count = serializers.SerializerMethodField()
    is_liked_by_user = serializers.SerializerMethodField()
    is_disliked_by_user = serializers.SerializerMethodField()

    class Meta:
        model = Post
        fields = [
            'id',
            'body',
            'shared_body',
            'created_on',
            'shared_on',
            'author',
            'author_image',   # ??? author_image ???
            'shared_user',
            'image',
            'tags',
            'likes_count',
            'dislikes_count',
            'is_liked_by_user',
            'is_disliked_by_user'
        ]

    # ??? author_image
    def get_author_image(self, obj):
        request = self.context.get('request')
        if obj.author and hasattr(obj.author, 'profile') and obj.author.profile.picture:
            if request:
                return request.build_absolute_uri(obj.author.profile.picture.url)
            else:
                return obj.author.profile.picture.url
        return None

    # ???? ??????
    def get_is_liked_by_user(self, obj):
        request = self.context.get('request')
        if request and request.user.is_authenticated:
            return request.user in obj.likes.all()
        return False

    def get_is_disliked_by_user(self, obj):
        request = self.context.get('request')
        if request and request.user.is_authenticated:
            return request.user in obj.dislikes.all()
        return False

    def get_likes_count(self, obj):
        return obj.likes.count()

    def get_dislikes_count(self, obj):
        return obj.dislikes.count()
        
        
########Notificat
from rest_framework import serializers
from .models import Notification, Post, Comment, CustomUser, UserProfile, ThreadModel, MessageModel, Image
# Serializer ???????? ?? ?????? ???? ?????????
#1️⃣ استخدام source للوصول لحقل الصورة في الـ profile
# 1️⃣ Serializer لعرض بيانات المستخدم
class UserProfileSerializerNoti(serializers.ModelSerializer):
    profile_picture = serializers.ImageField(source='profile.picture', read_only=True)
    followers_count = serializers.SerializerMethodField()

    class Meta:
        model = CustomUser
        fields = ['id', 'first_name', 'last_name', 'email', 'profile_picture', 'followers_count']

    def get_followers_count(self, obj):
        return obj.profile.followers.count() if hasattr(obj, 'profile') else 0


# 2️⃣ Serializer للتعليقات
class CommentSerializerNoti(serializers.ModelSerializer):
    author = UserProfileSerializerNoti(read_only=True)
    likes_count = serializers.SerializerMethodField()
    dislikes_count = serializers.SerializerMethodField()
    replies = serializers.SerializerMethodField()

    class Meta:
        model = Comment
        fields = ['id', 'comment', 'author', 'created_on', 'likes_count', 'dislikes_count', 'replies']

    def get_likes_count(self, obj):
        return obj.likes.count()

    def get_dislikes_count(self, obj):
        return obj.dislikes.count()

    def get_replies(self, obj):
        children = obj.children
        if children.exists():
            # ✅ نمرر نفس context حتى تظهر الصور كرابط مطلق
            return CommentSerializerNoti(children, many=True, context=self.context).data
        return []


# 3️⃣ Serializer للمنشورات
class PostSerializerNoti(serializers.ModelSerializer):
    author = UserProfileSerializerNoti(read_only=True)
    comments = serializers.SerializerMethodField()
    likes_count = serializers.SerializerMethodField()
    dislikes_count = serializers.SerializerMethodField()
    images = serializers.SerializerMethodField()

    class Meta:
        model = Post
        fields = ['id', 'body', 'shared_body', 'author', 'created_on', 'shared_on',
                  'likes_count', 'dislikes_count', 'comments', 'images']

    def get_comments(self, obj):
        parent_comments = obj.comment_set.filter(parent=None).order_by('-created_on')
        # ✅ تمرير context للـ CommentSerializerNoti
        return CommentSerializerNoti(parent_comments, many=True, context=self.context).data

    def get_likes_count(self, obj):
        return obj.likes.count()

    def get_dislikes_count(self, obj):
        return obj.dislikes.count()

    def get_images(self, obj):
        request = self.context.get('request')
        return [request.build_absolute_uri(img.image.url) if request else img.image.url for img in obj.image.all()]


# 4️⃣ Serializer للرسائل
class ThreadSerializerNot(serializers.ModelSerializer):
    sender = UserProfileSerializerNoti(source='sender_user', read_only=True)
    receiver = UserProfileSerializerNoti(source='receiver_user', read_only=True)

    class Meta:
        model = ThreadModel
        fields = ['id', 'sender', 'receiver']



# 5️⃣ Serializer للإشعارات
class NotificationSerializer3(serializers.ModelSerializer):
    from_user = UserProfileSerializerNoti(read_only=True)
    to_user = UserProfileSerializerNoti(read_only=True)
    post = PostSerializerNoti(read_only=True)
    comment = CommentSerializerNoti(read_only=True)
    thread = ThreadSerializerNot(read_only=True)
    message = serializers.SerializerMethodField()

    class Meta:
        model = Notification
        fields = ['id', 'notification_type', 'from_user', 'to_user',
                  'post', 'comment', 'thread', 'user_has_seen', 'date', 'message']

    def get_message(self, obj):
        if obj.notification_type == 1:
            if obj.post:
                return f"{obj.from_user.first_name} liked your post."
            elif obj.comment:
                return f"{obj.from_user.first_name} liked your comment."
        elif obj.notification_type == 2:
            if obj.post:
                return f"{obj.from_user.first_name} commented on your post."
            elif obj.comment:
                return f"{obj.from_user.first_name} replied to your comment."
        elif obj.notification_type == 3:
            return f"{obj.from_user.first_name} started following you."
        elif obj.thread:
            return f"{obj.from_user.first_name} sent you a message."
        return "You have a new notification."




# post comment reply

class PostSerializeraa(serializers.ModelSerializer):
    author = UserSerializer(read_only=True)
    author_image = serializers.SerializerMethodField()
    shared_user = UserSerializer(read_only=True)
    image = ImageSerializer(many=True, read_only=True)
    tags = TagSerializer(many=True, read_only=True)
    likes_count = serializers.SerializerMethodField()
    dislikes_count = serializers.SerializerMethodField()
    is_liked_by_user = serializers.SerializerMethodField()
    is_disliked_by_user = serializers.SerializerMethodField()
    comments = serializers.SerializerMethodField()  # التعليقات مع الردود

    class Meta:
        model = Post
        fields = [
            'id', 'body', 'shared_body', 'created_on', 'shared_on',
            'author', 'author_image', 'shared_user', 'image', 'tags',
            'likes_count', 'dislikes_count', 
            'comments','is_liked_by_user',
             'is_disliked_by_user'
        ]

    def get_is_liked_by_user(self, obj):
        request = self.context.get('request')
        return request.user in obj.likes.all() if request and request.user.is_authenticated else False

    def get_is_disliked_by_user(self, obj):
        request = self.context.get('request')
        return request.user in obj.dislikes.all() if request and request.user.is_authenticated else False

    def get_author_image(self, obj):
        if hasattr(obj.author, 'profile') and getattr(obj.author.profile, 'picture', None):
            request = self.context.get('request')
            return request.build_absolute_uri(obj.author.profile.picture.url)
        return None

    def get_comments(self, obj):
        # جميع التعليقات الرئيسية (parent=None)
        comments = Comment.objects.filter(post=obj, parent=None).order_by('-created_on')
        return CommentSerializer2(comments, many=True, context=self.context).data


    def get_likes_count(self, obj):
        return obj.likes.count()

    def get_dislikes_count(self, obj):
        return obj.dislikes.count()


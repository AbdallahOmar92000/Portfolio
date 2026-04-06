from rest_framework import serializers
from django.contrib.auth import authenticate
import datetime
import re

from .models import (
    Post, Comment, Image, Tag,
    Notification, ThreadModel, MessageModel,
    CustomUser, UserProfile
)
from dj_rest_auth.registration.serializers import RegisterSerializer

# -------------------------------
# User Serializer
# -------------------------------
class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = CustomUser
        fields = ['id', 'email', 'first_name', 'last_name', 'birth_date', 'profile_pic']


# -------------------------------
# User Profile for Notifications
# -------------------------------
class UserProfileSerializerNoti(serializers.ModelSerializer):
    profile_picture = serializers.ImageField(source='profile.picture', read_only=True)
    followers_count = serializers.SerializerMethodField()

    class Meta:
        model = CustomUser
        fields = ['id', 'first_name', 'last_name', 'email', 'profile_picture', 'followers_count']

    def get_followers_count(self, obj):
        return obj.profile.followers.count() if hasattr(obj, 'profile') else 0


# -------------------------------
# Tag Serializer
# -------------------------------
class TagSerializer(serializers.ModelSerializer):
    name = serializers.SerializerMethodField()
    author_email = serializers.EmailField(source='post_set.first.author.email', read_only=True)
    author_image = serializers.SerializerMethodField()

    class Meta:
        model = Tag
        fields = ['id', 'name', 'author_email', 'author_image']

    def get_name(self, obj):
        return f"#{obj.name}"

    def get_author_image(self, obj):
        post = obj.post_set.first()
        if post and hasattr(post.author, 'profile') and post.author.profile.picture:
            request = self.context.get('request')
            if request:
                return request.build_absolute_uri(post.author.profile.picture.url)
            return post.author.profile.picture.url
        return None


# -------------------------------
# Image Serializer
# -------------------------------
class ImageSerializer(serializers.ModelSerializer):
    class Meta:
        model = Image
        fields = ['id', 'image']


# -------------------------------
# Post Serializer
# -------------------------------
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
            'id', 'body', 'shared_body', 'created_on', 'shared_on',
            'author', 'author_image', 'shared_user', 'image', 'tags',
            'likes_count', 'dislikes_count', 'is_liked_by_user', 'is_disliked_by_user'
        ]

    def get_author_image(self, obj):
        if hasattr(obj.author, 'profile') and getattr(obj.author.profile, 'picture', None):
            request = self.context.get('request')
            if request:
                return request.build_absolute_uri(obj.author.profile.picture.url)
            return obj.author.profile.picture.url
        return None

    def get_is_liked_by_user(self, obj):
        request = self.context.get('request')
        return request.user in obj.likes.all() if request and request.user.is_authenticated else False

    def get_is_disliked_by_user(self, obj):
        request = self.context.get('request')
        return request.user in obj.dislikes.all() if request and request.user.is_authenticated else False

    def get_likes_count(self, obj):
        return obj.likes.count()

    def get_dislikes_count(self, obj):
        return obj.dislikes.count()


# -------------------------------
# Comment Serializer for Notifications
# -------------------------------
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
            return CommentSerializerNoti(children, many=True, context=self.context).data
        return []


# -------------------------------
# Post Serializer for Notifications
# -------------------------------
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
        return CommentSerializerNoti(parent_comments, many=True, context=self.context).data

    def get_likes_count(self, obj):
        return obj.likes.count()

    def get_dislikes_count(self, obj):
        return obj.dislikes.count()

    def get_images(self, obj):
        request = self.context.get('request')
        return [request.build_absolute_uri(img.image.url) if request else img.image.url for img in obj.image.all()]


# -------------------------------
# Thread Serializer
# -------------------------------
class ThreadSerializerNot(serializers.ModelSerializer):
    sender = UserProfileSerializerNoti(source='sender_user', read_only=True)
    receiver = UserProfileSerializerNoti(source='receiver_user', read_only=True)

    class Meta:
        model = ThreadModel
        fields = ['id', 'sender', 'receiver']


# -------------------------------
# Notification Serializer
# -------------------------------
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


# -------------------------------
# Register Serializer
# -------------------------------
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

        email = data.get('email')
        if not email:
            errors['email'] = "Email is required."
        elif '@' not in email or '.' not in email:
            errors['email'] = "Email is not valid."

        if not data.get('first_name') or data.get('first_name').strip() == '':
            errors['first_name'] = "First name is required."

        if not data.get('last_name') or data.get('last_name').strip() == '':
            errors['last_name'] = "Last name is required."

        if data.get('gender') not in dict(GENDER_CHOICES):
            errors['gender'] = "Gender must be one of: M, F, O."

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

        try:
            birth_date = datetime.date(
                int(data['birth_year']),
                int(data['birth_month']),
                int(data['birth_day'])
            )
            data['birth_date'] = birth_date
        except ValueError:
            errors['birth_date'] = "Date of birth is incorrect."

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


# -------------------------------
# Login Serializer
# -------------------------------
class CustomLoginSerializer(serializers.Serializer):
    email = serializers.EmailField()
    password = serializers.CharField(write_only=True)

    def validate(self, data):
        email = data.get('email')
        password = data.get('password')

        if email and password:
            user = authenticate(request=self.context.get('request'), email=email, password=password)
            if not user:
                raise serializers.ValidationError({"non_field_errors": ["Invalid email or password."]})
            if not user.is_active:
                raise serializers.ValidationError({"non_field_errors": ["This account is inactive."]})
        else:
            raise serializers.ValidationError({"non_field_errors": ["Email and password are required."]})

        data['user'] = user
        return data

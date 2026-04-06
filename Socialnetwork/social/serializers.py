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



class UserSerializer(serializers.ModelSerializer):
    class Meta:
        model = CustomUser
        fields = ['id', 'email']

class ImageSerializer(serializers.ModelSerializer):
    image = serializers.ImageField(use_url=True)

    class Meta:
        model = Image
        fields = ['id', 'image']

class TagSerializer(serializers.ModelSerializer):
    class Meta:
        model = Tag
        fields = ['id', 'name']

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
            'is_disliked_by_user',
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

    def get_likes_count(self, obj):
        return obj.likes.count()

    def get_dislikes_count(self, obj):
        return obj.dislikes.count()

    def get_author_image(self, obj):
        if hasattr(obj.author, 'profile') and getattr(obj.author.profile, 'picture', None):
            request = self.context.get('request')
            return request.build_absolute_uri(obj.author.profile.picture.url)
        return None
        
        
        
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




class UserProfileSerializer(serializers.ModelSerializer):
    class Meta:
        model = CustomUser
        fields = ['id', 'email', 'first_name', 'last_name', 'birth_date', 'profile_pic']



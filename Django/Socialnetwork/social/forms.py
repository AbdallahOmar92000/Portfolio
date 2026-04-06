from django import forms
from .models import *
from allauth.account.forms import SignupForm
from django.contrib.auth import get_user_model
User = get_user_model()


from allauth.account.forms import SignupForm
from django import forms
from django.forms.widgets import SelectDateWidget
import datetime

from django import forms
from allauth.account.forms import SignupForm
import datetime
from django import forms
from django.core.exceptions import ValidationError
from .models import UserProfile, CustomUser
from django import forms
from django.core.exceptions import ValidationError
from .models import UserProfile
from django.contrib.auth import get_user_model

User = get_user_model()

class PostForm(forms.ModelForm):
    body = forms.CharField(
        label='',
        widget=forms.Textarea(attrs={
            'rows':'3',
            'placeholder':'Say Something...'
        })
    )
    #add
    image = forms.ImageField(required = False)
    class Meta:
        model = Post
        fields = ['body']
        #fields = ['body','image']
 

        


class CommentForm(forms.ModelForm):
    comment = forms.CharField(
        label='',
        widget=forms.Textarea(
            attrs={'rows': '3',
                   'placeholder': 'Comment...'}
        ))
        
    #add image
    image = forms.ImageField(required=False)  # ????? ??? ??????

    class Meta:
        model = Comment
        fields = ['comment','image']
        

        
class CommentRepForm(forms.ModelForm):
    comment = forms.CharField(
        label='',
        widget=forms.Textarea(
            attrs={'rows': '3',
                   'placeholder': 'Reply...'}
        ))
        
    image = forms.ImageField(required=False)

    class Meta:
        model = Comment
        fields = ['comment','image']
        




class ThreadFor2m(forms.Form):
    email = forms.CharField(label='', max_length=100)
    
class ThreadForm(forms.Form):
    search_input = forms.CharField(
        max_length=150,
        required=True,
        widget=forms.TextInput(attrs={
            'placeholder': 'Enter First name, Last name or Email',
            'class': 'form-control'
        })
    )

    




class MessageForm(forms.ModelForm):
    body = forms.CharField(label='', max_length=1000)

    image = forms.ImageField(required=False)

    class Meta:
        model = MessageModel
        fields = ['body', 'image']





class ShareForm(forms.Form):
    body = forms.CharField(
        label='',
        widget=forms.Textarea(attrs={
            'rows': '3',
            'placeholder': 'Say Something...'
            }))
            
            



class ExploreForm(forms.Form):
    query = forms.CharField(
        label='',
        widget=forms.TextInput(attrs={
            'placeholder':'Explore tags'
        })
    )
    
    

class Co2mmentForm(forms.ModelForm):
    comment = forms.CharField(
        label='',
        widget=forms.Textarea(
            attrs={'rows': '3',
                   'placeholder': 'Say Something...'}
        ))
        
    # ??? ??????
    image = forms.ImageField(required=False)

    class Meta:
        model = Comment
        fields = ['comment', 'image']

    def __init__(self, *args, **kwargs):
        parent = kwargs.get('parent', None)  # ???? ??? ???? parent ??? ???? ??????
        super(CommentForm, self).__init__(*args, **kwargs)
        
        # ??? ??? ??????? ?? ?? (?? ????? ??? parent)? ???? ?????? ??? ??????
        if parent:
            self.fields['image'].widget = forms.HiddenInput()  # ????? ?????
            self.fields['image'].required = False  # ?????? ?? ?? ????? ??? ???????


class Us1erProfileForm(forms.ModelForm):
    username = forms.CharField(max_length=150, required=True)  # ????? ??? username
    email = forms.EmailField(required=True)  # ????? ??? email
    first_name = forms.CharField(max_length=30, required=True)
    last_name = forms.CharField(max_length=30, required=True)

    class Meta:
        model = UserProfile
        fields = ['username', 'name', 'bio', 'birth_date', 'location', 'picture']

    def __init__(self, *args, **kwargs):
        user = kwargs.pop('user', None)
        super().__init__(*args, **kwargs)
        if user:
            self.fields['first_name'].initial = user.first_name
            self.fields['last_name'].initial = user.last_name
            self.fields['email'].initial = user.email 

    def clean_username(self):
        # ?????? ?? ?? `username` ??? ????
        username = self.cleaned_data['username']
        if User.objects.filter(username=username).exclude(pk=self.instance.user.pk).exists():
            raise ValidationError("This username is already taken. Please choose another one.")
        return username
    def clean_email(self):
        # ?????? ?? ?? `email` ??? ????
        email = self.cleaned_data['email']
        if User.objects.filter(email=email).exclude(pk=self.instance.user.pk).exists():
            raise ValidationError("This email is already in use. Please choose another one.")
        return email
        
    def save(self, commit=True):
        profile = super().save(commit=False)
        user = self.instance.user
        user.email = self.cleaned_data['email']
        user.first_name = self.cleaned_data['first_name']
        user.last_name = self.cleaned_data['last_name']
        user.birth_date = self.cleaned_data['birth_date']

        if commit:
            user.save()
            profile.save()
        return profile
        






class UserProfileForm(forms.ModelForm):
    email = forms.EmailField(required=True)
    first_name = forms.CharField(max_length=30, required=True)
    last_name = forms.CharField(max_length=30, required=True)
    gender = forms.ChoiceField(
        choices=[('', '---------'), ('M', 'Male'), ('F', 'Female'), ('O', 'Other')],
        required=False,
        widget=forms.Select(attrs={'class': 'form-select'})  # Dropdown ?? Bootstrap
    )

    class Meta:
        model = UserProfile
        fields = ['name', 'bio', 'birth_date', 'location', 'picture']  # username ????

    def __init__(self, *args, **kwargs):
        user = kwargs.pop('user', None)
        super().__init__(*args, **kwargs)
        if user:
            self.fields['first_name'].initial = user.first_name
            self.fields['last_name'].initial = user.last_name
            self.fields['email'].initial = user.email
            self.fields['gender'].initial = user.gender  # ?????? ???????

    def clean_email(self):
        email = self.cleaned_data['email']
        if CustomUser.objects.filter(email=email).exclude(pk=self.instance.user.pk).exists():
            raise ValidationError("This email is already in use. Please choose another one.")
        return email

    def save(self, commit=True):
        profile = super().save(commit=False)
        user = self.instance.user
        user.email = self.cleaned_data['email']
        user.first_name = self.cleaned_data['first_name']
        user.last_name = self.cleaned_data['last_name']
        user.birth_date = self.cleaned_data['birth_date']
        user.gender = self.cleaned_data['gender']  # ??? ??? Dropdown

        if commit:
            user.save()
            profile.save()
        return profile








# ????? ????????
YEARS = range(datetime.date.today().year - 100, datetime.date.today().year + 1)
MONTHS = [(i, i) for i in range(1, 13)]
DAYS = [(i, i) for i in range(1, 32)]
GENDER_CHOICES = [
    ('M', 'Male'),
    ('F', 'Female'),
    ('O', 'Other'),
]

class CustomSignupForm(SignupForm):
    first_name = forms.CharField(
        max_length=30,
        label='First Name',
        required=True,
        widget=forms.TextInput(attrs={'placeholder': 'First name'})
    )
    last_name = forms.CharField(
        max_length=30,
        label='Last Name',
        required=True,
        widget=forms.TextInput(attrs={'placeholder': 'Last name'})
    )
    birth_year = forms.ChoiceField(
        label='Year', 
        choices=[('', 'Choose Year')] + [(str(y), str(y)) for y in YEARS],
        required=True
    )
    birth_month = forms.ChoiceField(
        label='Month', 
        choices=[('', 'Choose Month')] + [(str(m), str(m)) for m in range(1, 13)],
        required=True
    )
    birth_day = forms.ChoiceField(
        label='Day', 
        choices=[('', 'Choose Day')] + [(str(d), str(d)) for d in range(1, 32)],
        required=True
    )
    gender = forms.ChoiceField(
        label='Gender',
        choices=[('', 'Select Gender')] + GENDER_CHOICES,
        required=True,
        widget=forms.Select(attrs={'class': 'form-select'})
    )

    def clean(self):
        cleaned_data = super().clean()
        year = cleaned_data.get('birth_year')
        month = cleaned_data.get('birth_month')
        day = cleaned_data.get('birth_day')

        if not year or not month or not day:
            raise forms.ValidationError("Please select a valid date of birth.")

        try:
            birth_date = datetime.date(int(year), int(month), int(day))
        except ValueError:
            raise forms.ValidationError("Invalid date of birth. Please select a valid date.")

        cleaned_data['birth_date'] = birth_date
        return cleaned_data

    def save(self, request):
        user = super().save(request)
        user.first_name = self.cleaned_data['first_name']
        user.last_name = self.cleaned_data['last_name']
        user.birth_date = self.cleaned_data['birth_date']
        user.gender = self.cleaned_data['gender']
        user.save()
        return user



class Custom2SignupForm(SignupForm):
    first_name = forms.CharField(
        max_length=30,
        label='First Name',
        required=True,
        widget=forms.TextInput(attrs={'placeholder': 'First name'})
    )

    last_name = forms.CharField(
        max_length=30,
        label='Last Name',
        required=True,
        widget=forms.TextInput(attrs={'placeholder': 'Last name'})
    )



    birth_date = forms.DateField(
        label='Birth Date',
        required=True,
        widget=SelectDateWidget(
            years=range(datetime.date.today().year - 100, datetime.date.today().year + 1),
            empty_label=("???? ?????", "???? ?????", "???? ?????"),
        ),
        initial=None,
    )
    

    def save(self, request):
        user = super().save(request)
        user.first_name = self.cleaned_data['first_name']
        user.last_name = self.cleaned_data['last_name']
        user.birth_date = self.cleaned_data['birth_date']
        user.save()
        return user








        
from django.urls import path
from . import views  #مع from . import views، تحتاج إلى الوصول إلى العناصر داخل views باستخدام views..
from .views import *

urlpatterns = [
    path('translate', translate_view, name='translate'),
    path('translate2', translate_view2, name='translate'),
    path('translate3', translate_view2_COOKIES, name='translate'),
    path('api/translate/', TranslateTextView.as_view(), name='translate-text'),


]
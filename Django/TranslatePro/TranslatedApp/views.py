from django.shortcuts import render,redirect
from .forms import *
from .models import *
from .forms import TranslationForm
from .utils import *
import uuid as uuid_lib
from rest_framework.views import APIView
from rest_framework.response import Response
from rest_framework import status
from deep_translator import GoogleTranslator
import json
from rest_framework.permissions import AllowAny  # <-- أضف هذا


def translate_view(request):
    translation_result = None
    if request.method == 'POST':
        form = TranslationForm2(request.POST)
        if form.is_valid():
            source_language = form.cleaned_data['source_language']
            target_language = form.cleaned_data['target_language']
            source_text = form.cleaned_data['source_text']
            translation_result = translate_text(source_text, source_language, target_language)

             # حفظ الترجمة في قاعدة البيانات
            Translation.objects.create(
                # user=request.user,  # إذا كنت تريد حفظ الترجمة للمستخدم الحالي
                source_language=source_language,
                target_language=target_language,
                source_text=source_text,
                translated_text=translation_result
            )
    else:
        form = TranslationForm2()

    return render(request, 'index2.html', {'form': form, 'result': translation_result})





def translate_view2(request):
    translation_result = None
    translation_history = []

    if request.method == 'POST':
        form = TranslationForm(request.POST)
        if form.is_valid():
            source_language = form.cleaned_data['source_language']
            target_language = form.cleaned_data['target_language']
            source_text = form.cleaned_data['source_text']

            # ترجمة مباشرة بدون كاش
            translation_result = translate_text(source_text, source_language, target_language)

            # إضافة للسجل المؤقت في الجلسة الحالية (غير محفوظ)
            translation_history.append({
                'source_language': source_language,
                'target_language': target_language,
                'source_text': source_text,
                'translated_text': translation_result
            })
    else:
        form = TranslationForm()

    return render(request, 'a.html', {
        'form': form,
        'result': translation_result,
        'history': translation_history
    })

#هل تحب أكتب لك نسخة موحدة تجمع بين حفظ قاعدة البيانات والكاش وسجل المستخدم مع حذف من السجل؟






def translate_view2_COOKIES(request):
    translation_result = None
    translation_history = []

    # استرجاع السجل من الكوكيز (إذا موجود)
    history_cookie = request.COOKIES.get('translation_history')
    if history_cookie:
        try:
            translation_history = json.loads(history_cookie)
        except json.JSONDecodeError:
            translation_history = []

    if request.method == 'POST':
        if 'delete_index' in request.POST:
            index = int(request.POST.get('delete_index'))
            if 0 <= index < len(translation_history):
                del translation_history[index]
        else:
            form = TranslationForm(request.POST)
            if form.is_valid():
                source_language = form.cleaned_data['source_language']
                target_language = form.cleaned_data['target_language']
                source_text = form.cleaned_data['source_text']
                translation_result = translate_text(source_text, source_language, target_language)

                new_entry = {
                    'source_language': source_language,
                    'target_language': target_language,
                    'source_text': source_text,
                    'translated_text': translation_result
                }

                translation_history.insert(0, new_entry)  # أضف الترجمة الجديدة في البداية
                translation_history = translation_history[:50]  # احتفظ بحد أقصى 50 ترجمة
    else:
        form = TranslationForm()

    response = render(request, 'a.html', {
        'form': form,
        'result': translation_result,
        'history': translation_history,
    })

    # حفظ السجل في الكوكيز
    response.set_cookie('translation_history', json.dumps(translation_history), max_age=60*60*24*365)

    return response

def translate_view2_COOKIESs(request):
    translation_result = None

    # الحصول على UUID من الكوكيز أو إنشاؤه
    user_uuid = request.COOKIES.get('uuid')
    if not user_uuid:
        user_uuid = str(uuid_lib.uuid4())

    if request.method == 'POST':
        if 'delete_id' in request.POST:
            # حذف من قاعدة البيانات
            record_id = request.POST['delete_id']
            TranslationRecord.objects.filter(id=record_id, uuid=user_uuid).delete()
            return redirect('/tranlate3')

        form = TranslationForm(request.POST)
        if form.is_valid():
            source_language = form.cleaned_data['source_language']
            target_language = form.cleaned_data['target_language']
            source_text = form.cleaned_data['source_text']

            translation_result = translate_text(source_text, source_language, target_language)

            # حفظ في قاعدة البيانات
            TranslationRecord.objects.create(
                uuid=user_uuid,
                source_language=source_language,
                target_language=target_language,
                source_text=source_text,
                translated_text=translation_result
            )
    else:
        form = TranslationForm()

    # جلب السجل من قاعدة البيانات
    translation_history = TranslationRecord.objects.filter(uuid=user_uuid).order_by('-created_at')

    response = render(request, 'a.html', {
        'form': form,
        'result': translation_result,
        'history': translation_history
    })

    # إذا لم يكن الكوكيز موجودًا، نحفظه
    if not request.COOKIES.get('uuid'):
        response.set_cookie('uuid', user_uuid, max_age=60*60*24*365)  # سنة

    return response




class TranslateTextView(APIView):
    permission_classes = [AllowAny]  # <-- مهم جدًا للسماح بالوصول بدون تسجيل

    def post(self, request, format=None):
        # الحصول على UUID من الكوكيز أو إنشاؤه
       # user_uuid = request.COOKIES.get('uuid')
       # if not user_uuid:
       #     user_uuid = str(uuid_lib.uuid4())

        # الحصول على البيانات من الـ POST
        source_language = request.data.get('source_language')
        target_language = request.data.get('target_language')
        source_text = request.data.get('source_text')

        if not source_language or not target_language or not source_text:
            return Response({"error": "Missing required fields."}, status=status.HTTP_400_BAD_REQUEST)

        # الترجمة باستخدام deep-translator
        try:
            translator = GoogleTranslator(source=source_language, target=target_language)
            translated_text = translator.translate(source_text)
            return Response({"translated_text": translated_text}, status=status.HTTP_200_OK)
        except Exception as e:
            return Response({"error": str(e)}, status=status.HTTP_400_BAD_REQUEST)

from django import forms

# class TranslationForm(forms.Form):
#     source_language = forms.CharField(label='لغة المصدر', max_length=50)
#     target_language = forms.CharField(label='اللغة الهدف', max_length=50)
#     source_text = forms.CharField(label='النص المراد ترجمته', widget=forms.Textarea)
LANGUAGES = [
    ('af', 'Afrikaans'),
    ('sq', 'Albanian'),
    ('ar', 'Arabic'),
    ('hy', 'Armenian'),
    ('bn', 'Bengali'),
    ('bs', 'Bosnian'),
    ('ca', 'Catalan'),
    ('hr', 'Croatian'),
    ('cs', 'Czech'),
    ('da', 'Danish'),
    ('nl', 'Dutch'),
    ('en', 'English'),
    ('eo', 'Esperanto'),
    ('et', 'Estonian'),
    ('tl', 'Filipino'),
    ('fi', 'Finnish'),
    ('fr', 'French'),
    ('de', 'German'),
    ('el', 'Greek'),
    ('gu', 'Gujarati'),
    ('hi', 'Hindi'),
    ('hu', 'Hungarian'),
    ('is', 'Icelandic'),
    ('id', 'Indonesian'),
    ('it', 'Italian'),
    ('ja', 'Japanese'),
    ('jw', 'Javanese'),
    ('ko', 'Korean'),
    ('la', 'Latin'),
    ('lt', 'Lithuanian'),
    ('mk', 'Macedonian'),
    ('ml', 'Malayalam'),
    ('mr', 'Marathi'),
    ('ne', 'Nepali'),
    ('pl', 'Polish'),
    ('pt', 'Portuguese'),
    ('pa', 'Punjabi'),
    ('ro', 'Romanian'),
    ('ru', 'Russian'),
    ('sr', 'Serbian'),
    ('si', 'Sinhalese'),
    ('es', 'Spanish'),
    ('su', 'Sundanese'),
    ('sw', 'Swahili'),
    ('sv', 'Swedish'),
    ('ta', 'Tamil'),
    ('te', 'Telugu'),
    ('th', 'Thai'),
    ('tr', 'Turkish'),
    ('uk', 'Ukrainian'),
    ('vi', 'Vietnamese'),
    ('cy', 'Welsh'),
    ('yi', 'Yiddish'),
    ('zh', 'Chinese'),
]

# LANGUAGES = {
#     'af': 'Afrikaans',
#     'sq': 'Albanian',
#     'ar': 'Arabic',
#     'hy': 'Armenian',
#     'bn': 'Bengali',
#     'bs': 'Bosnian',
#     'ca': 'Catalan',
#     'hr': 'Croatian',
#     'cs': 'Czech',
#     'da': 'Danish',
#     'nl': 'Dutch',
#     'en': 'English',
#     'eo': 'Esperanto',
#     'et': 'Estonian',
#     'tl': 'Filipino',
#     'fi': 'Finnish',
#     'fr': 'French',
#     'de': 'German',
#     'el': 'Greek',
#     'gu': 'Gujarati',
#     'hi': 'Hindi',
#     'hu': 'Hungarian',
#     'is': 'Icelandic',
#     'id': 'Indonesian',
#     'it': 'Italian',
#     'ja': 'Japanese',
#     'jw': 'Javanese',
#     'ko': 'Korean',
#     'la': 'Latin',
#     'lt': 'Lithuanian',
#     'mk': 'Macedonian',
#     'ml': 'Malayalam',
#     'mr': 'Marathi',
#     'ne': 'Nepali',
#     'pl': 'Polish',
#     'pt': 'Portuguese',
#     'pa': 'Punjabi',
#     'ro': 'Romanian',
#     'ru': 'Russian',
#     'sr': 'Serbian',
#     'si': 'Sinhalese',
#     'es': 'Spanish',
#     'su': 'Sundanese',
#     'sw': 'Swahili',
#     'sv': 'Swedish',
#     'ta': 'Tamil',
#     'te': 'Telugu',
#     'th': 'Thai',
#     'tr': 'Turkish',
#     'uk': 'Ukrainian',
#     'vi': 'Vietnamese',
#     'cy': 'Welsh',
#     'yi': 'Yiddish',
#     'zh': 'Chinese',
#     'ml': 'Malay',
#     'ka': 'Georgian',
#     'am': 'Amharic',
#     'sw': 'Swahili',
#     'ht': 'Haitian Creole',
#     'he': 'Hebrew',
#     'no': 'Norwegian',
#     'pl': 'Polish',
#     'sw': 'Swahili',
#     'tr': 'Turkish',
#     'uz': 'Uzbek',
#     'mr': 'Marathi',
#     'my': 'Burmese',
#     'zu': 'Zulu',
#     'tl': 'Filipino',
#     'te': 'Telugu',
#     'sq': 'Albanian',
#     'bn': 'Bengali',
#     # قائمة تتوسع باستمرار لتشمل المزيد من اللغات
# }


class TranslationForm(forms.Form):
    source_language = forms.ChoiceField(label='لغة المصدر', choices=LANGUAGES)
    target_language = forms.ChoiceField(label='اللغة الهدف', choices=LANGUAGES)
    source_text = forms.CharField(label='النص المراد ترجمته', widget=forms.Textarea)

class TranslationForm2(forms.Form):
    source_language = forms.ChoiceField(label='لغة المصدر', choices=LANGUAGES)
    target_language = forms.ChoiceField(label='اللغة الهدف', choices=LANGUAGES)
    source_text = forms.CharField(label='النص المراد ترجمته', widget=forms.Textarea)





# القاموس (Dictionary) في لغة Python هو نوع من أنواع البيانات الذي يتيح لك تخزين البيانات في شكل مفتاح (key) مرتبط بقيمة (value). يتم استخدام القاموس بشكل أساسي لربط المفاتيح بالقيم بحيث يمكنك الوصول إلى القيمة بسرعة باستخدام المفتاح.

# خصائص القاموس:
# غير مرتب (Unordered): لا يتم الاحتفاظ بترتيب العناصر في القاموس.

# مفاتيح فريدة (Unique Keys): يجب أن تكون المفاتيح في القاموس فريدة. لا يمكنك أن يكون لديك نفس المفتاح أكثر من مرة.

# قابل للتعديل (Mutable): يمكنك تعديل القيم، إضافة، أو حذف العناصر في القاموس.

# يمكن أن يحتوي على أنواع بيانات متنوعة: يمكن أن تحتوي القيم على أنواع بيانات متعددة مثل الأعداد الصحيحة، السلاسل النصية، القوائم، التوبلات، إلخ.



# التوبل:
# غير قابل للتعديل (Immutable): بمجرد إنشاء التوبل، لا يمكن تغيير قيمته. لا يمكنك إضافة أو حذف أو تعديل العناصر داخل التوبل.

# مرتبة (Ordered): يتم حفظ العناصر بترتيبها كما هي في التوبل.

# قابل للتكرار (Iterable): يمكنك استخدام التكرار (مثل استخدام for للتكرار عبر التوبل).

# يمكن أن يحتوي على أنواع بيانات متنوعة: يمكن أن يحتوي التوبل على أنواع متعددة من البيانات، مثل الأعداد الصحيحة، السلاسل النصية، القوائم، القواميس، وغيرها.
from django.core.cache import cache
from deep_translator import GoogleTranslator

def translate_text(source_text, source_language, target_language):
    try:
        translator = GoogleTranslator(source=source_language, target=target_language)
        result = translator.translate(source_text)
        return result
    except Exception as e:
        return f"Error: {str(e)}"


def delete_translation_from_cache(source_text, source_language, target_language):
    # تحديد مفتاح الكاش بناءً على النص واللغات
    cache_key = f"{source_text}_{source_language}_{target_language}"
    
    # محاولة حذف الترجمة من الكاش
    cache.delete(cache_key)
    return f"تم حذف الترجمة من الكاش: {cache_key}"
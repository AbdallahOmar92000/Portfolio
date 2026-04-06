from django.db import models
from django.contrib.auth.models import User

# Create your models here.

class Translation(models.Model):
    source_language =models.TextField()
    target_language = models.TextField()
    source_text = models.TextField()
    translated_text = models.TextField()
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"{self.source_language} -> {self.target_language}: {self.source_text}"


# class TranslationHistory(models.Model):
#     user = models.ForeignKey(User, on_delete=models.CASCADE)  # تأكد من إضافة هذا الحقل
#     source_language = models.CharField(max_length=100)
#     target_language = models.CharField(max_length=100)
#     source_text = models.TextField()
#     translated_text = models.TextField()
#     timestamp = models.DateTimeField(auto_now_add=True)

#     def __str__(self):
#         return f"{self.source_text} -> {self.translated_text}"
    

class TranslationRecord(models.Model):
    uuid = models.TextField()
    source_language = models.TextField()
    target_language = models.TextField()
    source_text = models.TextField()
    translated_text = models.TextField()
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"{self.source_language} -> {self.target_language}: {self.source_text}"

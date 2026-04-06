import firebase_admin
from firebase_admin import credentials
import os

# --- المسار الأساسي للمشروع ---
BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))

# --- Firebase Nokat_app ---
cred_app1_path = os.path.join(BASE_DIR, 'Nokat_api', 'mynokatnoti.json')
cred_app1 = credentials.Certificate(cred_app1_path)
try:
    firebase_app1 = firebase_admin.initialize_app(cred_app1, name="nokat_app")
except ValueError:
    firebase_app1 = firebase_admin.get_app("nokat_app")

# --- Firebase Msgs_app ---
cred_app2_path = os.path.join(BASE_DIR, 'Msgs_Api', 'notification.json')
cred_app2 = credentials.Certificate(cred_app2_path)
try:
    firebase_app2 = firebase_admin.initialize_app(cred_app2, name="msgs_app")
except ValueError:
    firebase_app2 = firebase_admin.get_app("msgs_app")

# --- Firebase Img_app ---
cred_app3_path = os.path.join(BASE_DIR, 'Img_Api', 'myimgnotif.json')
cred_app3 = credentials.Certificate(cred_app3_path)
try:
    firebase_app3 = firebase_admin.initialize_app(cred_app3, name="imgapp")
except ValueError:
    firebase_app3 = firebase_admin.get_app("imgapp")
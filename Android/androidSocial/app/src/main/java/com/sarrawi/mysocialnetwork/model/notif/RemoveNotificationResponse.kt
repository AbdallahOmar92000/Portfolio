package com.sarrawi.mysocialnetwork.model.notif

data class RemoveNotificationResponse(
    val message: String?,  // سيكون موجود عند النجاح
    val error: String?     // سيكون موجود عند الفشل
)

package com.example.tubes.domain.repository


import com.example.tubes.data.model.TeacherNotificationUi

interface TeacherNotificationRepository {


    suspend fun getNotificationsByUser(
        userId: String
    ): List<TeacherNotificationUi>


    suspend fun markAsRead(
        notificationId: String
    )
}

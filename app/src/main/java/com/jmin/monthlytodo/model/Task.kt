package com.jmin.monthlytodo.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val priority: Priority = Priority.MEDIUM,
    val category: String = "General", // 在数据库层面仍然使用英文值
    val dueDate: Date,
    val isCompleted: Boolean = false,
    val createdAt: Date = Date(),
    val order: Int = 0
)

enum class Priority {
    LOW, MEDIUM, HIGH
}
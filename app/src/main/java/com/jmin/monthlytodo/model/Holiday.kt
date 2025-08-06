package com.jmin.monthlytodo.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "holidays")
data class Holiday(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val date: Date,
    val isCustom: Boolean = false // true for user-defined holidays, false for international holidays
)
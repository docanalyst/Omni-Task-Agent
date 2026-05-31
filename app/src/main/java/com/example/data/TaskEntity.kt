package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val dueDate: Long, // timestamp
    val category: String, // Work, Personal, Shopping, Health, Other
    val locationName: String?, // e.g. "Grocery Store", "Office", "Home"
    val syncStatus: String, // "Synced", "Pending", "OfflineOnly"
    val isCompleted: Boolean = false,
    val isEncrypted: Boolean = false,
    val priority: String, // "Low", "Medium", "High"
    val crossAppReminders: String // comma-separated list, e.g. "Google Tasks,Google Calendar"
)

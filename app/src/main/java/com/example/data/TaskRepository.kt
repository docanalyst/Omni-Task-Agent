package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepository(private val taskDao: TaskDao) {
    
    // Listen to all tasks and decrypt them before exposing them to the Flow!
    val allTasks: Flow<List<Task>> = taskDao.getAllTasksFlow().map { list ->
        list.map { task ->
            if (task.isEncrypted) {
                task.copy(
                    title = CryptoHelper.decrypt(task.title),
                    description = CryptoHelper.decrypt(task.description)
                )
            } else {
                task
            }
        }
    }

    suspend fun getTaskById(id: Int): Task? {
        val task = taskDao.getTaskById(id) ?: return null
        return if (task.isEncrypted) {
            task.copy(
                title = CryptoHelper.decrypt(task.title),
                description = CryptoHelper.decrypt(task.description)
            )
        } else {
            task
        }
    }

    suspend fun insertTask(task: Task, encryptLocal: Boolean): Long {
        val adjustedTask = if (encryptLocal) {
            task.copy(
                title = CryptoHelper.encrypt(task.title),
                description = CryptoHelper.encrypt(task.description),
                isEncrypted = true
            )
        } else {
            task.copy(isEncrypted = false)
        }
        return taskDao.insertTask(adjustedTask)
    }

    suspend fun updateTask(task: Task, encryptLocal: Boolean) {
        val adjustedTask = if (encryptLocal) {
            task.copy(
                title = CryptoHelper.encrypt(task.title),
                description = CryptoHelper.encrypt(task.description),
                isEncrypted = true
            )
        } else {
            task.copy(
                title = task.title,
                description = task.description,
                isEncrypted = false
            )
        }
        taskDao.updateTask(adjustedTask)
    }

    suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task)
    }

    suspend fun deleteAll() {
        taskDao.deleteAllTasks()
    }
}

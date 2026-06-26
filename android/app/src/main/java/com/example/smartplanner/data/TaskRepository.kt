package com.example.smartplanner.data

import com.example.smartplanner.model.Task
import com.example.smartplanner.model.TaskGroup
import com.example.smartplanner.model.ProductivityStats

object TaskRepository {

    private val tasks = mutableListOf<Task>()
    private val groupingService = TaskGroupingService()
    private val statsService = ProductivityStatsService()
    private var nextId = 1L
    private var nextSubtaskId = 1L
    var highFirst: Boolean = true

    fun getNewId(): Long {
        val id = nextId
        nextId += 1
        return id
    }

    fun addTask(task: Task) {
        tasks.add(task)
    }

    fun getNewSubtaskId(): Long {
        val id = nextSubtaskId
        nextSubtaskId += 1
        return id
    }

    fun toggleDone(id: Long) {
        val index = tasks.indexOfFirst { it.id == id }
        if (index >= 0) {
            val t = tasks[index]
            tasks[index] = t.copy(done = !t.done)
        }
    }

    fun toggleSubtask(taskId: Long, subtaskId: Long) {
        val taskIndex = tasks.indexOfFirst { it.id == taskId }
        if (taskIndex < 0) return

        val task = tasks[taskIndex]
        val updatedSubtasks = task.subtasks.map { subtask ->
            if (subtask.id == subtaskId) {
                subtask.copy(done = !subtask.done)
            } else {
                subtask
            }
        }
        tasks[taskIndex] = task.copy(subtasks = updatedSubtasks)
    }

    fun getGroups(): List<TaskGroup> {
        return groupingService.groupByPriority(tasks, highFirst)
    }

    fun hasTasks(): Boolean {
        return tasks.isNotEmpty()
    }

    fun productivityStats(): ProductivityStats {
        return statsService.calculate(tasks)
    }

    internal fun clearForTests() {
        tasks.clear()
        nextId = 1L
        nextSubtaskId = 1L
        highFirst = true
    }
}

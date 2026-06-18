package com.example.smartplanner.data

import com.example.smartplanner.model.Task
import com.example.smartplanner.model.TaskPriority
import com.example.smartplanner.model.Subtask
import com.example.smartplanner.ui.tasks.TaskListItem

object TaskRepository {

    private val tasks = mutableListOf<Task>()
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

    fun getGroupedItems(): List<TaskListItem> {
        val sorted = tasks.sortedWith { a, b ->
            val cmp = if (highFirst) {
                b.priority.ordinal.compareTo(a.priority.ordinal)
            } else {
                a.priority.ordinal.compareTo(b.priority.ordinal)
            }
            if (cmp != 0) cmp else a.id.compareTo(b.id)
        }

        val groups = sorted.groupBy { it.priority }
        val priorities = TaskPriority.values().sortedWith { a, b ->
            if (highFirst) b.ordinal.compareTo(a.ordinal) else a.ordinal.compareTo(b.ordinal)
        }

        val result = mutableListOf<TaskListItem>()
        for (p in priorities) {
            val group = groups[p] ?: continue
            result.add(TaskListItem.Header(p))
            for (task in group) {
                result.add(TaskListItem.Row(task))
            }
        }
        return result
    }
}

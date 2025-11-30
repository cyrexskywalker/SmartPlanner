package com.example.smartplanner.ui.tasks

import com.example.smartplanner.model.Task
import com.example.smartplanner.model.TaskPriority

sealed class TaskListItem {
    data class Header(val priority: TaskPriority) : TaskListItem()
    data class Row(val task: Task) : TaskListItem()
}
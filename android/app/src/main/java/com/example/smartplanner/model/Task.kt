package com.example.smartplanner.model

data class Subtask(
    val id: Long,
    val title: String,
    val done: Boolean
)

enum class TaskPriority {
    LOW, MEDIUM, HIGH;

    val title: String
        get() = when (this) {
            LOW -> "Низкий"
            MEDIUM -> "Средний"
            HIGH -> "Высокий"
        }
}

data class Task(
    val id: Long,
    val title: String,
    val description: String?,
    val priority: TaskPriority,
    val flagged: Boolean,
    val deadlineText: String?,
    var done: Boolean,
    val subtasks: List<Subtask>
)

data class TaskGroup(
    val priority: TaskPriority,
    val tasks: List<Task>
)

data class ProductivityStats(
    val totalTasks: Int,
    val completedTasks: Int,
    val activeTasks: Int,
    val completionPercent: Int,
    val highPriorityTasks: Int,
    val flaggedTasks: Int,
    val overdueOrDeadlineTasks: Int,
    val totalSubtasks: Int,
    val completedSubtasks: Int
)

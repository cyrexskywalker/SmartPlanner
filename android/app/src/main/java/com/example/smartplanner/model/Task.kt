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

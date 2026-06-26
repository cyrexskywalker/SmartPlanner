package com.example.smartplanner.data

import com.example.smartplanner.model.ProductivityStats
import com.example.smartplanner.model.Task
import com.example.smartplanner.model.TaskPriority
import kotlin.math.roundToInt

class ProductivityStatsService {
    fun calculate(tasks: List<Task>): ProductivityStats {
        val totalTasks = tasks.size
        val completedTasks = tasks.count { it.done }
        val totalSubtasks = tasks.sumOf { it.subtasks.size }
        val completedSubtasks = tasks.sumOf { task -> task.subtasks.count { it.done } }

        return ProductivityStats(
            totalTasks = totalTasks,
            completedTasks = completedTasks,
            activeTasks = totalTasks - completedTasks,
            completionPercent = completionPercent(completedTasks, totalTasks),
            highPriorityTasks = tasks.count { it.priority == TaskPriority.HIGH },
            flaggedTasks = tasks.count { it.flagged },
            overdueOrDeadlineTasks = tasks.count { it.deadlineText != null },
            totalSubtasks = totalSubtasks,
            completedSubtasks = completedSubtasks
        )
    }

    private fun completionPercent(completed: Int, total: Int): Int {
        if (total == 0) return 0
        return ((completed.toDouble() / total.toDouble()) * 100).roundToInt()
    }
}

package com.example.smartplanner.data

import com.example.smartplanner.model.Subtask
import com.example.smartplanner.model.Task
import com.example.smartplanner.model.TaskPriority
import org.junit.Assert.assertEquals
import org.junit.Test

class ProductivityStatsServiceTest {
    private val service = ProductivityStatsService()

    @Test
    fun calculate_returnsZeroStatsForEmptyTasks() {
        val stats = service.calculate(emptyList())

        assertEquals(0, stats.totalTasks)
        assertEquals(0, stats.completedTasks)
        assertEquals(0, stats.activeTasks)
        assertEquals(0, stats.completionPercent)
        assertEquals(0, stats.highPriorityTasks)
        assertEquals(0, stats.flaggedTasks)
        assertEquals(0, stats.overdueOrDeadlineTasks)
        assertEquals(0, stats.totalSubtasks)
        assertEquals(0, stats.completedSubtasks)
    }

    @Test
    fun calculate_countsTasksPrioritiesFlagsDeadlinesAndSubtasks() {
        val tasks = listOf(
            task(
                id = 1,
                priority = TaskPriority.HIGH,
                done = true,
                flagged = true,
                deadlineText = "18.06 20:00",
                subtasks = listOf(
                    Subtask(id = 1, title = "done", done = true),
                    Subtask(id = 2, title = "active", done = false)
                )
            ),
            task(
                id = 2,
                priority = TaskPriority.MEDIUM,
                done = false,
                subtasks = listOf(
                    Subtask(id = 3, title = "done", done = true)
                )
            ),
            task(
                id = 3,
                priority = TaskPriority.HIGH,
                done = false,
                deadlineText = "19.06 12:00"
            )
        )

        val stats = service.calculate(tasks)

        assertEquals(3, stats.totalTasks)
        assertEquals(1, stats.completedTasks)
        assertEquals(2, stats.activeTasks)
        assertEquals(33, stats.completionPercent)
        assertEquals(2, stats.highPriorityTasks)
        assertEquals(1, stats.flaggedTasks)
        assertEquals(2, stats.overdueOrDeadlineTasks)
        assertEquals(3, stats.totalSubtasks)
        assertEquals(2, stats.completedSubtasks)
    }

    @Test
    fun calculate_roundsCompletionPercentToNearestInt() {
        val tasks = listOf(
            task(id = 1, priority = TaskPriority.LOW, done = true),
            task(id = 2, priority = TaskPriority.LOW, done = true),
            task(id = 3, priority = TaskPriority.LOW, done = false)
        )

        val stats = service.calculate(tasks)

        assertEquals(67, stats.completionPercent)
    }

    private fun task(
        id: Long,
        priority: TaskPriority,
        done: Boolean = false,
        flagged: Boolean = false,
        deadlineText: String? = null,
        subtasks: List<Subtask> = emptyList()
    ): Task {
        return Task(
            id = id,
            title = "Task $id",
            description = null,
            priority = priority,
            flagged = flagged,
            deadlineText = deadlineText,
            done = done,
            subtasks = subtasks
        )
    }
}

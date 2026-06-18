package com.example.smartplanner.data

import com.example.smartplanner.model.Task
import com.example.smartplanner.model.TaskPriority
import org.junit.Assert.assertEquals
import org.junit.Test

class TaskGroupingServiceTest {
    private val service = TaskGroupingService()

    @Test
    fun groupByPriority_sortsHighPriorityFirstAndKeepsIdOrderInsideGroup() {
        val tasks = listOf(
            task(id = 3, priority = TaskPriority.LOW),
            task(id = 2, priority = TaskPriority.HIGH),
            task(id = 1, priority = TaskPriority.HIGH),
            task(id = 4, priority = TaskPriority.MEDIUM)
        )

        val groups = service.groupByPriority(tasks, highFirst = true)

        assertEquals(listOf(TaskPriority.HIGH, TaskPriority.MEDIUM, TaskPriority.LOW), groups.map { it.priority })
        assertEquals(listOf(1L, 2L), groups.first().tasks.map { it.id })
    }

    @Test
    fun groupByPriority_sortsLowPriorityFirstWhenRequested() {
        val tasks = listOf(
            task(id = 1, priority = TaskPriority.HIGH),
            task(id = 2, priority = TaskPriority.LOW),
            task(id = 3, priority = TaskPriority.MEDIUM)
        )

        val groups = service.groupByPriority(tasks, highFirst = false)

        assertEquals(listOf(TaskPriority.LOW, TaskPriority.MEDIUM, TaskPriority.HIGH), groups.map { it.priority })
    }

    @Test
    fun groupByPriority_returnsEmptyListForNoTasks() {
        val groups = service.groupByPriority(emptyList(), highFirst = true)

        assertEquals(emptyList<TaskPriority>(), groups.map { it.priority })
    }

    @Test
    fun groupByPriority_doesNotCreateEmptyPriorityGroups() {
        val tasks = listOf(
            task(id = 1, priority = TaskPriority.MEDIUM),
            task(id = 2, priority = TaskPriority.MEDIUM)
        )

        val groups = service.groupByPriority(tasks, highFirst = true)

        assertEquals(listOf(TaskPriority.MEDIUM), groups.map { it.priority })
        assertEquals(listOf(1L, 2L), groups.first().tasks.map { it.id })
    }

    private fun task(id: Long, priority: TaskPriority): Task {
        return Task(
            id = id,
            title = "Task $id",
            description = null,
            priority = priority,
            flagged = false,
            deadlineText = null,
            done = false,
            subtasks = emptyList()
        )
    }
}

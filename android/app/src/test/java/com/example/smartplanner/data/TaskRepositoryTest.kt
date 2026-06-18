package com.example.smartplanner.data

import com.example.smartplanner.model.Subtask
import com.example.smartplanner.model.Task
import com.example.smartplanner.model.TaskPriority
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TaskRepositoryTest {

    @Before
    fun setUp() {
        TaskRepository.clearForTests()
    }

    @After
    fun tearDown() {
        TaskRepository.clearForTests()
    }

    @Test
    fun getNewId_returnsIncrementalIds() {
        assertEquals(1L, TaskRepository.getNewId())
        assertEquals(2L, TaskRepository.getNewId())
    }

    @Test
    fun addTask_updatesHasTasksAndGroups() {
        TaskRepository.addTask(task(id = 1, priority = TaskPriority.HIGH))

        val groups = TaskRepository.getGroups()

        assertTrue(TaskRepository.hasTasks())
        assertEquals(listOf(TaskPriority.HIGH), groups.map { it.priority })
        assertEquals(listOf(1L), groups.first().tasks.map { it.id })
    }

    @Test
    fun toggleDone_switchesTaskCompletion() {
        TaskRepository.addTask(task(id = 1, priority = TaskPriority.MEDIUM, done = false))

        TaskRepository.toggleDone(1)

        assertTrue(TaskRepository.getGroups().first().tasks.first().done)
    }

    @Test
    fun toggleDone_ignoresUnknownTaskId() {
        TaskRepository.addTask(task(id = 1, priority = TaskPriority.MEDIUM, done = false))

        TaskRepository.toggleDone(999)

        assertFalse(TaskRepository.getGroups().first().tasks.first().done)
    }

    @Test
    fun toggleSubtask_switchesOnlySelectedSubtask() {
        TaskRepository.addTask(
            task(
                id = 1,
                priority = TaskPriority.MEDIUM,
                subtasks = listOf(
                    Subtask(id = 10, title = "first", done = false),
                    Subtask(id = 11, title = "second", done = false)
                )
            )
        )

        TaskRepository.toggleSubtask(taskId = 1, subtaskId = 11)

        val subtasks = TaskRepository.getGroups().first().tasks.first().subtasks
        assertFalse(subtasks[0].done)
        assertTrue(subtasks[1].done)
    }

    @Test
    fun getGroups_respectsHighFirstSetting() {
        TaskRepository.addTask(task(id = 1, priority = TaskPriority.HIGH))
        TaskRepository.addTask(task(id = 2, priority = TaskPriority.LOW))
        TaskRepository.highFirst = false

        val groups = TaskRepository.getGroups()

        assertEquals(listOf(TaskPriority.LOW, TaskPriority.HIGH), groups.map { it.priority })
    }

    private fun task(
        id: Long,
        priority: TaskPriority,
        done: Boolean = false,
        subtasks: List<Subtask> = emptyList()
    ): Task {
        return Task(
            id = id,
            title = "Task $id",
            description = null,
            priority = priority,
            flagged = false,
            deadlineText = null,
            done = done,
            subtasks = subtasks
        )
    }
}

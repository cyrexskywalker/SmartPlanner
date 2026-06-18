package com.example.smartplanner.data

import com.example.smartplanner.model.Task
import com.example.smartplanner.model.TaskGroup
import com.example.smartplanner.model.TaskPriority

class TaskGroupingService {
    fun groupByPriority(tasks: List<Task>, highFirst: Boolean): List<TaskGroup> {
        val sortedTasks = tasks.sortedWith { first, second ->
            val priorityCompare = if (highFirst) {
                second.priority.ordinal.compareTo(first.priority.ordinal)
            } else {
                first.priority.ordinal.compareTo(second.priority.ordinal)
            }

            if (priorityCompare != 0) {
                priorityCompare
            } else {
                first.id.compareTo(second.id)
            }
        }

        val groupedTasks = sortedTasks.groupBy { it.priority }
        val priorities = TaskPriority.values().sortedWith { first, second ->
            if (highFirst) {
                second.ordinal.compareTo(first.ordinal)
            } else {
                first.ordinal.compareTo(second.ordinal)
            }
        }

        return priorities.mapNotNull { priority ->
            groupedTasks[priority]?.let { groupTasks ->
                TaskGroup(priority = priority, tasks = groupTasks)
            }
        }
    }
}

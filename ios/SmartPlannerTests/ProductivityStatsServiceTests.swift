import Foundation
import Testing
@testable import ios

struct ProductivityStatsServiceTests {
    private let service = ProductivityStatsService()

    @Test func calculateReturnsZeroStatsForEmptyTasks() {
        let stats = service.calculate(tasks: [])

        #expect(stats.totalTasks == 0)
        #expect(stats.completedTasks == 0)
        #expect(stats.activeTasks == 0)
        #expect(stats.completionPercent == 0)
        #expect(stats.highPriorityTasks == 0)
        #expect(stats.flaggedTasks == 0)
        #expect(stats.deadlineTasks == 0)
        #expect(stats.totalSubtasks == 0)
        #expect(stats.completedSubtasks == 0)
    }

    @Test func calculateCountsTasksPrioritiesFlagsDeadlinesAndSubtasks() {
        let tasks = [
            makeTask(
                title: "Done important",
                priority: .high,
                isFlagged: true,
                hasDeadline: true,
                isDone: true,
                subtasks: [
                    SubtaskModel(title: "done", isDone: true),
                    SubtaskModel(title: "active", isDone: false)
                ]
            ),
            makeTask(
                title: "Active medium",
                priority: .medium,
                subtasks: [
                    SubtaskModel(title: "done", isDone: true)
                ]
            ),
            makeTask(
                title: "Active important",
                priority: .high,
                hasDeadline: true
            )
        ]

        let stats = service.calculate(tasks: tasks)

        #expect(stats.totalTasks == 3)
        #expect(stats.completedTasks == 1)
        #expect(stats.activeTasks == 2)
        #expect(stats.completionPercent == 33)
        #expect(stats.highPriorityTasks == 2)
        #expect(stats.flaggedTasks == 1)
        #expect(stats.deadlineTasks == 2)
        #expect(stats.totalSubtasks == 3)
        #expect(stats.completedSubtasks == 2)
    }

    @Test func calculateRoundsCompletionPercentToNearestInt() {
        let tasks = [
            makeTask(title: "First", priority: .low, isDone: true),
            makeTask(title: "Second", priority: .low, isDone: true),
            makeTask(title: "Third", priority: .low, isDone: false)
        ]

        let stats = service.calculate(tasks: tasks)

        #expect(stats.completionPercent == 67)
    }

    private func makeTask(
        title: String,
        priority: TaskPriority,
        isFlagged: Bool = false,
        hasDeadline: Bool = false,
        isDone: Bool = false,
        subtasks: [SubtaskModel] = []
    ) -> TaskModel {
        TaskModel(
            title: title,
            priority: priority,
            isFlagged: isFlagged,
            hasDeadline: hasDeadline,
            isDone: isDone,
            subtasks: subtasks
        )
    }
}

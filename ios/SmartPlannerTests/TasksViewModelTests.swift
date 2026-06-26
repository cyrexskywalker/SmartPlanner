import Foundation
import Testing
@testable import ios

struct TasksViewModelTests {

    @Test func addTask_appendsTaskToList() {
        let viewModel = TasksViewModel()
        let task = makeTask(id: UUID(), title: "Write report", priority: .medium)

        viewModel.add(task: task)

        #expect(viewModel.tasks == [task])
    }

    @Test func groupedTasks_sortsHighPriorityFirstByDefault() {
        let viewModel = TasksViewModel()
        viewModel.add(task: makeTask(title: "Low", priority: .low))
        viewModel.add(task: makeTask(title: "High", priority: .high))
        viewModel.add(task: makeTask(title: "Medium", priority: .medium))

        let groups = viewModel.groupedTasks

        #expect(groups.map(\.priority) == [.high, .medium, .low])
    }

    @Test func groupedTasks_sortsLowPriorityFirstWhenRequested() {
        let viewModel = TasksViewModel()
        viewModel.add(task: makeTask(title: "High", priority: .high))
        viewModel.add(task: makeTask(title: "Low", priority: .low))
        viewModel.highFirst = false

        let groups = viewModel.groupedTasks

        #expect(groups.map(\.priority) == [.low, .high])
    }

    @Test func toggleDone_switchesSelectedTaskOnly() throws {
        let viewModel = TasksViewModel()
        let firstId = UUID()
        let secondId = UUID()
        viewModel.add(task: makeTask(id: firstId, title: "First", priority: .medium))
        viewModel.add(task: makeTask(id: secondId, title: "Second", priority: .medium))

        viewModel.toggleDone(id: secondId)

        let first = try #require(viewModel.tasks.first { $0.id == firstId })
        let second = try #require(viewModel.tasks.first { $0.id == secondId })
        #expect(!first.isDone)
        #expect(second.isDone)
    }

    @Test func toggleSubtask_switchesSelectedSubtaskOnly() throws {
        let viewModel = TasksViewModel()
        let taskId = UUID()
        let firstSubtaskId = UUID()
        let secondSubtaskId = UUID()
        viewModel.add(
            task: makeTask(
                id: taskId,
                title: "Task",
                priority: .medium,
                subtasks: [
                    SubtaskModel(id: firstSubtaskId, title: "First"),
                    SubtaskModel(id: secondSubtaskId, title: "Second")
                ]
            )
        )

        viewModel.toggleSubtask(taskId: taskId, subtaskId: secondSubtaskId)

        let task = try #require(viewModel.tasks.first)
        #expect(!task.subtasks[0].isDone)
        #expect(task.subtasks[1].isDone)
    }

    @Test func toggleUnknownIdsDoesNotChangeState() {
        let viewModel = TasksViewModel()
        let task = makeTask(title: "Task", priority: .medium)
        viewModel.add(task: task)

        viewModel.toggleDone(id: UUID())
        viewModel.toggleSubtask(taskId: task.id, subtaskId: UUID())
        viewModel.toggleSubtask(taskId: UUID(), subtaskId: UUID())

        #expect(viewModel.tasks == [task])
    }

    @Test func productivityStatsReflectsCurrentTasks() {
        let viewModel = TasksViewModel()
        viewModel.add(task: makeTask(title: "Done", priority: .high, isDone: true))
        viewModel.add(task: makeTask(title: "Active", priority: .low, isDone: false))

        let stats = viewModel.productivityStats

        #expect(stats.totalTasks == 2)
        #expect(stats.completedTasks == 1)
        #expect(stats.activeTasks == 1)
        #expect(stats.completionPercent == 50)
        #expect(stats.highPriorityTasks == 1)
    }

    private func makeTask(
        id: UUID = UUID(),
        title: String,
        priority: TaskPriority,
        isDone: Bool = false,
        subtasks: [SubtaskModel] = []
    ) -> TaskModel {
        TaskModel(
            id: id,
            title: title,
            priority: priority,
            isDone: isDone,
            subtasks: subtasks
        )
    }
}

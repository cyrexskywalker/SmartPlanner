import Foundation

struct ProductivityStatsService {
    func calculate(tasks: [TaskModel]) -> ProductivityStats {
        let totalTasks = tasks.count
        let completedTasks = tasks.filter(\.isDone).count
        let totalSubtasks = tasks.reduce(0) { $0 + $1.subtasks.count }
        let completedSubtasks = tasks.reduce(0) { total, task in
            total + task.subtasks.filter(\.isDone).count
        }

        return ProductivityStats(
            totalTasks: totalTasks,
            completedTasks: completedTasks,
            activeTasks: totalTasks - completedTasks,
            completionPercent: completionPercent(completed: completedTasks, total: totalTasks),
            highPriorityTasks: tasks.filter { $0.priority == .high }.count,
            flaggedTasks: tasks.filter(\.isFlagged).count,
            deadlineTasks: tasks.filter(\.hasDeadline).count,
            totalSubtasks: totalSubtasks,
            completedSubtasks: completedSubtasks
        )
    }

    private func completionPercent(completed: Int, total: Int) -> Int {
        guard total > 0 else { return 0 }
        return Int((Double(completed) / Double(total) * 100).rounded())
    }
}

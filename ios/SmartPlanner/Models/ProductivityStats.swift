import Foundation

struct ProductivityStats: Equatable {
    let totalTasks: Int
    let completedTasks: Int
    let activeTasks: Int
    let completionPercent: Int
    let highPriorityTasks: Int
    let flaggedTasks: Int
    let deadlineTasks: Int
    let totalSubtasks: Int
    let completedSubtasks: Int
}

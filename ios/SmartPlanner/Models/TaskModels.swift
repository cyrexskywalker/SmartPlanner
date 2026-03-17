import Foundation
import SwiftUI

enum TaskPriority: Int, CaseIterable, Identifiable {
    case low = 0
    case medium = 1
    case high = 2

    var id: Int { rawValue }

    var title: String {
        switch self {
        case .low: return "Низкий"
        case .medium: return "Средний"
        case .high: return "Высокий"
        }
    }

    var color: Color {
        switch self {
        case .low: return .green
        case .medium: return .yellow
        case .high: return .red
        }
    }
}

struct TaskModel: Identifiable, Equatable {
    let id: UUID
    var title: String
    var description: String
    var priority: TaskPriority
    var isFlagged: Bool
    var hasDeadline: Bool
    var deadline: Date?
    var isDone: Bool

    init(
        id: UUID = UUID(),
        title: String,
        description: String = "",
        priority: TaskPriority = .medium,
        isFlagged: Bool = false,
        hasDeadline: Bool = false,
        deadline: Date? = nil,
        isDone: Bool = false
    ) {
        self.id = id
        self.title = title
        self.description = description
        self.priority = priority
        self.isFlagged = isFlagged
        self.hasDeadline = hasDeadline
        self.deadline = deadline
        self.isDone = isDone
    }
}

struct PriorityGroup: Identifiable {
    let priority: TaskPriority
    let tasks: [TaskModel]

    var id: Int { priority.rawValue }
}

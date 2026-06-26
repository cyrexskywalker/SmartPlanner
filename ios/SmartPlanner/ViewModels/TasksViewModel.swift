import Foundation

final class TasksViewModel: ObservableObject {
    @Published var tasks: [TaskModel] = []
    @Published var highFirst: Bool = true
    private let statsService = ProductivityStatsService()

    var groupedTasks: [PriorityGroup] {
        let sorted = tasks.sorted { first, second in
            if highFirst {
                return first.priority.rawValue > second.priority.rawValue
            } else {
                return first.priority.rawValue < second.priority.rawValue
            }
        }

        let dict = Dictionary(grouping: sorted) { $0.priority }

        let priorities = TaskPriority.allCases.sorted { first, second in
            if highFirst {
                return first.rawValue > second.rawValue
            } else {
                return first.rawValue < second.rawValue
            }
        }

        var result: [PriorityGroup] = []
        for p in priorities {
            if let group = dict[p] {
                result.append(PriorityGroup(priority: p, tasks: group))
            }
        }
        return result
    }

    var productivityStats: ProductivityStats {
        statsService.calculate(tasks: tasks)
    }

    func add(task: TaskModel) {
        tasks.append(task)
    }

    func toggleDone(id: UUID) {
        guard let index = tasks.firstIndex(where: { $0.id == id }) else { return }
        tasks[index].isDone.toggle()
    }

    func toggleSubtask(taskId: UUID, subtaskId: UUID) {
        guard let taskIndex = tasks.firstIndex(where: { $0.id == taskId }) else { return }
        guard let subtaskIndex = tasks[taskIndex].subtasks.firstIndex(where: { $0.id == subtaskId }) else {
            return
        }
        tasks[taskIndex].subtasks[subtaskIndex].isDone.toggle()
    }
}

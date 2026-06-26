import SwiftUI

struct ProductivityStatsView: View {
    let stats: ProductivityStats

    var body: some View {
        VStack(alignment: .leading, spacing: 10) {
            Text("Статистика продуктивности")
                .font(.headline)

            VStack(alignment: .leading, spacing: 6) {
                HStack {
                    Text("Выполнено")
                    Spacer()
                    Text("\(stats.completionPercent)%")
                        .bold()
                }
                ProgressView(value: Double(stats.completionPercent), total: 100)
            }

            HStack {
                metric(title: "Всего", value: stats.totalTasks)
                metric(title: "Активных", value: stats.activeTasks)
                metric(title: "Готово", value: stats.completedTasks)
            }

            HStack {
                metric(title: "Важных", value: stats.highPriorityTasks)
                metric(title: "Флаг", value: stats.flaggedTasks)
                metric(title: "Дедлайн", value: stats.deadlineTasks)
            }

            Text("Подзадачи: \(stats.completedSubtasks)/\(stats.totalSubtasks) выполнено")
                .font(.footnote)
                .foregroundStyle(.secondary)
        }
        .padding(14)
        .background(
            RoundedRectangle(cornerRadius: 18)
                .fill(Color.blue.opacity(0.08))
        )
    }

    private func metric(title: String, value: Int) -> some View {
        VStack(alignment: .leading, spacing: 2) {
            Text("\(value)")
                .font(.title3)
                .bold()
            Text(title)
                .font(.caption)
                .foregroundStyle(.secondary)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}

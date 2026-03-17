import SwiftUI

struct ContentView: View {
    @StateObject private var tasksViewModel = TasksViewModel()

    var body: some View {
        TabView {
            Text("Главная")
                .tabItem {
                    Image(systemName: "house")
                    Text("Главная")
                }

            TasksScreen()
                .environmentObject(tasksViewModel)
                .tabItem {
                    Image(systemName: "checklist")
                    Text("Задачи")
                }

            Text("Записи")
                .tabItem {
                    Image(systemName: "square.and.pencil")
                    Text("Записи")
                }
        }
    }
}

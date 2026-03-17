import SwiftUI

struct ContentView: View {
    @StateObject private var tasksViewModel = TasksViewModel()

    var body: some View {
        TabView {
            NewsScreen()
                .tabItem {
                    Image(systemName: "newspaper")
                    Text("Новости")
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

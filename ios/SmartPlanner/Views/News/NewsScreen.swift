import SwiftUI

struct NewsScreen: View {
    @StateObject private var viewModel = NewsViewModel()

    var body: some View {
        NavigationStack {
            Group {
                if viewModel.isLoading && viewModel.articles.isEmpty {
                    ProgressView("Загрузка новостей")
                } else if let errorMessage = viewModel.errorMessage, viewModel.articles.isEmpty {
                    VStack(spacing: 16) {
                        Text(errorMessage)
                            .foregroundColor(.secondary)
                        Button("Повторить") {
                            viewModel.retry()
                        }
                    }
                } else {
                    ScrollView {
                        LazyVStack(spacing: 16) {
                            ForEach(viewModel.articles) { article in
                                NewsRowView(article: article)
                            }
                        }
                        .padding()
                    }
                    .refreshable {
                        viewModel.retry()
                    }
                }
            }
            .navigationTitle("Новости")
        }
        .onAppear {
            viewModel.startRefreshing()
        }
        .onDisappear {
            viewModel.stopRefreshing()
        }
    }
}

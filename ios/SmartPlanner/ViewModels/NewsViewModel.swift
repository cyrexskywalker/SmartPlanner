import Foundation

@MainActor
final class NewsViewModel: ObservableObject {
    @Published private(set) var articles: [NewsArticle] = []
    @Published private(set) var isLoading = false
    @Published private(set) var errorMessage: String?

    private let repository: NewsRepositoryProtocol
    private var refreshTask: Task<Void, Never>?

    init(repository: NewsRepositoryProtocol = NewsRepository()) {
        self.repository = repository
    }

    func startRefreshing() {
        guard refreshTask == nil else { return }
        let cached = repository.cachedArticles()
        if !cached.isEmpty {
            articles = cached
        }

        refreshTask = Task {
            await loadNews()
            while !Task.isCancelled {
                try? await Task.sleep(for: .seconds(120))
                if Task.isCancelled { break }
                await loadNews()
            }
        }
    }

    func stopRefreshing() {
        refreshTask?.cancel()
        refreshTask = nil
    }

    func retry() {
        Task {
            await loadNews()
        }
    }

    private func loadNews() async {
        isLoading = true
        defer { isLoading = false }

        do {
            let fetchedArticles = try await repository.refreshArticles()
            articles = fetchedArticles
            errorMessage = nil
        } catch {
            if articles.isEmpty {
                errorMessage = "Не удалось загрузить новости"
            }
        }
    }
}

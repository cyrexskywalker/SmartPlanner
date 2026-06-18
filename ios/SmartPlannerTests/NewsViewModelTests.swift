import Foundation
import Testing
@testable import ios

@MainActor
struct NewsViewModelTests {

    @Test func startRefreshing_publishesCachedArticlesImmediately() {
        let cachedArticle = makeArticle(title: "Cached article")
        let viewModel = NewsViewModel(
            repository: MockNewsRepository(
                cached: [cachedArticle],
                refreshResult: .success([makeArticle(title: "Fresh article")])
            )
        )

        viewModel.startRefreshing()
        defer { viewModel.stopRefreshing() }

        #expect(viewModel.articles == [cachedArticle])
    }

    @Test func retry_publishesFetchedArticlesAndClearsError() async throws {
        let freshArticle = makeArticle(title: "Fresh article")
        let viewModel = NewsViewModel(
            repository: MockNewsRepository(
                cached: [],
                refreshResult: .success([freshArticle])
            )
        )

        viewModel.retry()
        try await Task.sleep(for: .milliseconds(100))

        #expect(viewModel.articles == [freshArticle])
        #expect(viewModel.errorMessage == nil)
        #expect(!viewModel.isLoading)
    }

    @Test func retry_setsErrorWhenRefreshFailsAndNoArticlesExist() async throws {
        let viewModel = NewsViewModel(
            repository: MockNewsRepository(
                cached: [],
                refreshResult: .failure(URLError(.notConnectedToInternet))
            )
        )

        viewModel.retry()
        try await Task.sleep(for: .milliseconds(100))

        #expect(viewModel.articles.isEmpty)
        #expect(viewModel.errorMessage == "Не удалось загрузить новости")
        #expect(!viewModel.isLoading)
    }

    private func makeArticle(title: String) -> NewsArticle {
        NewsArticle(
            id: title,
            title: title,
            abstract: "Abstract",
            publishedAt: "18.06.2026",
            source: "NYT",
            section: "World",
            imageURL: nil
        )
    }
}

private final class MockNewsRepository: NewsRepositoryProtocol {
    private let cached: [NewsArticle]
    private let refreshResult: Result<[NewsArticle], Error>

    init(cached: [NewsArticle], refreshResult: Result<[NewsArticle], Error>) {
        self.cached = cached
        self.refreshResult = refreshResult
    }

    func cachedArticles() -> [NewsArticle] {
        cached
    }

    func refreshArticles() async throws -> [NewsArticle] {
        try refreshResult.get()
    }
}

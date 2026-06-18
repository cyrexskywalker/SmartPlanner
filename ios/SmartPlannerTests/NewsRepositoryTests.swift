import Foundation
import Testing
@testable import ios

struct NewsRepositoryTests {

    @Test func refreshArticles_fetchesFromServiceAndSavesToCache() async throws {
        let defaults = makeDefaults()
        let cacheStore = NewsCacheStore(defaults: defaults)
        let article = makeArticle(title: "Fresh article")
        let repository = NewsRepository(
            service: MockNewsService(result: .success([article])),
            cacheStore: cacheStore
        )

        let refreshed = try await repository.refreshArticles()

        #expect(refreshed == [article])
        #expect(repository.cachedArticles() == [article])
    }

    @Test func refreshArticles_propagatesServiceErrorAndKeepsCache() async throws {
        let defaults = makeDefaults()
        let cacheStore = NewsCacheStore(defaults: defaults)
        let cachedArticle = makeArticle(title: "Cached article")
        cacheStore.save([cachedArticle])
        let repository = NewsRepository(
            service: MockNewsService(result: .failure(URLError(.notConnectedToInternet))),
            cacheStore: cacheStore
        )

        await #expect(throws: URLError.self) {
            _ = try await repository.refreshArticles()
        }
        #expect(repository.cachedArticles() == [cachedArticle])
    }

    private func makeDefaults() -> UserDefaults {
        let suiteName = "SmartPlannerTests.\(UUID().uuidString)"
        let defaults = UserDefaults(suiteName: suiteName)!
        defaults.removePersistentDomain(forName: suiteName)
        return defaults
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

private struct MockNewsService: NewsServiceProtocol {
    let result: Result<[NewsArticle], Error>

    func fetchTopStories() async throws -> [NewsArticle] {
        try result.get()
    }
}

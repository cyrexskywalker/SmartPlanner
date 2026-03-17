import Foundation

protocol NewsRepositoryProtocol {
    func cachedArticles() -> [NewsArticle]
    func refreshArticles() async throws -> [NewsArticle]
}

final class NewsRepository: NewsRepositoryProtocol {
    private let service: NewsServiceProtocol
    private let cacheStore: NewsCacheStore

    init(
        service: NewsServiceProtocol = NewsService(),
        cacheStore: NewsCacheStore = NewsCacheStore()
    ) {
        self.service = service
        self.cacheStore = cacheStore
    }

    func cachedArticles() -> [NewsArticle] {
        cacheStore.load()
    }

    func refreshArticles() async throws -> [NewsArticle] {
        let articles = try await service.fetchTopStories()
        cacheStore.save(articles)
        return articles
    }
}

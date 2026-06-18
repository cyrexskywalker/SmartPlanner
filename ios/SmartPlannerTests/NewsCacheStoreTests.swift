import Foundation
import Testing
@testable import ios

struct NewsCacheStoreTests {

    @Test func saveAndLoad_returnsFreshArticles() {
        let defaults = makeDefaults()
        let store = NewsCacheStore(defaults: defaults)
        let articles = [makeArticle(title: "Cached article")]

        store.save(articles)

        #expect(store.load() == articles)
    }

    @Test func load_removesCorruptedPayload() {
        let defaults = makeDefaults()
        defaults.set(Data("not-json".utf8), forKey: "news.cache.payload")
        let store = NewsCacheStore(defaults: defaults)

        #expect(store.load().isEmpty)
        #expect(defaults.data(forKey: "news.cache.payload") == nil)
    }

    @Test func load_removesExpiredPayload() throws {
        let defaults = makeDefaults()
        let expiredPayload = CachedNewsPayload(
            savedAt: Date(timeIntervalSinceNow: -25 * 60 * 60),
            articles: [makeArticle(title: "Old article")]
        )
        let data = try JSONEncoder().encode(expiredPayload)
        defaults.set(data, forKey: "news.cache.payload")
        let store = NewsCacheStore(defaults: defaults)

        #expect(store.load().isEmpty)
        #expect(defaults.data(forKey: "news.cache.payload") == nil)
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
            imageURL: URL(string: "https://example.com/image.jpg")
        )
    }
}

import Foundation

struct CachedNewsPayload: Codable {
    let savedAt: Date
    let articles: [NewsArticle]
}

final class NewsCacheStore {
    private let defaults: UserDefaults
    private let cacheKey = "news.cache.payload"
    private let maxAge: TimeInterval = 60 * 60 * 24

    init(defaults: UserDefaults = .standard) {
        self.defaults = defaults
    }

    func load() -> [NewsArticle] {
        guard let data = defaults.data(forKey: cacheKey) else { return [] }
        guard let payload = try? JSONDecoder().decode(CachedNewsPayload.self, from: data) else {
            defaults.removeObject(forKey: cacheKey)
            return []
        }

        if Date().timeIntervalSince(payload.savedAt) > maxAge {
            defaults.removeObject(forKey: cacheKey)
            return []
        }

        return payload.articles
    }

    func save(_ articles: [NewsArticle]) {
        let payload = CachedNewsPayload(savedAt: Date(), articles: articles)
        guard let data = try? JSONEncoder().encode(payload) else { return }
        defaults.set(data, forKey: cacheKey)
    }
}

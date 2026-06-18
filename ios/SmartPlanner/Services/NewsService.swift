import Foundation

protocol NewsServiceProtocol {
    func fetchTopStories() async throws -> [NewsArticle]
}

struct NewsService: NewsServiceProtocol {
    private let session: URLSession
    private let inputDateFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = "yyyy-MM-dd"
        formatter.locale = Locale(identifier: "en_US_POSIX")
        return formatter
    }()
    private let outputDateFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateFormat = "dd.MM.yyyy"
        return formatter
    }()

    init(session: URLSession = .shared) {
        self.session = session
    }

    func fetchTopStories() async throws -> [NewsArticle] {
        let (data, response) = try await session.data(from: NewsAPIConfig.mostPopularURL)
        guard let httpResponse = response as? HTTPURLResponse, 200..<300 ~= httpResponse.statusCode else {
            throw URLError(.badServerResponse)
        }

        let decoder = JSONDecoder()
        let payload = try decoder.decode(MostPopularResponse.self, from: data)

        return payload.results.map { article in
            NewsArticle(
                id: "\(article.title)-\(article.publishedDate)",
                title: article.title,
                abstract: article.abstract,
                publishedAt: formatDate(article.publishedDate),
                source: article.source,
                section: article.section,
                imageURL: article.media?.first?.metadata.last?.url
            )
        }
    }

    private func formatDate(_ value: String) -> String {
        guard let date = inputDateFormatter.date(from: value) else { return value }
        return outputDateFormatter.string(from: date)
    }
}

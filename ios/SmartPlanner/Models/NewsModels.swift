import Foundation

struct NewsArticle: Identifiable, Equatable, Codable {
    let id: String
    let title: String
    let abstract: String
    let publishedAt: String
    let source: String
    let section: String
    let imageURL: URL?
}

struct MostPopularResponse: Decodable {
    let results: [MostPopularArticleDTO]
}

struct MostPopularArticleDTO: Decodable {
    let title: String
    let abstract: String
    let publishedDate: String
    let source: String
    let section: String
    let media: [MostPopularMediaDTO]?

    enum CodingKeys: String, CodingKey {
        case title
        case abstract
        case publishedDate = "published_date"
        case source
        case section
        case media
    }
}

struct MostPopularMediaDTO: Decodable {
    let metadata: [MostPopularMediaMetadataDTO]

    enum CodingKeys: String, CodingKey {
        case metadata = "media-metadata"
    }
}

struct MostPopularMediaMetadataDTO: Decodable {
    let url: URL
}

import Foundation

enum NewsAPIConfig {
    static let apiKey = "XHt4QaJ5V9xoLGfqmuV3aPYuLaNGfp3yaDKdD1o5RNKELjel"
    static let mostPopularURL = URL(string: "https://api.nytimes.com/svc/mostpopular/v2/viewed/1.json?api-key=\(apiKey)")!
}

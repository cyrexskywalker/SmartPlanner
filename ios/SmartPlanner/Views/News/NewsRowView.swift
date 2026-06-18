import SwiftUI

struct NewsRowView: View {
    let article: NewsArticle

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            AsyncImage(url: article.imageURL) { phase in
                switch phase {
                case .success(let image):
                    image
                        .resizable()
                        .scaledToFill()
                case .failure:
                    placeholder(text: "Изображение недоступно")
                case .empty:
                    placeholder(text: "Загрузка изображения")
                @unknown default:
                    placeholder(text: "Загрузка изображения")
                }
            }
            .frame(height: 180)
            .frame(maxWidth: .infinity)
            .clipped()
            .cornerRadius(16)

            Text(article.title)
                .font(.headline)

            Text(article.abstract)
                .font(.subheadline)
                .foregroundColor(.secondary)

            Text("\(article.source) · \(article.section) · \(article.publishedAt)")
                .font(.footnote)
                .foregroundColor(.secondary)
        }
        .padding(16)
        .background(
            RoundedRectangle(cornerRadius: 20)
                .fill(Color.white)
                .shadow(color: Color.black.opacity(0.08), radius: 8, x: 0, y: 3)
        )
    }

    @ViewBuilder
    private func placeholder(text: String) -> some View {
        ZStack {
            RoundedRectangle(cornerRadius: 16)
                .fill(Color.gray.opacity(0.12))
            VStack(spacing: 10) {
                ProgressView()
                Text(text)
                    .font(.footnote)
                    .foregroundColor(.secondary)
            }
        }
    }
}

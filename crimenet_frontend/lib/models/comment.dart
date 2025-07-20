class Comment {
  final int id;
  final String author;
  final String content;
  final String? createdAt;

  Comment({
    required this.id,
    required this.author,
    required this.content,
    this.createdAt,
  });

  factory Comment.fromJson(Map<String, dynamic> json) {
    return Comment(
      id: json['id'],
      author: json['author'] ?? '',
      content: json['content'] ?? '',
      createdAt: json['createdAt'],
    );
  }
}

class Case {
  final int id;
  final String title;
  final String description;
  final String status;
  final String? postedAt;

  Case({
    required this.id,
    required this.title,
    required this.description,
    required this.status,
    this.postedAt,
  });

  factory Case.fromJson(Map<String, dynamic> json) {
    return Case(
      id: json['id'],
      title: json['title'] ?? '',
      description: json['description'] ?? '',
      status: json['status'] ?? '',
      postedAt: json['postedAt'],
    );
  }
}

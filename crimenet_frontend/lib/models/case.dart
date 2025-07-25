class Case {
  final int id;
  final String title;
  final String description;
  final String status;
  final String? postedAt;
  final String? imageUrl;
  final String? mediaUrl;
  final List<String> tags;
  final String? caseType;
  final String? difficulty;

  Case({
    required this.id,
    required this.title,
    required this.description,
    required this.status,
    this.postedAt,
    this.imageUrl,
    this.mediaUrl,
    this.tags = const [],
    this.caseType,
    this.difficulty,
  });

  factory Case.fromJson(Map<String, dynamic> json) {
    return Case(
      id: json['id'],
      title: json['title'] ?? '',
      description: json['description'] ?? '',
      status: json['status'] ?? '',
      postedAt: json['postedAt'],
      imageUrl: json['imageUrl'],
      mediaUrl: json['mediaUrl'],
      tags: json['tags'] != null ? List<String>.from(json['tags']) : [],
      caseType: json['caseType'],
      difficulty: json['difficulty'],
    );
  }
  
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'title': title,
      'description': description,
      'status': status,
      'postedAt': postedAt,
      'imageUrl': imageUrl,
      'mediaUrl': mediaUrl,
      'tags': tags,
      'caseType': caseType,
      'difficulty': difficulty,
    };
  }
}

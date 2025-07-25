import 'package:flutter/material.dart';

enum BadgeType {
  rookie,
  detective,
  expert,
  legend,
  collaborator,
  quickSolver,
  thoroughInvestigator,
  communityHelper,
  firstCase,
  tenCases,
  hundredCases,
  highRated,
  mentor,
  specialist,
}

class Badge {
  final int id;
  final BadgeType type;
  final String name;
  final String description;
  final IconData icon;
  final Color color;
  final String? earnedAt;
  final bool isUnlocked;

  Badge({
    required this.id,
    required this.type,
    required this.name,
    required this.description,
    required this.icon,
    required this.color,
    this.earnedAt,
    this.isUnlocked = false,
  });

  factory Badge.fromJson(Map<String, dynamic> json) {
    return Badge(
      id: json['id'] ?? 0,
      type: BadgeType.values.firstWhere(
        (e) => e.toString().split('.').last == json['type'],
        orElse: () => BadgeType.rookie,
      ),
      name: json['name'] ?? '',
      description: json['description'] ?? '',
      icon: _getIconForBadgeType(json['type']),
      color: _getColorForBadgeType(json['type']),
      earnedAt: json['earnedAt'],
      isUnlocked: json['isUnlocked'] ?? false,
    );
  }

  static IconData _getIconForBadgeType(String? type) {
    switch (type) {
      case 'rookie':
        return Icons.school;
      case 'detective':
        return Icons.search;
      case 'expert':
        return Icons.star;
      case 'legend':
        return Icons.military_tech;
      case 'collaborator':
        return Icons.group;
      case 'quickSolver':
        return Icons.flash_on;
      case 'thoroughInvestigator':
        return Icons.psychology;
      case 'communityHelper':
        return Icons.volunteer_activism;
      case 'firstCase':
        return Icons.flag;
      case 'tenCases':
        return Icons.local_fire_department;
      case 'hundredCases':
        return Icons.emoji_events;
      case 'highRated':
        return Icons.thumb_up;
      case 'mentor':
        return Icons.supervisor_account;
      case 'specialist':
        return Icons.workspace_premium;
      default:
        return Icons.badge;
    }
  }

  static Color _getColorForBadgeType(String? type) {
    switch (type) {
      case 'rookie':
        return Colors.green;
      case 'detective':
        return Colors.blue;
      case 'expert':
        return Colors.purple;
      case 'legend':
        return Colors.amber;
      case 'collaborator':
        return Colors.orange;
      case 'quickSolver':
        return Colors.yellow;
      case 'thoroughInvestigator':
        return Colors.indigo;
      case 'communityHelper':
        return Colors.pink;
      case 'firstCase':
        return Colors.lightGreen;
      case 'tenCases':
        return Colors.red;
      case 'hundredCases':
        return Colors.deepOrange;
      case 'highRated':
        return Colors.cyan;
      case 'mentor':
        return Colors.teal;
      case 'specialist':
        return Colors.deepPurple;
      default:
        return Colors.grey;
    }
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'type': type.toString().split('.').last,
      'name': name,
      'description': description,
      'earnedAt': earnedAt,
      'isUnlocked': isUnlocked,
    };
  }
}

// Predefined badges with their criteria
class BadgeDefinitions {
  static List<Badge> getAllBadges() {
    return [
      Badge(
        id: 1,
        type: BadgeType.rookie,
        name: 'Rookie Investigator',
        description: 'Welcome to CrimeNet! Complete your first case.',
        icon: Icons.school,
        color: Colors.green,
      ),
      Badge(
        id: 2,
        type: BadgeType.detective,
        name: 'Detective',
        description: 'Solve 5 cases successfully.',
        icon: Icons.search,
        color: Colors.blue,
      ),
      Badge(
        id: 3,
        type: BadgeType.expert,
        name: 'Expert Investigator',
        description: 'Solve 25 cases with high ratings.',
        icon: Icons.star,
        color: Colors.purple,
      ),
      Badge(
        id: 4,
        type: BadgeType.legend,
        name: 'Legend',
        description: 'Solve 100 cases and maintain 4.5+ average rating.',
        icon: Icons.military_tech,
        color: Colors.amber,
      ),
      Badge(
        id: 5,
        type: BadgeType.collaborator,
        name: 'Team Player',
        description: 'Participate in 10 collaborative cases.',
        icon: Icons.group,
        color: Colors.orange,
      ),
      Badge(
        id: 6,
        type: BadgeType.quickSolver,
        name: 'Quick Solver',
        description: 'Solve 5 cases within 24 hours of posting.',
        icon: Icons.flash_on,
        color: Colors.yellow,
      ),
      Badge(
        id: 7,
        type: BadgeType.thoroughInvestigator,
        name: 'Thorough Investigator',
        description: 'Provide detailed analysis in 20 cases.',
        icon: Icons.psychology,
        color: Colors.indigo,
      ),
      Badge(
        id: 8,
        type: BadgeType.communityHelper,
        name: 'Community Helper',
        description: 'Help 50 different investigators.',
        icon: Icons.volunteer_activism,
        color: Colors.pink,
      ),
      Badge(
        id: 9,
        type: BadgeType.firstCase,
        name: 'First Case',
        description: 'Complete your very first case.',
        icon: Icons.flag,
        color: Colors.lightGreen,
      ),
      Badge(
        id: 10,
        type: BadgeType.tenCases,
        name: 'Case Crusher',
        description: 'Solve 10 cases.',
        icon: Icons.local_fire_department,
        color: Colors.red,
      ),
      Badge(
        id: 11,
        type: BadgeType.hundredCases,
        name: 'Century Solver',
        description: 'Solve 100 cases.',
        icon: Icons.emoji_events,
        color: Colors.deepOrange,
      ),
      Badge(
        id: 12,
        type: BadgeType.highRated,
        name: 'Highly Rated',
        description: 'Maintain 4.0+ average rating with 10+ ratings.',
        icon: Icons.thumb_up,
        color: Colors.cyan,
      ),
      Badge(
        id: 13,
        type: BadgeType.mentor,
        name: 'Mentor',
        description: 'Guide and help 10 rookie investigators.',
        icon: Icons.supervisor_account,
        color: Colors.teal,
      ),
      Badge(
        id: 14,
        type: BadgeType.specialist,
        name: 'Specialist',
        description: 'Excel in a specific crime category (20+ cases).',
        icon: Icons.workspace_premium,
        color: Colors.deepPurple,
      ),
    ];
  }
}

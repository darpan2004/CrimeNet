import 'package:flutter/material.dart';
import '../constants/app_constants.dart';
import '../models/case.dart';
import '../services/auth_service.dart';
import '../models/user.dart';
import '../widgets/enhanced_card.dart';
import '../widgets/status_badge.dart';
import '../widgets/loading_shimmer.dart';
import 'create_case_screen.dart';
import 'case_detail_screen.dart';
import 'dm_list_screen.dart';

class CasesScreen extends StatefulWidget {
  final User? user;
  const CasesScreen({super.key, this.user});

  @override
  State<CasesScreen> createState() => _CasesScreenState();
}

class _CasesScreenState extends State<CasesScreen> {
  late Future<List<Case>> _casesFuture;
  final AuthService _authService = AuthService();

  @override
  void initState() {
    super.initState();
    //  print("darpan " + widget.user!.role);
    _casesFuture = _authService.fetchCases();
  }

  void _navigateToCreateCase() async {
    final result = await Navigator.of(context).push(
      MaterialPageRoute(
        builder:
            (context) => CreateCaseScreen(
              onCaseCreated: () {
                setState(() {
                  _casesFuture = _authService.fetchCases();
                });
              },
            ),
      ),
    );
    if (result == true) {
      setState(() {
        _casesFuture = _authService.fetchCases();
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final user = widget.user;
    final canPost =
        user != null && (user.role == 'ADMIN' || user.role == 'ORGANIZATION');
    final theme = Theme.of(context);
    return Scaffold(
      appBar: AppBar(
        title: const Text('Active Cases'),
        elevation: 0,
        actions: [
          IconButton(
            icon: const Icon(Icons.message_rounded),
            onPressed: () {
              Navigator.push(
                context,
                MaterialPageRoute(builder: (_) => const DMInboxScreen()),
              );
            },
            tooltip: 'Messages',
          ),
        ],
      ),
      floatingActionButton: canPost
          ? FloatingActionButton.extended(
              onPressed: _navigateToCreateCase,
              icon: const Icon(Icons.add_rounded),
              label: const Text('New Case'),
              tooltip: 'Create New Case',
            )
          : null,
      body: FutureBuilder<List<Case>>(
        future: _casesFuture,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            // Enhanced loading state with shimmer cards
            return Padding(
              padding: const EdgeInsets.all(AppConstants.spacingMd),
              child: Column(
                children: List.generate(
                  5,
                  (index) => Padding(
                    padding: const EdgeInsets.only(bottom: AppConstants.spacingMd),
                    child: ShimmerCard(height: 120),
                  ),
                ),
              ),
            );
          } else if (snapshot.hasError) {
            return Center(
              child: Padding(
                padding: const EdgeInsets.all(AppConstants.spacingLg),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Icon(
                      Icons.error_outline_rounded,
                      size: 64,
                      color: Color(AppConstants.errorColor),
                    ),
                    const SizedBox(height: AppConstants.spacingMd),
                    Text(
                      'Error loading cases',
                      style: theme.textTheme.titleLarge?.copyWith(
                        color: Color(AppConstants.errorColor),
                      ),
                    ),
                    const SizedBox(height: AppConstants.spacingSm),
                    Text(
                      '${snapshot.error}',
                      style: theme.textTheme.bodyMedium,
                      textAlign: TextAlign.center,
                    ),
                  ],
                ),
              ),
            );
          } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
            return Center(
              child: Padding(
                padding: const EdgeInsets.all(AppConstants.spacingLg),
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Icon(
                      Icons.folder_open_rounded,
                      size: 64,
                      color: Color(AppConstants.textLightColor),
                    ),
                    const SizedBox(height: AppConstants.spacingMd),
                    Text(
                      'No cases found',
                      style: theme.textTheme.titleLarge?.copyWith(
                        color: Color(AppConstants.textLightColor),
                      ),
                    ),
                    const SizedBox(height: AppConstants.spacingSm),
                    Text(
                      'Cases will appear here when they are created',
                      style: theme.textTheme.bodyMedium,
                      textAlign: TextAlign.center,
                    ),
                  ],
                ),
              ),
            );
          }
          final cases = snapshot.data!;
          return ListView.builder(
            padding: const EdgeInsets.all(AppConstants.spacingMd),
            itemCount: cases.length,
            itemBuilder: (context, index) {
              final c = cases[index];
              return Padding(
                padding: const EdgeInsets.only(bottom: AppConstants.spacingMd),
                child: EnhancedCard(
                  onTap: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (context) => CaseDetailScreen(caseItem: c),
                      ),
                    );
                  },
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      // Case header with image and title
                      Row(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          // Enhanced case image or icon
                          Container(
                            width: 80,
                            height: 80,
                            decoration: BoxDecoration(
                              borderRadius: BorderRadius.circular(AppConstants.radiusLg),
                              gradient: c.imageUrl != null && c.imageUrl!.isNotEmpty
                                  ? null
                                  : LinearGradient(
                                      begin: Alignment.topLeft,
                                      end: Alignment.bottomRight,
                                      colors: [
                                        Color(AppConstants.primaryColor),
                                        Color(AppConstants.secondaryColor),
                                      ],
                                    ),
                            ),
                            child: c.imageUrl != null && c.imageUrl!.isNotEmpty
                                ? ClipRRect(
                                    borderRadius: BorderRadius.circular(AppConstants.radiusLg),
                                    child: Image.network(
                                      c.imageUrl!,
                                      width: 80,
                                      height: 80,
                                      fit: BoxFit.cover,
                                      errorBuilder: (context, error, stack) => Container(
                                        decoration: BoxDecoration(
                                          borderRadius: BorderRadius.circular(AppConstants.radiusLg),
                                          gradient: LinearGradient(
                                            begin: Alignment.topLeft,
                                            end: Alignment.bottomRight,
                                            colors: [
                                              Color(AppConstants.primaryColor),
                                              Color(AppConstants.secondaryColor),
                                            ],
                                          ),
                                        ),
                                        child: const Icon(
                                          Icons.folder_rounded,
                                          color: Colors.white,
                                          size: 40,
                                        ),
                                      ),
                                    ),
                                  )
                                : const Icon(
                                    Icons.folder_rounded,
                                    color: Colors.white,
                                    size: 40,
                                  ),
                          ),
                          const SizedBox(width: AppConstants.spacingMd),
                          // Case details
                          Expanded(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                Text(
                                  c.title,
                                  style: theme.textTheme.titleMedium?.copyWith(
                                    fontWeight: FontWeight.w600,
                                  ),
                                  maxLines: 2,
                                  overflow: TextOverflow.ellipsis,
                                ),
                                const SizedBox(height: AppConstants.spacingSm),
                                Text(
                                  c.description,
                                  style: theme.textTheme.bodyMedium?.copyWith(
                                    color: Color(AppConstants.textLightColor),
                                  ),
                                  maxLines: 2,
                                  overflow: TextOverflow.ellipsis,
                                ),
                              ],
                            ),
                          ),
                        ],
                      ),
                      const SizedBox(height: AppConstants.spacingMd),
                      // Status and timestamp row
                      Row(
                        children: [
                          StatusBadge(status: c.status),
                          const Spacer(),
                          if (c.postedAt != null) ...[
                            Icon(
                              Icons.access_time_rounded,
                              size: 16,
                              color: Color(AppConstants.textLightColor),
                            ),
                            const SizedBox(width: AppConstants.spacingSm),
                            Text(
                              c.postedAt!,
                              style: theme.textTheme.bodySmall?.copyWith(
                                color: Color(AppConstants.textLightColor),
                              ),
                            ),
                          ],
                        ],
                      ),
                    ],
                  ),
                ),
              );
            },
          );
        },
      ),
    );
  }
}

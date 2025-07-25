import 'package:flutter/material.dart';
import '../models/case.dart';
import '../models/comment.dart';
import '../services/auth_service.dart';
import 'package:video_player/video_player.dart';
import '../screens/profile_screen.dart'; // Added import for ProfileScreen

class CaseDetailScreen extends StatefulWidget {
  final Case caseItem;
  const CaseDetailScreen({super.key, required this.caseItem});

  @override
  State<CaseDetailScreen> createState() => _CaseDetailScreenState();
}

class _CaseDetailScreenState extends State<CaseDetailScreen> {
  late Future<List<Comment>> _commentsFuture;
  final AuthService _authService = AuthService();
  final TextEditingController _commentController = TextEditingController();
  bool _isPosting = false;
  VideoPlayerController? _videoController;
  bool _videoInitialized = false;

  @override
  void initState() {
    super.initState();
    // Print all case details when the screen is opened
    final c = widget.caseItem;
    print(
      'Case details: id= [33m${c.id} [0m, title=${c.title}, description=${c.description}, status=${c.status}, postedAt=${c.postedAt}, imageUrl=${c.imageUrl}, mediaUrl=${c.mediaUrl}',
    );
    _commentsFuture = _authService.fetchCaseComments(widget.caseItem.id);

    _initVideo();
  }

  void _initVideo() {
    final url = widget.caseItem.mediaUrl;
    if (url != null &&
        (url.endsWith('.mp4') ||
            url.endsWith('.webm') ||
            url.endsWith('.mov'))) {
      _videoController = VideoPlayerController.network(url)
        ..initialize().then((_) {
          setState(() {
            _videoInitialized = true;
          });
        });
    }
  }

  @override
  void dispose() {
    _commentController.dispose();
    _videoController?.dispose();
    super.dispose();
  }

  Future<void> _postComment() async {
    final content = _commentController.text.trim();
    if (content.isEmpty) return;
    setState(() {
      _isPosting = true;
    });
    try {
      await _authService.postCaseComment(widget.caseItem.id, content);
      setState(() {
        _commentsFuture = _authService.fetchCaseComments(widget.caseItem.id);
        _commentController.clear();
      });
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Failed to post comment: ${e.toString()}')),
      );
    } finally {
      setState(() {
        _isPosting = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    final c = widget.caseItem;
    final theme = Theme.of(context);
    return Scaffold(
      appBar: AppBar(title: const Text('Case Details')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              c.title,
              style: theme.textTheme.displayLarge?.copyWith(fontSize: 26),
            ),
            const SizedBox(height: 8),
            Row(
              children: [
                Container(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 10,
                    vertical: 4,
                  ),
                  decoration: BoxDecoration(
                    color: theme.colorScheme.primary.withOpacity(0.15),
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Text(
                    c.status,
                    style: theme.textTheme.labelLarge?.copyWith(
                      color: theme.colorScheme.primary,
                    ),
                  ),
                ),
                if (c.postedAt != null) ...[
                  const SizedBox(width: 16),
                  Icon(
                    Icons.access_time,
                    size: 18,
                    color: theme.colorScheme.onSurface.withOpacity(0.6),
                  ),
                  const SizedBox(width: 4),
                  Text(
                    c.postedAt!,
                    style: theme.textTheme.bodySmall?.copyWith(
                      color: theme.colorScheme.onSurface.withOpacity(0.6),
                    ),
                  ),
                ],
              ],
            ),
            const SizedBox(height: 16),
            if (c.imageUrl != null && c.imageUrl!.isNotEmpty)
              Card(
                margin: EdgeInsets.zero,
                clipBehavior: Clip.antiAlias,
                child: Image.network(
                  c.imageUrl!,
                  height: 220,
                  width: double.infinity,
                  fit: BoxFit.cover,
                  errorBuilder:
                      (context, error, stack) => Container(
                        height: 220,
                        color: theme.colorScheme.surface,
                        child: Icon(
                          Icons.broken_image,
                          size: 48,
                          color: theme.colorScheme.primary,
                        ),
                      ),
                ),
              ),
            if (c.mediaUrl != null && c.mediaUrl!.isNotEmpty) ...[
              const SizedBox(height: 12),
              Card(
                margin: EdgeInsets.zero,
                clipBehavior: Clip.antiAlias,
                child:
                    _videoController != null && _videoInitialized
                        ? AspectRatio(
                          aspectRatio: _videoController!.value.aspectRatio,
                          child: VideoPlayer(_videoController!),
                        )
                        : _videoController != null
                        ? const Center(
                          child: Padding(
                            padding: EdgeInsets.all(16.0),
                            child: CircularProgressIndicator(),
                          ),
                        )
                        : Padding(
                          padding: const EdgeInsets.all(16.0),
                          child: Text(
                            'Media: ${c.mediaUrl}',
                            style: theme.textTheme.bodyMedium?.copyWith(
                              color: theme.colorScheme.primary,
                            ),
                          ),
                        ),
              ),
            ],
            const SizedBox(height: 16),
            Text(
              c.description,
              style: theme.textTheme.bodyMedium?.copyWith(fontSize: 17),
            ),
            const SizedBox(height: 24),
            const Divider(),
            Text('Comments', style: theme.textTheme.titleLarge),
            const SizedBox(height: 8),
            FutureBuilder<List<Comment>>(
              future: _commentsFuture,
              builder: (context, snapshot) {
                if (snapshot.connectionState == ConnectionState.waiting) {
                  return const Center(child: CircularProgressIndicator());
                } else if (snapshot.hasError) {
                  return Text(
                    'Error loading comments: \\${snapshot.error}',
                    style: const TextStyle(color: Colors.red),
                  );
                } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
                  return Text(
                    'No comments yet.',
                    style: theme.textTheme.bodyMedium?.copyWith(
                      color: theme.colorScheme.onSurface.withOpacity(0.5),
                    ),
                  );
                }
                final comments = snapshot.data!;
                return Column(
                  children: comments.map((c) => _buildCommentItem(c)).toList(),
                );
              },
            ),
            const SizedBox(height: 16),
            Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: _commentController,
                    decoration: InputDecoration(
                      hintText: 'Add a comment...',
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(12),
                      ),
                      contentPadding: const EdgeInsets.symmetric(
                        horizontal: 12,
                        vertical: 8,
                      ),
                      filled: true,
                      fillColor: theme.cardColor,
                    ),
                    minLines: 1,
                    maxLines: 3,
                    enabled: !_isPosting,
                  ),
                ),
                const SizedBox(width: 8),
                FilledButton(
                  onPressed: _isPosting ? null : _postComment,
                  style: FilledButton.styleFrom(
                    backgroundColor: theme.colorScheme.primary,
                    foregroundColor: Colors.white,
                    padding: const EdgeInsets.symmetric(
                      horizontal: 18,
                      vertical: 14,
                    ),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(12),
                    ),
                  ),
                  child:
                      _isPosting
                          ? const SizedBox(
                            width: 16,
                            height: 16,
                            child: CircularProgressIndicator(
                              strokeWidth: 2,
                              color: Colors.white,
                            ),
                          )
                          : const Text('Post'),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildCommentItem(Comment comment) {
    final theme = Theme.of(context);
    return GestureDetector(
      onTap: () {
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (_) => ProfileScreen(userId: comment.userId),
          ),
        );
      },
      child: Card(
        margin: const EdgeInsets.symmetric(vertical: 6),
        elevation: 1,
        child: ListTile(
          leading: CircleAvatar(
            backgroundColor: theme.colorScheme.primary.withOpacity(0.15),
            child: const Icon(Icons.person, color: Colors.white),
          ),
          title: Text(
            comment.author.isNotEmpty ? comment.author : 'Unknown',
            style: theme.textTheme.labelLarge,
          ),
          subtitle: Text(comment.content, style: theme.textTheme.bodyMedium),
        ),
      ),
    );
  }
}

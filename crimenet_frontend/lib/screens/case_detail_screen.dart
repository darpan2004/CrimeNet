import 'package:flutter/material.dart';
import '../models/case.dart';
import '../models/comment.dart';
import '../services/auth_service.dart';
import 'package:video_player/video_player.dart';

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
    return Scaffold(
      appBar: AppBar(title: const Text('Case Details')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              c.title,
              style: const TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 8),
            Text('Status: ${c.status}', style: const TextStyle(fontSize: 16)),
            if (c.postedAt != null) ...[
              const SizedBox(height: 4),
              Text(
                'Posted at: ${c.postedAt}',
                style: const TextStyle(fontSize: 14, color: Colors.grey),
              ),
            ],
            const SizedBox(height: 16),
            if (c.imageUrl != null)
              Image.network(c.imageUrl!, height: 200, fit: BoxFit.cover),
            if (c.mediaUrl != null) ...[
              const SizedBox(height: 8),
              if (_videoController != null && _videoInitialized)
                AspectRatio(
                  aspectRatio: _videoController!.value.aspectRatio,
                  child: VideoPlayer(_videoController!),
                )
              else if (_videoController != null)
                const Center(child: CircularProgressIndicator())
              else
                Text(
                  'Media: ${c.mediaUrl}',
                  style: const TextStyle(fontSize: 14, color: Colors.blue),
                ),
            ],
            const SizedBox(height: 16),
            Text(c.description, style: const TextStyle(fontSize: 16)),
            const SizedBox(height: 24),
            const Divider(),
            const Text(
              'Comments',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 8),
            FutureBuilder<List<Comment>>(
              future: _commentsFuture,
              builder: (context, snapshot) {
                if (snapshot.connectionState == ConnectionState.waiting) {
                  return const Center(child: CircularProgressIndicator());
                } else if (snapshot.hasError) {
                  return Text(
                    'Error loading comments: ${snapshot.error}',
                    style: const TextStyle(color: Colors.red),
                  );
                } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
                  return const Text(
                    'No comments yet.',
                    style: TextStyle(color: Colors.grey),
                  );
                }
                final comments = snapshot.data!;
                return Column(
                  children:
                      comments
                          .map(
                            (c) => ListTile(
                              title: Text(c.author),
                              subtitle: Text(c.content),
                              trailing: Text(c.createdAt ?? ''),
                            ),
                          )
                          .toList(),
                );
              },
            ),
            const SizedBox(height: 16),
            Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: _commentController,
                    decoration: const InputDecoration(
                      hintText: 'Add a comment...',
                      border: OutlineInputBorder(),
                      contentPadding: EdgeInsets.symmetric(
                        horizontal: 12,
                        vertical: 8,
                      ),
                    ),
                    minLines: 1,
                    maxLines: 3,
                    enabled: !_isPosting,
                  ),
                ),
                const SizedBox(width: 8),
                ElevatedButton(
                  onPressed: _isPosting ? null : _postComment,
                  child:
                      _isPosting
                          ? const SizedBox(
                            width: 16,
                            height: 16,
                            child: CircularProgressIndicator(strokeWidth: 2),
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
}

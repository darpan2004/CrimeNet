import 'package:flutter/material.dart';
import '../models/case.dart';
import '../services/auth_service.dart';
import '../models/user.dart';
import 'package:shared_preferences/shared_preferences.dart';

class CasesScreen extends StatefulWidget {
  final User? user;
  const CasesScreen({Key? key, this.user}) : super(key: key);

  @override
  State<CasesScreen> createState() => _CasesScreenState();
}

class _CasesScreenState extends State<CasesScreen> {
  late Future<List<Case>> _casesFuture;
  final AuthService _authService = AuthService();

  @override
  void initState() {
    super.initState();
    _casesFuture = _authService.fetchCases();
  }

  void _showAddCaseDialog() {
    final _titleController = TextEditingController();
    final _descController = TextEditingController();
    showDialog(
      context: context,
      builder:
          (context) => AlertDialog(
            title: const Text('Add New Case'),
            content: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                TextField(
                  controller: _titleController,
                  decoration: const InputDecoration(labelText: 'Title'),
                ),
                TextField(
                  controller: _descController,
                  decoration: const InputDecoration(labelText: 'Description'),
                ),
              ],
            ),
            actions: [
              TextButton(
                onPressed: () => Navigator.of(context).pop(),
                child: const Text('Cancel'),
              ),
              ElevatedButton(
                onPressed: () async {
                  final caseData = {
                    'title': _titleController.text.trim(),
                    'description': _descController.text.trim(),
                  };
                  try {
                    await _authService.postCase(caseData);
                    setState(() {
                      _casesFuture = _authService.fetchCases();
                    });
                    Navigator.of(context).pop();
                  } catch (e) {
                    ScaffoldMessenger.of(context).showSnackBar(
                      SnackBar(content: Text('Failed to add case: $e')),
                    );
                  }
                },
                child: const Text('Add'),
              ),
            ],
          ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final user = widget.user;
    final canPost =
        user != null && (user.role == 'ADMIN' || user.role == 'ORGANIZATION');
    return Scaffold(
      appBar: AppBar(title: const Text('Cases')),
      floatingActionButton:
          canPost
              ? FloatingActionButton(
                onPressed: _showAddCaseDialog,
                child: const Icon(Icons.add),
                tooltip: 'Add Case',
              )
              : null,
      body: FutureBuilder<List<Case>>(
        future: _casesFuture,
        builder: (context, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting) {
            return const Center(child: CircularProgressIndicator());
          } else if (snapshot.hasError) {
            return Center(child: Text('Error: ${snapshot.error}'));
          } else if (!snapshot.hasData || snapshot.data!.isEmpty) {
            return const Center(child: Text('No cases found.'));
          }
          final cases = snapshot.data!;
          return ListView.builder(
            itemCount: cases.length,
            itemBuilder: (context, index) {
              final c = cases[index];
              return ListTile(
                title: Text(c.title),
                subtitle: Text(c.description),
                trailing: Text(c.status),
              );
            },
          );
        },
      ),
    );
  }
}

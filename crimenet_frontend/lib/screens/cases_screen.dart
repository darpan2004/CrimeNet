import 'package:flutter/material.dart';
import '../models/case.dart';
import '../services/auth_service.dart';
import '../models/user.dart';
import 'create_case_screen.dart';
import 'case_detail_screen.dart';

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
    return Scaffold(
      appBar: AppBar(title: const Text('Cases')),
      floatingActionButton:
          canPost
              ? FloatingActionButton(
                onPressed: _navigateToCreateCase,
                tooltip: 'Add Case',
                child: const Icon(Icons.add),
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
                onTap: () {
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => CaseDetailScreen(caseItem: c),
                    ),
                  );
                },
              );
            },
          );
        },
      ),
    );
  }
}

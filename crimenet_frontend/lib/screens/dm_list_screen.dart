import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import '../models/user.dart';
import 'dm_chat_screen.dart';
import '../constants/app_constants.dart';
import '../providers/auth_provider.dart';
import 'package:provider/provider.dart';

class DMListScreen extends StatefulWidget {
  const DMListScreen({Key? key}) : super(key: key);

  @override
  State<DMListScreen> createState() => _DMListScreenState();
}

class _DMListScreenState extends State<DMListScreen> {
  List<User> eligibleUsers = [];
  bool isLoading = true;

  @override
  void initState() {
    super.initState();
    fetchEligibleUsers();
  }

  Future<void> fetchEligibleUsers() async {
    final authProvider = Provider.of<AuthProvider>(context, listen: false);
    final token = await authProvider.getToken();
    final response = await http.get(
      Uri.parse('${AppConstants.baseUrl}/dm/eligible'),
      headers: {'Authorization': 'Bearer $token'},
    );
    if (response.statusCode == 200) {
      final List<dynamic> data = json.decode(response.body);
      setState(() {
        eligibleUsers = data.map((u) => User.fromJson(u)).toList();
        isLoading = false;
      });
    } else {
      setState(() {
        isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Direct Messages')),
      body:
          isLoading
              ? const Center(child: CircularProgressIndicator())
              : eligibleUsers.isEmpty
              ? const Center(child: Text('No users available for DM.'))
              : ListView.builder(
                itemCount: eligibleUsers.length,
                itemBuilder: (context, index) {
                  final user = eligibleUsers[index];
                  return ListTile(
                    leading: const Icon(Icons.person),
                    title: Text(user.username ?? ''),
                    subtitle: Text(user.email ?? ''),
                    onTap: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (_) => DMChatScreen(peerUser: user),
                        ),
                      );
                    },
                  );
                },
              ),
    );
  }
}

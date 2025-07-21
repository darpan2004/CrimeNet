export 'dm_list_screen.dart' show DMInboxScreen;
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import '../models/user.dart';
import 'dm_chat_screen.dart';
import '../constants/app_constants.dart';
import '../providers/auth_provider.dart';
import 'package:provider/provider.dart';
import '../services/auth_service.dart';

class DMInboxScreen extends StatefulWidget {
  const DMInboxScreen({Key? key}) : super(key: key);

  @override
  State<DMInboxScreen> createState() => _DMInboxScreenState();
}

class _DMInboxScreenState extends State<DMInboxScreen> {
  List<User> dmUsers = [];
  bool isLoading = true;

  @override
  void initState() {
    super.initState();
    fetchDMUsers();
  }

  Future<void> fetchDMUsers() async {
    final token = await AuthService().getToken();
    final response = await http.get(
      Uri.parse('${AppConstants.baseUrl}/dm/history'),
      headers: {'Authorization': 'Bearer $token'},
    );
    if (response.statusCode == 200) {
      final List<dynamic> data = json.decode(response.body);
      setState(() {
        dmUsers = data.map((u) => User.fromJson(u)).toList();
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
      appBar: AppBar(title: const Text('DM Inbox')),
      body:
          isLoading
              ? const Center(child: CircularProgressIndicator())
              : dmUsers.isEmpty
              ? const Center(child: Text('No DMs yet.'))
              : ListView.builder(
                itemCount: dmUsers.length,
                itemBuilder: (context, index) {
                  final user = dmUsers[index];
                  return ListTile(
                    leading: const Icon(Icons.person),
                    title: Text(user.username ?? 'Unknown'),
                    subtitle: Text(user.email ?? 'No email'),
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

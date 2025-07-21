import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';
import '../models/user.dart';
import '../constants/app_constants.dart';
import '../providers/auth_provider.dart';
import 'package:stomp_dart_client/stomp.dart';
import 'package:stomp_dart_client/stomp_config.dart';
import 'package:stomp_dart_client/stomp_frame.dart';

class DMChatScreen extends StatefulWidget {
  final User peerUser;
  const DMChatScreen({Key? key, required this.peerUser}) : super(key: key);

  @override
  State<DMChatScreen> createState() => _DMChatScreenState();
}

class _DMChatScreenState extends State<DMChatScreen> {
  List<dynamic> messages = [];
  final TextEditingController _controller = TextEditingController();
  StompClient? stompClient;
  User? currentUser;

  @override
  void initState() {
    super.initState();
    initChat();
  }

  Future<void> initChat() async {
    final authProvider = Provider.of<AuthProvider>(context, listen: false);
    currentUser = authProvider.user;
    await fetchChatHistory();
    connectWebSocket();
  }

  Future<void> fetchChatHistory() async {
    final authProvider = Provider.of<AuthProvider>(context, listen: false);
    final token = await authProvider.getToken();
    final response = await http.get(
      Uri.parse('${AppConstants.baseUrl}/dm/chat/${widget.peerUser.id}'),
      headers: {'Authorization': 'Bearer $token'},
    );
    if (response.statusCode == 200) {
      setState(() {
        messages = json.decode(response.body);
      });
    }
  }

  void connectWebSocket() {
    stompClient = StompClient(
      config: StompConfig.SockJS(
        url: '${AppConstants.baseUrl.replaceFirst('/api', '')}/ws',
        onConnect: onConnect,
        onWebSocketError: (dynamic error) => print(error),
      ),
    );
    stompClient!.activate();
  }

  void onConnect(StompFrame frame) {
    stompClient!.subscribe(
      destination: '/topic/dm.${currentUser!.id}',
      callback: (frame) {
        if (frame.body != null) {
          final msg = json.decode(frame.body!);
          if ((msg['sender']['id'] == widget.peerUser.id &&
                  msg['receiver']['id'] == currentUser!.id) ||
              (msg['sender']['id'] == currentUser!.id &&
                  msg['receiver']['id'] == widget.peerUser.id)) {
            setState(() {
              messages.add(msg);
            });
          }
        }
      },
    );
  }

  void sendMessage() {
    if (_controller.text.trim().isEmpty || stompClient == null) return;
    final msg = {
      'senderId': currentUser!.id,
      'receiverId': widget.peerUser.id,
      'content': _controller.text.trim(),
    };
    stompClient!.send(destination: '/app/dm.send', body: json.encode(msg));
    _controller.clear();
  }

  @override
  void dispose() {
    stompClient?.deactivate();
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Chat with ${widget.peerUser.username}')),
      body: Column(
        children: [
          Expanded(
            child: ListView.builder(
              itemCount: messages.length,
              itemBuilder: (context, index) {
                final msg = messages[index];
                final isMe = msg['sender']['id'] == currentUser?.id;
                return Align(
                  alignment:
                      isMe ? Alignment.centerRight : Alignment.centerLeft,
                  child: Container(
                    margin: const EdgeInsets.symmetric(
                      vertical: 2,
                      horizontal: 8,
                    ),
                    padding: const EdgeInsets.all(10),
                    decoration: BoxDecoration(
                      color: isMe ? Colors.blue[100] : Colors.grey[200],
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: Text(msg['content'] ?? ''),
                  ),
                );
              },
            ),
          ),
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: Row(
              children: [
                Expanded(
                  child: TextField(
                    controller: _controller,
                    decoration: const InputDecoration(
                      hintText: 'Type a message...',
                    ),
                  ),
                ),
                IconButton(
                  icon: const Icon(Icons.send),
                  onPressed: sendMessage,
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

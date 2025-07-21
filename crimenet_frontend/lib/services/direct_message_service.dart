import 'package:http/http.dart' as http;
import '../constants/app_constants.dart';
import 'auth_service.dart';
import 'dart:convert';

class DirectMessageService {
  Future<bool> isEligibleForDM(int currentUserId, int otherUserId) async {
    final token = await AuthService().getToken();
    final response = await http.get(
      Uri.parse(
        '${AppConstants.baseUrl}/dm/eligible?user1=$currentUserId&user2=$otherUserId',
      ),
      headers: {'Authorization': 'Bearer $token'},
    );
    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      return data['eligible'] == true;
    }
    return false;
  }
}

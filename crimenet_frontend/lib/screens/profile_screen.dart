import 'package:flutter/material.dart';
import '../constants/app_constants.dart';
import 'login_screen.dart';
import '../models/user.dart';
import '../services/auth_service.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../services/direct_message_service.dart';
import 'dm_chat_screen.dart';

class ProfileScreen extends StatefulWidget {
  final int? userId;
  const ProfileScreen({Key? key, this.userId}) : super(key: key);

  @override
  State<ProfileScreen> createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  User? _user;
  bool _isLoading = true;
  String? _error;

  @override
  void initState() {
    super.initState();
    _fetchUser();
  }

  Future<void> _fetchUser() async {
    setState(() {
      _isLoading = true;
      _error = null;
    });
    try {
      User? user;
      if (widget.userId != null) {
        print(widget.userId!.toString() + " darpan");
        user = await AuthService().getUserById(widget.userId!);
        if (user == null &&
            ModalRoute.of(context)?.settings.arguments is String) {
          // Fallback: try to fetch by username if provided as argument
          final username =
              ModalRoute.of(context)?.settings.arguments as String?;
          if (username != null && username.isNotEmpty) {
            user = await AuthService().getUserByUsername(username);
          }
        }
        if (user == null) {
          setState(() {
            _user = null;
            _isLoading = false;
            _error = 'User not found.';
          });
          return;
        }
      } else {
        user = await AuthService().getCurrentUser();
      }
      setState(() {
        _user = user;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _error = e.toString();
        _isLoading = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(AppConstants.backgroundColor),
      appBar: AppBar(
        title: const Text('Profile'),
        backgroundColor: Colors.transparent,
        elevation: 0,
        actions: [
          FutureBuilder<User?>(
            future: AuthService().getCurrentUser(),
            builder: (context, snapshot) {
              if (!snapshot.hasData || snapshot.data == null || _user == null)
                return SizedBox.shrink();
              final currentUserId = snapshot.data!.id;
              if (_user!.id == currentUserId) {
                return IconButton(
                  icon: const Icon(Icons.logout),
                  tooltip: 'Logout',
                  onPressed: () async {
                    final prefs = await SharedPreferences.getInstance();
                    await prefs.remove('jwt_token');
                    if (context.mounted) {
                      Navigator.of(context).pushReplacementNamed('/login');
                    }
                  },
                );
              }
              return SizedBox.shrink();
            },
          ),
        ],
      ),
      body:
          _isLoading
              ? const Center(child: CircularProgressIndicator())
              : _error != null
              ? Center(child: Text('Error: \\$_error'))
              : _user == null
              ? const Center(child: Text('No user data'))
              : SingleChildScrollView(
                padding: const EdgeInsets.all(16.0),
                child: Column(
                  children: [
                    // Profile Header
                    Container(
                      padding: const EdgeInsets.all(24),
                      decoration: BoxDecoration(
                        color: Colors.white,
                        borderRadius: BorderRadius.circular(16),
                        boxShadow: [
                          BoxShadow(
                            color: Colors.black.withOpacity(0.05),
                            blurRadius: 10,
                            offset: const Offset(0, 2),
                          ),
                        ],
                      ),
                      child: Column(
                        children: [
                          CircleAvatar(
                            radius: 50,
                            backgroundColor: const Color(
                              AppConstants.primaryColor,
                            ),
                            child: const Icon(
                              Icons.person,
                              size: 50,
                              color: Colors.white,
                            ),
                          ),
                          const SizedBox(height: 16),
                          Text(
                            _user!.displayName,
                            style: const TextStyle(
                              fontSize: 24,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                          const SizedBox(height: 4),
                          Text(
                            _user!.bio ?? 'No bio',
                            style: TextStyle(
                              fontSize: 16,
                              color: const Color(AppConstants.textLightColor),
                            ),
                          ),
                          const SizedBox(height: 16),
                          Row(
                            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                            children: [
                              _buildStatItem(
                                'Cases Solved',
                                _user!.solvedCasesCount.toString(),
                              ),
                              _buildStatItem(
                                'Rating',
                                _user!.averageRating.toStringAsFixed(1),
                              ),
                              _buildStatItem(
                                'Badges',
                                _user!.badges.length.toString(),
                              ),
                            ],
                          ),
                        ],
                      ),
                    ),
                    const SizedBox(height: 24),

                    // Profile Sections
                    _buildSection('Personal Information', [
                      _buildProfileItem(
                        Icons.email,
                        'Email',
                        _user!.email ?? 'Not provided',
                      ),
                      if (_user!.phoneNumber != null &&
                          _user!.phoneNumber!.isNotEmpty)
                        _buildProfileItem(
                          Icons.phone,
                          'Phone',
                          _user!.phoneNumber!,
                        ),
                      if (_user!.location != null &&
                          _user!.location!.isNotEmpty)
                        _buildProfileItem(
                          Icons.location_on,
                          'Location',
                          _user!.location!,
                        ),
                      if (_user!.experience != null &&
                          _user!.experience!.isNotEmpty)
                        _buildProfileItem(
                          Icons.work,
                          'Experience',
                          _user!.experience!,
                        ),
                    ]),
                    const SizedBox(height: 16),

                    _buildSection('Expertise', [
                      if (_user!.specializationsList.isNotEmpty)
                        _buildProfileItem(
                          Icons.security,
                          'Specialization',
                          _user!.specializationsList.join(', '),
                        ),
                      if (_user!.certifications != null &&
                          _user!.certifications!.isNotEmpty)
                        _buildProfileItem(
                          Icons.school,
                          'Certifications',
                          _user!.certifications!,
                        ),
                      if (_user!.expertise != null)
                        _buildProfileItem(
                          Icons.star,
                          'Skills',
                          _user!.expertise!,
                        ),
                    ]),
                    const SizedBox(height: 16),

                    _buildSection('Account', [
                      _buildProfileItem(
                        Icons.notifications,
                        'Notifications',
                        'Enabled',
                      ),
                      _buildProfileItem(
                        Icons.privacy_tip,
                        'Privacy',
                        'Public Profile',
                      ),
                      _buildProfileItem(Icons.language, 'Language', 'English'),
                    ]),
                    const SizedBox(height: 24),

                    // Action Buttons
                    FutureBuilder<User?>(
                      future: AuthService().getCurrentUser(),
                      builder: (context, snapshot) {
                        if (!snapshot.hasData || snapshot.data == null) {
                          return SizedBox.shrink();
                        }
                        final currentUserId = snapshot.data!.id;
                        if (_user!.id == currentUserId) {
                          return Column(
                            children: [
                              SizedBox(
                                width: double.infinity,
                                child: ElevatedButton(
                                  onPressed: () {},
                                  style: ElevatedButton.styleFrom(
                                    backgroundColor: const Color(
                                      AppConstants.primaryColor,
                                    ),
                                    padding: const EdgeInsets.symmetric(
                                      vertical: 16,
                                    ),
                                    shape: RoundedRectangleBorder(
                                      borderRadius: BorderRadius.circular(12),
                                    ),
                                  ),
                                  child: const Text(
                                    'Edit Profile',
                                    style: TextStyle(
                                      fontSize: 16,
                                      fontWeight: FontWeight.w600,
                                      color: Colors.white,
                                    ),
                                  ),
                                ),
                              ),
                              const SizedBox(height: 12),
                              SizedBox(
                                width: double.infinity,
                                child: OutlinedButton(
                                  onPressed: () {
                                    Navigator.pushReplacement(
                                      context,
                                      MaterialPageRoute(
                                        builder:
                                            (context) => const LoginScreen(),
                                      ),
                                    );
                                  },
                                  style: OutlinedButton.styleFrom(
                                    side: BorderSide(
                                      color: const Color(
                                        AppConstants.accentColor,
                                      ),
                                    ),
                                    padding: const EdgeInsets.symmetric(
                                      vertical: 16,
                                    ),
                                    shape: RoundedRectangleBorder(
                                      borderRadius: BorderRadius.circular(12),
                                    ),
                                  ),
                                  child: Text(
                                    'Logout',
                                    style: TextStyle(
                                      fontSize: 16,
                                      fontWeight: FontWeight.w600,
                                      color: const Color(
                                        AppConstants.accentColor,
                                      ),
                                    ),
                                  ),
                                ),
                              ),
                            ],
                          );
                        } else {
                          // Show prominent Send Message button for other users
                          return _buildChatButton();
                        }
                      },
                    ),
                    const SizedBox(height: 24),
                  ],
                ),
              ),
    );
  }

  Widget _buildStatItem(String label, String value) {
    return Column(
      children: [
        Text(
          value,
          style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
        ),
        Text(
          label,
          style: TextStyle(
            fontSize: 12,
            color: const Color(AppConstants.textLightColor),
          ),
        ),
      ],
    );
  }

  Widget _buildSection(String title, List<Widget> children) {
    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(16),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.05),
            blurRadius: 10,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Padding(
            padding: const EdgeInsets.all(16),
            child: Text(
              title,
              style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
          ),
          ...children,
        ],
      ),
    );
  }

  Widget _buildProfileItem(IconData icon, String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: Row(
        children: [
          Icon(icon, color: const Color(AppConstants.primaryColor), size: 20),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  label,
                  style: TextStyle(
                    fontSize: 12,
                    color: const Color(AppConstants.textLightColor),
                  ),
                ),
                Text(
                  value,
                  style: const TextStyle(
                    fontSize: 14,
                    fontWeight: FontWeight.w500,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildChatButton() {
    if (_user == null) return SizedBox.shrink();
    return FutureBuilder<User?>(
      future: AuthService().getCurrentUser(),
      builder: (context, snapshot) {
        if (!snapshot.hasData || snapshot.data == null)
          return SizedBox.shrink();
        final currentUserId = snapshot.data!.id;
        if (_user!.id == currentUserId) return SizedBox.shrink();
        return FutureBuilder<bool>(
          future: DirectMessageService().isEligibleForDM(
            currentUserId!,
            _user!.id!,
          ),
          builder: (context, snapshot) {
            if (snapshot.connectionState == ConnectionState.waiting) {
              return const SizedBox.shrink();
            }
            if (snapshot.data == true) {
              return SizedBox(
                width: double.infinity,
                child: ElevatedButton.icon(
                  icon: const Icon(Icons.message),
                  onPressed: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (_) => DMChatScreen(peerUser: _user!),
                      ),
                    );
                  },
                  label: const Text('Send Message'),
                  style: ElevatedButton.styleFrom(
                    backgroundColor: const Color(AppConstants.primaryColor),
                    padding: const EdgeInsets.symmetric(vertical: 16),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(12),
                    ),
                  ),
                ),
              );
            }
            return const SizedBox.shrink();
          },
        );
      },
    );
  }
}

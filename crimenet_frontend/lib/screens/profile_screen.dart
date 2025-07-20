import 'package:flutter/material.dart';
import '../constants/app_constants.dart';
import 'login_screen.dart';
import '../models/user.dart';
import '../services/auth_service.dart';
import 'package:shared_preferences/shared_preferences.dart';

class ProfileScreen extends StatefulWidget {
  const ProfileScreen({super.key});

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
      final user = await AuthService().getCurrentUser();
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
          IconButton(
            icon: const Icon(Icons.logout),
            tooltip: 'Logout',
            onPressed: () async {
              final prefs = await SharedPreferences.getInstance();
              await prefs.remove('jwt_token');
              if (context.mounted) {
                Navigator.of(context).pushReplacementNamed('/login');
              }
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
                      _buildProfileItem(Icons.email, 'Email', _user!.email),
                      if (_user!.phoneNumber != null)
                        _buildProfileItem(
                          Icons.phone,
                          'Phone',
                          _user!.phoneNumber!,
                        ),
                      if (_user!.location != null)
                        _buildProfileItem(
                          Icons.location_on,
                          'Location',
                          _user!.location!,
                        ),
                      if (_user!.experience != null)
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
                      if (_user!.certifications != null)
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
                    SizedBox(
                      width: double.infinity,
                      child: ElevatedButton(
                        onPressed: () {},
                        style: ElevatedButton.styleFrom(
                          backgroundColor: const Color(
                            AppConstants.primaryColor,
                          ),
                          padding: const EdgeInsets.symmetric(vertical: 16),
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
                              builder: (context) => const LoginScreen(),
                            ),
                          );
                        },
                        style: OutlinedButton.styleFrom(
                          side: BorderSide(
                            color: const Color(AppConstants.accentColor),
                          ),
                          padding: const EdgeInsets.symmetric(vertical: 16),
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(12),
                          ),
                        ),
                        child: Text(
                          'Logout',
                          style: TextStyle(
                            fontSize: 16,
                            fontWeight: FontWeight.w600,
                            color: const Color(AppConstants.accentColor),
                          ),
                        ),
                      ),
                    ),
                    const SizedBox(height: 24),
                    ElevatedButton(
                      onPressed: () async {
                        try {
                          final user = await AuthService().getCurrentUser();
                          print('User fetched from /me: \\${user?.toJson()}');
                          ScaffoldMessenger.of(context).showSnackBar(
                            SnackBar(
                              content: Text(
                                'User fetched: \\${user?.username ?? 'null'}',
                              ),
                            ),
                          );
                        } catch (e) {
                          print(
                            'Error fetching user from /me: \\${e.toString()}',
                          );
                          ScaffoldMessenger.of(context).showSnackBar(
                            SnackBar(content: Text('Error: \\${e.toString()}')),
                          );
                        }
                      },
                      child: const Text('Fetch /me and Print'),
                    ),
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
}

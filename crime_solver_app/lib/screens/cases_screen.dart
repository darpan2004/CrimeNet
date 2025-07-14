import 'package:flutter/material.dart';
import '../constants/app_constants.dart';

class CasesScreen extends StatelessWidget {
  const CasesScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(AppConstants.backgroundColor),
      appBar: AppBar(
        title: const Text('Cases'),
        backgroundColor: Colors.transparent,
        elevation: 0,
        actions: [
          IconButton(icon: const Icon(Icons.search), onPressed: () {}),
          IconButton(icon: const Icon(Icons.filter_list), onPressed: () {}),
        ],
      ),
      body: ListView(
        padding: const EdgeInsets.all(16.0),
        children: [
          _buildCaseCard(
            'Missing Person Case #123',
            'High Priority',
            'A 25-year-old woman has been missing for 3 days. Last seen at Central Park.',
            '2 hours ago',
            Colors.red,
            Icons.person_search,
          ),
          _buildCaseCard(
            'Bank Robbery Case #89',
            'Medium Priority',
            'Armed robbery at First National Bank. Security footage available.',
            '1 day ago',
            Colors.orange,
            Icons.account_balance,
          ),
          _buildCaseCard(
            'Vandalism Case #67',
            'Low Priority',
            'Graffiti found on public property. Multiple witnesses identified.',
            '3 days ago',
            Colors.green,
            Icons.edit,
          ),
          _buildCaseCard(
            'Fraud Case #45',
            'High Priority',
            'Online scam targeting elderly citizens. Multiple victims reported.',
            '1 week ago',
            Colors.red,
            Icons.computer,
          ),
          _buildCaseCard(
            'Theft Case #34',
            'Medium Priority',
            'Vehicle theft from parking garage. License plate captured.',
            '2 weeks ago',
            Colors.orange,
            Icons.directions_car,
          ),
        ],
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () {},
        backgroundColor: const Color(AppConstants.primaryColor),
        child: const Icon(Icons.add, color: Colors.white),
      ),
    );
  }

  Widget _buildCaseCard(
    String title,
    String priority,
    String description,
    String time,
    Color priorityColor,
    IconData icon,
  ) {
    return Container(
      margin: const EdgeInsets.only(bottom: 16),
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
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Container(
                  padding: const EdgeInsets.all(8),
                  decoration: BoxDecoration(
                    color: priorityColor.withOpacity(0.1),
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Icon(icon, color: priorityColor, size: 20),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        title,
                        style: const TextStyle(
                          fontSize: 16,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      const SizedBox(height: 4),
                      Container(
                        padding: const EdgeInsets.symmetric(
                          horizontal: 8,
                          vertical: 4,
                        ),
                        decoration: BoxDecoration(
                          color: priorityColor.withOpacity(0.1),
                          borderRadius: BorderRadius.circular(12),
                        ),
                        child: Text(
                          priority,
                          style: TextStyle(
                            fontSize: 12,
                            fontWeight: FontWeight.w600,
                            color: priorityColor,
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
                IconButton(icon: const Icon(Icons.more_vert), onPressed: () {}),
              ],
            ),
            const SizedBox(height: 12),
            Text(
              description,
              style: TextStyle(
                fontSize: 14,
                color: const Color(AppConstants.textLightColor),
              ),
            ),
            const SizedBox(height: 12),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  time,
                  style: TextStyle(
                    fontSize: 12,
                    color: const Color(AppConstants.textLightColor),
                  ),
                ),
                Row(
                  children: [
                    Icon(
                      Icons.people,
                      size: 16,
                      color: const Color(AppConstants.textLightColor),
                    ),
                    const SizedBox(width: 4),
                    Text(
                      '3 investigators',
                      style: TextStyle(
                        fontSize: 12,
                        color: const Color(AppConstants.textLightColor),
                      ),
                    ),
                  ],
                ),
              ],
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                Expanded(
                  child: OutlinedButton(
                    onPressed: () {},
                    style: OutlinedButton.styleFrom(
                      side: BorderSide(
                        color: const Color(AppConstants.primaryColor),
                      ),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(8),
                      ),
                    ),
                    child: Text(
                      'View Details',
                      style: TextStyle(
                        color: const Color(AppConstants.primaryColor),
                      ),
                    ),
                  ),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: ElevatedButton(
                    onPressed: () {},
                    style: ElevatedButton.styleFrom(
                      backgroundColor: const Color(AppConstants.primaryColor),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(8),
                      ),
                    ),
                    child: const Text(
                      'Join Case',
                      style: TextStyle(color: Colors.white),
                    ),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

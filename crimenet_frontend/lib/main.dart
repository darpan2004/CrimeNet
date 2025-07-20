import 'package:flutter/material.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:provider/provider.dart';
import 'constants/app_constants.dart';
import 'providers/auth_provider.dart';
import 'screens/login_screen.dart';
import 'screens/home_screen.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(const CrimeSolverApp());
}

class CrimeSolverApp extends StatelessWidget {
  const CrimeSolverApp({super.key});

  @override
  Widget build(BuildContext context) {
    return FutureBuilder(
      future: Firebase.initializeApp(),
      builder: (context, snapshot) {
        if (snapshot.connectionState == ConnectionState.waiting) {
          return MaterialApp(
            home: Scaffold(body: Center(child: CircularProgressIndicator())),
          );
        } else if (snapshot.hasError) {
          return MaterialApp(
            home: Scaffold(
              body: Center(
                child: Text('Firebase init error: \\n${snapshot.error}'),
              ),
            ),
          );
        }
        // Firebase initialized, show app
        return MultiProvider(
          providers: [ChangeNotifierProvider(create: (_) => AuthProvider())],
          child: MaterialApp(
            title: 'Crime Solver',
            debugShowCheckedModeBanner: false,
            theme: ThemeData(
              primarySwatch: Colors.blue,
              primaryColor: const Color(AppConstants.primaryColor),
              scaffoldBackgroundColor: const Color(
                AppConstants.backgroundColor,
              ),
              appBarTheme: const AppBarTheme(
                backgroundColor: Color(AppConstants.primaryColor),
                foregroundColor: Colors.white,
              ),
              textTheme: const TextTheme(
                bodyLarge: TextStyle(color: Color(AppConstants.textColor)),
                bodyMedium: TextStyle(color: Color(AppConstants.textColor)),
              ),
            ),
            home: const AuthWrapper(),
          ),
        );
      },
    );
  }
}

class AuthWrapper extends StatelessWidget {
  const AuthWrapper({super.key});

  @override
  Widget build(BuildContext context) {
    final authProvider = Provider.of<AuthProvider>(context);
    if (authProvider.isLoading) {
      return const Scaffold(body: Center(child: CircularProgressIndicator()));
    } else if (authProvider.user != null) {
      return const HomeScreen();
    } else {
      return const LoginScreen();
    }
  }
}

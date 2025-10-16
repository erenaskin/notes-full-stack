import 'package:flutter/material.dart';
import 'package:go_router/go_router.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:notes/src/features/auth/data/auth_providers.dart';
import 'package:notes/src/features/auth/presentation/screens/login_screen.dart';
import 'package:notes/src/features/auth/presentation/screens/register_screen.dart';
import 'package:notes/src/features/notes/presentation/screens/note_edit_screen.dart';
import 'package:notes/src/features/notes/presentation/screens/notes_list_screen.dart';

final routerProvider = Provider<GoRouter>((ref) {
  final authState = ref.watch(authNotifierProvider);

  return GoRouter(
    initialLocation: '/login',
    debugLogDiagnostics: true,
    redirect: (BuildContext context, GoRouterState state) {
      final isAuthenticated = authState.asData?.value == AuthState.authenticated;
      
      final isLoggingIn = state.matchedLocation == '/login' || state.matchedLocation == '/register';

      // If user is not authenticated and trying to access a protected route, redirect to login.
      if (!isAuthenticated && !isLoggingIn) {
        return '/login';
      }

      // If user is authenticated and trying to access login/register, redirect to notes.
      if (isAuthenticated && isLoggingIn) {
        return '/notes';
      }
      
      return null; // No redirect needed
    },
    routes: [
      GoRoute(
        path: '/login',
        builder: (context, state) => const LoginScreen(),
      ),
      GoRoute(
        path: '/register',
        builder: (context, state) => const RegisterScreen(),
      ),
      GoRoute(
        path: '/notes',
        builder: (context, state) => const NotesListScreen(),
      ),
      GoRoute(
        path: '/note/new',
        builder: (context, state) => const NoteEditScreen(),
      ),
      GoRoute(
        path: '/note/:id',
        builder: (context, state) {
          final id = int.tryParse(state.pathParameters['id'] ?? '');
          if (id != null) {
            return NoteEditScreen(noteId: id);
          }
          // Handle error case, e.g., redirect to notes list if ID is invalid
          return const NotesListScreen();
        },
      ),
    ],
  );
});

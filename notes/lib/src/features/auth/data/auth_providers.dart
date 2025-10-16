import 'dart:async';

import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';
import 'package:notes/src/core/api/dio_client.dart';
import 'package:notes/src/core/storage/secure_storage_service.dart';
import 'package:notes/src/features/auth/data/auth_repository.dart';

// 1. Core Services Providers
final flutterSecureStorageProvider = Provider((ref) => const FlutterSecureStorage());

final secureStorageServiceProvider = Provider(
  (ref) => SecureStorageService(ref.watch(flutterSecureStorageProvider)),
);

final dioClientProvider = Provider(
  (ref) => DioClient(ref.watch(secureStorageServiceProvider)),
);

final dioProvider = Provider((ref) => ref.watch(dioClientProvider).dio);

// 2. Auth Repository Provider
final authRepositoryProvider = Provider(
  (ref) => AuthRepository(
    ref.watch(dioProvider),
    ref.watch(secureStorageServiceProvider),
  ),
);

// 3. Auth State Enum
enum AuthState { unknown, authenticated, unauthenticated }

// 4. Auth State Notifier Provider
final authNotifierProvider =
    AsyncNotifierProvider<AuthNotifier, AuthState>(AuthNotifier.new);

class AuthNotifier extends AsyncNotifier<AuthState> {
  late AuthRepository _authRepository;

  @override
  FutureOr<AuthState> build() {
    _authRepository = ref.watch(authRepositoryProvider);
    _checkToken();
    return AuthState.unknown;
  }

  Future<void> _checkToken() async {
    state = const AsyncValue.loading();
    final token = await ref.read(secureStorageServiceProvider).readToken();
    state =
        AsyncValue.data(token != null ? AuthState.authenticated : AuthState.unauthenticated);
  }

  Future<void> login(String username, String password) async {
    state = const AsyncValue.loading();
    try {
      await _authRepository.login(username, password);
      state = const AsyncValue.data(AuthState.authenticated);
    } catch (e) {
      state = AsyncValue.error(e, StackTrace.current);
      // Revert to unauthenticated on error
      state = const AsyncValue.data(AuthState.unauthenticated);
    }
  }

  Future<void> register({
    required String username,
    required String password,
    required String email,
  }) async {
    state = const AsyncValue.loading();
    try {
      await _authRepository.register(
          username: username, password: password, email: email);
      // Optionally, you can log the user in directly after registration
      // await login(username, password);
       state = const AsyncValue.data(AuthState.unauthenticated); // Stay on register/login page
    } catch (e) {
       state = AsyncValue.error(e, StackTrace.current);
       state = const AsyncValue.data(AuthState.unauthenticated);
    }
  }

  Future<void> logout() async {
    state = const AsyncValue.loading();
    await _authRepository.logout();
    state = const AsyncValue.data(AuthState.unauthenticated);
  }
}

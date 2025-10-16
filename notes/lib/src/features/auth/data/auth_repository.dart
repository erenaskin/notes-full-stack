import 'package:dio/dio.dart';
import 'package:notes/src/core/storage/secure_storage_service.dart';

class AuthRepository {
  AuthRepository(this._dio, this._secureStorageService);

  final Dio _dio;
  final SecureStorageService _secureStorageService;

  Future<void> login(String username, String password) async {
    try {
      final response = await _dio.post(
        '/api/auth/login',
        data: {
          'username': username,
          'password': password,
        },
      );

      print('Login response data: ${response.data}'); // <-- Bu satır eklendi

      if (response.statusCode == 200 && response.data != null) {
        final token = response.data['token']; // Adjust based on your API response
        if (token != null) {
          await _secureStorageService.saveToken(token);
        } else {
          throw Exception('Token not found in response');
        }
      } else {
        throw Exception('Failed to login');
      }
    } on DioException catch (e) {
      // Handle Dio-specific errors
      throw Exception('Failed to login: ${e.message}');
    }
  }

  Future<void> register({
    required String username,
    required String password,
    required String email,
  }) async {
    try {
      final response = await _dio.post(
        '/api/auth/register',
        data: {
          'username': username,
          'password': password,
          'email': email,
        },
      );

      // Backend returns 200 OK for successful registration
      if (response.statusCode != 200) {
        throw Exception('Failed to register');
      }
    } on DioException catch (e) {
      throw Exception('Failed to register: ${e.message}');
    }
  }

  Future<void> logout() async {
    await _secureStorageService.deleteToken();
  }
}

import 'package:dio/dio.dart';
import 'package:notes/src/core/storage/secure_storage_service.dart';

class DioClient {
  DioClient(this._secureStorageService) {
    _dio = Dio(
      BaseOptions(
        baseUrl: 'http://localhost:8080', // TODO: Replace with your API base URL
        connectTimeout: const Duration(milliseconds: 5000),
        receiveTimeout: const Duration(milliseconds: 3000),
      ),
    )..interceptors.add(
        InterceptorsWrapper(
          onRequest: (options, handler) async {
            final token = await _secureStorageService.readToken();
            if (token != null) {
              options.headers['Authorization'] = 'Bearer $token';
              // print('Sending token for ${options.path}: $token'); // Removed print statement
            } else {
              // print('No token found for request to ${options.path}'); // Removed print statement
            }
            return handler.next(options);
          },
        ),
      );
  }

  final SecureStorageService _secureStorageService;
  late final Dio _dio;

  Dio get dio => _dio;
}

import 'package:dio/dio.dart';
import 'package:notes/src/features/notes/data/note_model.dart';

class NoteRepository {
  NoteRepository(this._dio);
  final Dio _dio;

  Future<List<Note>> getNotes() async {
    try {
      final response = await _dio.get('/api/notes');
      final data = response.data as List;
      return data.map((item) => Note.fromJson(item)).toList();
    } catch (e) {
      throw Exception('Failed to fetch notes: $e');
    }
  }

  Future<Note> getNoteById(int id) async {
    try {
      final response = await _dio.get('/api/notes/$id');
      return Note.fromJson(response.data);
    } catch (e) {
      throw Exception('Failed to fetch note: $e');
    }
  }


  Future<Note> createNote({required String title, required String content}) async {
    try {
      final response = await _dio.post(
        '/api/notes',
        data: {'title': title, 'content': content},
      );
      return Note.fromJson(response.data);
    } catch (e) {
      throw Exception('Failed to create note: $e');
    }
  }

  Future<Note> updateNote({
    required int id,
    required String title,
    required String content,
  }) async {
    try {
      final response = await _dio.put(
        '/api/notes/$id',
        data: {'title': title, 'content': content},
      );
      return Note.fromJson(response.data);
    } catch (e) {
      throw Exception('Failed to update note: $e');
    }
  }

  Future<void> deleteNote(int id) async {
    try {
      await _dio.delete('/api/notes/$id');
    } catch (e) {
      throw Exception('Failed to delete note: $e');
    }
  }
}

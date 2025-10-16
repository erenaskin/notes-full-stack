import 'dart:async';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:notes/src/features/auth/data/auth_providers.dart';
import 'package:notes/src/features/notes/data/note_model.dart';
import 'note_repository.dart';

// 1. Note Repository Provider
final noteRepositoryProvider = Provider(
  (ref) => NoteRepository(ref.watch(dioProvider)),
);

// 2. Notes List Notifier Provider
final notesProvider =
    AsyncNotifierProvider<NotesNotifier, List<Note>>(NotesNotifier.new);

class NotesNotifier extends AsyncNotifier<List<Note>> {
  @override
  FutureOr<List<Note>> build() {
    // Initially fetch notes
    return _fetchNotes();
  }

  Future<List<Note>> _fetchNotes() async {
    final repository = ref.read(noteRepositoryProvider);
    return repository.getNotes();
  }

  Future<void> addNote({required String title, required String content}) async {
    final repository = ref.read(noteRepositoryProvider);
    state = const AsyncValue.loading();
    try {
      await repository.createNote(title: title, content: content);
      // After adding, refresh the list
      state = await AsyncValue.guard(() => _fetchNotes());
    } catch (e, stack) {
      state = AsyncValue.error(e, stack);
    }
  }

  Future<void> updateNote({
    required int id,
    required String title,
    required String content,
  }) async {
    final repository = ref.read(noteRepositoryProvider);
    state = const AsyncValue.loading();
    try {
      await repository.updateNote(id: id, title: title, content: content);
      // After updating, refresh the list
      state = await AsyncValue.guard(() => _fetchNotes());
    } catch (e, stack) {
      state = AsyncValue.error(e, stack);
    }
  }

  Future<void> deleteNote(int id) async {
    final repository = ref.read(noteRepositoryProvider);
    state = const AsyncValue.loading();
    try {
      await repository.deleteNote(id);
      // After deleting, refresh the list
      state = await AsyncValue.guard(() => _fetchNotes());
    } catch (e, stack) {
      state = AsyncValue.error(e, stack);
    }
  }
}

// 3. Provider for a single note, useful for edit screen
final noteProvider = FutureProvider.autoDispose.family<Note, int>((ref, id) async {
  final repository = ref.watch(noteRepositoryProvider);
  return repository.getNoteById(id);
});

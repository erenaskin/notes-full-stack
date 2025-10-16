import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:notes/src/features/auth/data/auth_providers.dart';
import 'package:notes/src/features/notes/data/note_providers.dart';
import 'package:notes/src/features/notes/presentation/widgets/note_list_item.dart';

class NotesListScreen extends ConsumerWidget {
  const NotesListScreen({super.key});

  @override
  Widget build(BuildContext context, WidgetRef ref) {
    final notesAsyncValue = ref.watch(notesProvider);

    return Scaffold(
      appBar: AppBar(
        title: const Text('My Notes'),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: () {
              ref.read(authNotifierProvider.notifier).logout();
            },
          ),
        ],
      ),
      body: notesAsyncValue.when(
        data: (notes) {
          if (notes.isEmpty) {
            return const Center(child: Text('You have no notes yet.'));
          }
          return ListView.builder(
            itemCount: notes.length,
            itemBuilder: (context, index) {
              final note = notes[index];
              return NoteListItem(
                note: note,
                onTap: () => context.push('/note/${note.id}'), // Değişiklik burada!
              );
            },
          );
        },
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (error, stackTrace) => Center(
          child: Text('Failed to load notes: $error'),
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => context.push('/note/new'), 
        child: const Icon(Icons.add),
      ),
    );
  }
}

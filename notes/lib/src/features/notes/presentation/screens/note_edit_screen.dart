import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import 'package:go_router/go_router.dart';
import 'package:notes/src/features/notes/data/note_providers.dart';

class NoteEditScreen extends ConsumerStatefulWidget {
  const NoteEditScreen({this.noteId, super.key});

  final int? noteId;

  @override
  ConsumerState<NoteEditScreen> createState() => _NoteEditScreenState();
}

class _NoteEditScreenState extends ConsumerState<NoteEditScreen> {
  final _titleController = TextEditingController();
  final _contentController = TextEditingController();
  final _formKey = GlobalKey<FormState>();

  bool get _isEditing => widget.noteId != null;

  @override
  void initState() {
    super.initState();

    if (_isEditing) {
      ref.read(noteProvider(widget.noteId!).future).then((note) {
        _titleController.text = note.title;
        _contentController.text = note.content;
        setState(() {}); 
      }).catchError((error, stackTrace) {
        // Removed SnackBar call as errors are handled in the build method via noteAsyncValue
        // if (mounted) {
        //   ScaffoldMessenger.of(context).showSnackBar(
        //     SnackBar(content: Text('Error fetching note: ${error.toString()}')),
        //   );
        // }
      });
    }
  }

  @override
  void dispose() {
    _titleController.dispose();
    _contentController.dispose();
    super.dispose();
  }

  Future<void> _saveNote() async {
    if (_formKey.currentState!.validate()) {
      final notifier = ref.read(notesProvider.notifier);
      final navigator = GoRouter.of(context); // Store navigator before async call

      final title = _titleController.text;
      final content = _contentController.text;
      
      try {
        if (_isEditing) {
          await notifier.updateNote(id: widget.noteId!, title: title, content: content);
          if (!mounted) return; // Check mounted immediately after async operation
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Note successfully updated!')),
          );
        } else {
          await notifier.addNote(title: title, content: content);
          if (!mounted) return; // Check mounted immediately after async operation
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('Note successfully added!')),
          );
        }
        if (!mounted) return; // Check mounted before using context.pop()
        navigator.pop();
      } catch (e) {
         if (!mounted) return; // Check mounted immediately after async operation
         ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Failed to save note: ${e.toString()}')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final noteAsyncValue = _isEditing ? ref.watch(noteProvider(widget.noteId!)) : null;

    return Scaffold(
      appBar: AppBar(
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => context.pop(),
        ),
        title: Text(_isEditing ? 'Edit Note' : 'New Note'),
        actions: [
          if (_isEditing)
             IconButton(
              icon: const Icon(Icons.delete),
               onPressed: () async {
                 final navigator = GoRouter.of(context); // Capture before async
                 await ref.read(notesProvider.notifier).deleteNote(widget.noteId!);
                 if (!mounted) return;
                 navigator.pop();
               },
            )
        ],
      ),
      body: noteAsyncValue?.when(
        data: (note) => _buildForm(),
        loading: () => const Center(child: CircularProgressIndicator()),
        error: (err, stack) => Center(child: Text('Error: $err')),
      ) ?? _buildForm(),
      floatingActionButton: FloatingActionButton(
        onPressed: _saveNote,
        child: const Icon(Icons.save),
      ),
    );
  }

  Widget _buildForm() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16.0),
      child: Form(
        key: _formKey,
        child: Column(
          children: [
            TextFormField(
              controller: _titleController,
              decoration: const InputDecoration(
                labelText: 'Title',
                border: OutlineInputBorder(),
              ),
              validator: (value) => value!.isEmpty ? 'Please enter a title' : null,
            ),
            const SizedBox(height: 16),
            TextFormField(
              controller: _contentController,
              decoration: const InputDecoration(
                labelText: 'Content',
                border: OutlineInputBorder(),
              ),
               maxLines: 10,
              validator: (value) => value!.isEmpty ? 'Please enter content' : null,
            ),
          ],
        ),
      ),
    );
  }
}

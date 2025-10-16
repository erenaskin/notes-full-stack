import 'package:flutter/material.dart';
import 'package:notes/src/features/notes/data/note_model.dart';

class NoteListItem extends StatelessWidget {
  const NoteListItem({
    required this.note,
    this.onTap,
    super.key,
  });

  final Note note;
  final VoidCallback? onTap;

  @override
  Widget build(BuildContext context) {
    return ListTile(
      title: Text(note.title),
      subtitle: Text(
        note.content,
        maxLines: 2,
        overflow: TextOverflow.ellipsis,
      ),
      onTap: onTap,
    );
  }
}

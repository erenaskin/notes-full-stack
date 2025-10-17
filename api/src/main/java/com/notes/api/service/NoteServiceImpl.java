package com.notes.api.service;

import com.notes.api.domain.entity.Note;
import com.notes.api.domain.entity.User;
import com.notes.api.domain.repository.NoteRepository;
import com.notes.api.domain.repository.UserRepository;
import com.notes.api.web.dto.CreateNoteRequest;
import com.notes.api.web.dto.NoteDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    private User getUserByDetails(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + userDetails.getUsername()));
    }

    @Override
    public List<NoteDto> findAllForUser(UserDetails userDetails) {
        User user = getUserByDetails(userDetails);
        return noteRepository.findByUserAndNotDeleted(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public NoteDto findByIdForUser(Long id, UserDetails userDetails) {
        User user = getUserByDetails(userDetails);
        Note note = noteRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new RuntimeException("Note not found with id: " + id));
        if (!note.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You are not allowed to access this note");
        }
        return convertToDto(note);
    }

    @Override
    public NoteDto create(CreateNoteRequest request, UserDetails userDetails) {
        User user = getUserByDetails(userDetails);
        Note note = Note.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .user(user)
                .build();
        return convertToDto(noteRepository.save(note));
    }

    @Override
    public NoteDto update(Long id, CreateNoteRequest request, UserDetails userDetails) {
        User user = getUserByDetails(userDetails);
        Note note = noteRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new RuntimeException("Note not found with id: " + id));
        if (!note.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You are not allowed to update this note");
        }
        note.setTitle(request.getTitle());
        note.setContent(request.getContent());
        return convertToDto(noteRepository.save(note));
    }

    @Override
    public void delete(Long id, UserDetails userDetails) {
        User user = getUserByDetails(userDetails);
        Note note = noteRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new RuntimeException("Note not found with id: " + id));
        if (!note.getUser().getId().equals(user.getId())) {
            throw new SecurityException("You are not allowed to delete this note");
        }
        // Soft delete: fiziksel olarak silme, sadece işaretle
        note.setIsDeleted(true);
        note.setDeletedAt(java.time.LocalDateTime.now());
        noteRepository.save(note);
    }

    private NoteDto convertToDto(Note note) {
        return NoteDto.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .updatedAt(note.getUpdatedAt())
                .build();
    }
}

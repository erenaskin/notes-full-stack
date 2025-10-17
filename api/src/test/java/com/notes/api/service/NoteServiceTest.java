package com.notes.api.service;

import com.notes.api.domain.entity.Note;
import com.notes.api.domain.entity.User;
import com.notes.api.domain.repository.NoteRepository;
import com.notes.api.domain.repository.UserRepository;
import com.notes.api.web.dto.CreateNoteRequest;
import com.notes.api.web.dto.NoteDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NoteServiceImpl noteService;

    private User user;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("test@example.com");
        
        userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("testuser")
                .password("password")
                .authorities("ROLE_USER")
                .build();
    }

    @Test
    void findAllForUser_shouldReturnNoteDtoList() {
        Note note = Note.builder().id(1L).title("Test Note").content("Test Content").user(user).build();
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(noteRepository.findByUserOrderByUpdatedAtDesc(user)).thenReturn(Collections.singletonList(note));

        List<NoteDto> result = noteService.findAllForUser(userDetails);

        assertEquals(1, result.size());
        assertEquals("Test Note", result.get(0).getTitle());
        verify(userRepository).findByUsername("testuser");
        verify(noteRepository).findByUserOrderByUpdatedAtDesc(user);
    }

    @Test
    void findAllForUser_shouldReturnEmptyList_whenNoNotes() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(noteRepository.findByUserOrderByUpdatedAtDesc(user)).thenReturn(Collections.emptyList());

        List<NoteDto> result = noteService.findAllForUser(userDetails);

        assertTrue(result.isEmpty());
        verify(userRepository).findByUsername("testuser");
        verify(noteRepository).findByUserOrderByUpdatedAtDesc(user);
    }

    @Test
    void findByIdForUser_shouldReturnNoteDto() {
        Note note = Note.builder().id(1L).title("Test Note").content("Test Content").user(user).build();
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(noteRepository.findById(1L)).thenReturn(Optional.of(note));

        NoteDto result = noteService.findByIdForUser(1L, userDetails);

        assertEquals("Test Note", result.getTitle());
        verify(userRepository).findByUsername("testuser");
        verify(noteRepository).findById(1L);
    }

    @Test
    void findByIdForUser_shouldThrowException_whenNoteNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(noteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> noteService.findByIdForUser(1L, userDetails));
    }

    @Test
    void findByIdForUser_shouldThrowSecurityException_whenUserNotAllowed() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        Note note = Note.builder().id(1L).title("Test Note").content("Test Content").user(anotherUser).build();
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(noteRepository.findById(1L)).thenReturn(Optional.of(note));

        assertThrows(SecurityException.class, () -> noteService.findByIdForUser(1L, userDetails));
    }

    @Test
    void create_shouldReturnCreatedNoteDto() {
        CreateNoteRequest request = new CreateNoteRequest("New Note", "New Content");
        Note note = Note.builder().id(1L).title("New Note").content("New Content").user(user).build();
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(noteRepository.save(any(Note.class))).thenReturn(note);

        NoteDto result = noteService.create(request, userDetails);

        assertEquals("New Note", result.getTitle());
        verify(userRepository).findByUsername("testuser");
        verify(noteRepository).save(any(Note.class));
    }

    @Test
    void update_shouldReturnUpdatedNoteDto() {
        CreateNoteRequest request = new CreateNoteRequest("Updated Note", "Updated Content");
        Note existingNote = Note.builder().id(1L).title("Old Note").content("Old Content").user(user).build();
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(noteRepository.findById(1L)).thenReturn(Optional.of(existingNote));
        when(noteRepository.save(any(Note.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NoteDto result = noteService.update(1L, request, userDetails);

        assertEquals("Updated Note", result.getTitle());
        assertEquals("Updated Content", result.getContent());
        verify(userRepository).findByUsername("testuser");
        verify(noteRepository).findById(1L);
        verify(noteRepository).save(any(Note.class));
    }

    @Test
    void update_shouldThrowException_whenNoteNotFound() {
        CreateNoteRequest request = new CreateNoteRequest("Updated Note", "Updated Content");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(noteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> noteService.update(1L, request, userDetails));
    }

    @Test
    void update_shouldThrowSecurityException_whenUserNotAllowed() {
        CreateNoteRequest request = new CreateNoteRequest("Updated Note", "Updated Content");
        User anotherUser = new User();
        anotherUser.setId(2L);
        Note existingNote = Note.builder().id(1L).title("Old Note").content("Old Content").user(anotherUser).build();
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(noteRepository.findById(1L)).thenReturn(Optional.of(existingNote));

        assertThrows(SecurityException.class, () -> noteService.update(1L, request, userDetails));
    }

    @Test
    void delete_shouldDeleteNote() {
        Note note = Note.builder().id(1L).title("Test Note").content("Test Content").user(user).build();
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(noteRepository.findById(1L)).thenReturn(Optional.of(note));
        doNothing().when(noteRepository).delete(note);

        noteService.delete(1L, userDetails);

        verify(userRepository).findByUsername("testuser");
        verify(noteRepository).delete(note);
    }

    @Test
    void delete_shouldThrowException_whenNoteNotFound() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(noteRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> noteService.delete(1L, userDetails));
    }

    @Test
    void delete_shouldThrowSecurityException_whenUserNotAllowed() {
        User anotherUser = new User();
        anotherUser.setId(2L);
        Note note = Note.builder().id(1L).title("Test Note").content("Test Content").user(anotherUser).build();
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(noteRepository.findById(1L)).thenReturn(Optional.of(note));

        assertThrows(SecurityException.class, () -> noteService.delete(1L, userDetails));
    }
}

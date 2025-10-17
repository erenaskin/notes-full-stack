package com.notes.api.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notes.api.domain.entity.Note;
import com.notes.api.domain.entity.User;
import com.notes.api.domain.repository.NoteRepository;
import com.notes.api.domain.repository.UserRepository;
import com.notes.api.web.dto.CreateNoteRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser("testuser")
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        // 1. Önce notları sil
        noteRepository.deleteAll();
        // 2. Sonra kullanıcıları sil
        userRepository.deleteAll();
        
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        // 3. E-posta adresini ekle
        testUser.setEmail("testuser@example.com"); 
        userRepository.save(testUser);
    }

    @Test
    void getNotes_shouldReturnNotesForUser() throws Exception {
        Note note = Note.builder().title("Test Note").content("Content").user(testUser).isDeleted(false).build();
        noteRepository.save(note);

        mockMvc.perform(get("/api/notes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Test Note")));
    }

    @Test
    void getNoteById_shouldReturnNote() throws Exception {
        Note note = Note.builder().title("Test Note").content("Content").user(testUser).isDeleted(false).build();
        note = noteRepository.save(note);

        mockMvc.perform(get("/api/notes/{id}", note.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Test Note")));
    }

    @Test
    void getNoteById_shouldReturnNotFound_whenNoteDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/notes/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void createNote_shouldCreateAndReturnNote() throws Exception {
        CreateNoteRequest request = new CreateNoteRequest("New Note", "New Content");

        mockMvc.perform(post("/api/notes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("New Note")));
    }

    @Test
    void updateNote_shouldUpdateAndReturnNote() throws Exception {
        Note note = Note.builder().title("Old Note").content("Old Content").user(testUser).isDeleted(false).build();
        note = noteRepository.save(note);
        CreateNoteRequest request = new CreateNoteRequest("Updated Note", "Updated Content");

        mockMvc.perform(put("/api/notes/{id}", note.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Note")));
    }

    @Test
    void deleteNote_shouldDeleteNote() throws Exception {
        Note note = Note.builder().title("Test Note").content("Content").user(testUser).isDeleted(false).build();
        note = noteRepository.save(note);

        mockMvc.perform(delete("/api/notes/{id}", note.getId()))
                .andExpect(status().isNoContent());

        // Soft delete: not artık silinmiş olarak işaretlenmeli
        mockMvc.perform(get("/api/notes/{id}", note.getId()))
                .andExpect(status().isNotFound());
        
        // Veritabanında hala mevcut olmalı ama isDeleted=true olmalı
        Note deletedNote = noteRepository.findById(note.getId()).orElseThrow();
        assertTrue(deletedNote.getIsDeleted());
        assertNotNull(deletedNote.getDeletedAt());
    }
}


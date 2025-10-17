package com.notes.api.e2e;

import com.notes.api.domain.entity.User;
import com.notes.api.domain.repository.NoteRepository;
import com.notes.api.domain.repository.UserRepository;
import com.notes.api.web.dto.CreateNoteRequest;
import com.notes.api.web.dto.NoteDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class NoteE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // PasswordEncoder eklendi

    @BeforeEach
    void setUp() {
        noteRepository.deleteAll();
        userRepository.deleteAll();

        User testUser = new User();
        testUser.setUsername("testuser");
        // Parola şifrelenerek kaydediliyor
        testUser.setPassword(passwordEncoder.encode("password"));
        testUser.setEmail("testuser@example.com");
        userRepository.save(testUser);
    }

    @Test
    void testNoteCrudFlow() {
        // Create Note
        CreateNoteRequest createRequest = new CreateNoteRequest("E2E Test Note", "E2E Content");
        ResponseEntity<NoteDto> createResponse = restTemplate.withBasicAuth("testuser", "password")
                .postForEntity(getNoteUrl(), createRequest, NoteDto.class);
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        NoteDto createdNote = createResponse.getBody();
        Assertions.assertNotNull(createdNote);
        assertEquals("E2E Test Note", createdNote.getTitle());

        // Get Note
        ResponseEntity<NoteDto> getResponse = restTemplate.withBasicAuth("testuser", "password")
                .getForEntity(getNoteUrl() + "/" + createdNote.getId(), NoteDto.class);
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        Assertions.assertNotNull(getResponse.getBody());
        assertEquals("E2E Test Note", getResponse.getBody().getTitle());

        // Update Note
        CreateNoteRequest updateRequest = new CreateNoteRequest("Updated E2E Note", "Updated E2E Content");
        restTemplate.withBasicAuth("testuser", "password")
                .put(getNoteUrl() + "/" + createdNote.getId(), updateRequest);

        // Verify Update
        ResponseEntity<NoteDto> getUpdatedResponse = restTemplate.withBasicAuth("testuser", "password")
                .getForEntity(getNoteUrl() + "/" + createdNote.getId(), NoteDto.class);
        assertEquals(HttpStatus.OK, getUpdatedResponse.getStatusCode());
        Assertions.assertNotNull(getUpdatedResponse.getBody());
        assertEquals("Updated E2E Note", getUpdatedResponse.getBody().getTitle());

        // Delete Note
        restTemplate.withBasicAuth("testuser", "password").delete(getNoteUrl() + "/" + createdNote.getId());

        // Verify Deletion
        ResponseEntity<NoteDto> getDeletedResponse = restTemplate.withBasicAuth("testuser", "password")
                .getForEntity(getNoteUrl() + "/" + createdNote.getId(), NoteDto.class);
        assertEquals(HttpStatus.NOT_FOUND, getDeletedResponse.getStatusCode());
    }

    @Test
    void getNotes_shouldReturnUnauthorized_whenNotAuthenticated() {
        ResponseEntity<String> response = restTemplate
                .getForEntity(getNoteUrl(), String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    private String getNoteUrl() {
        return "http://localhost:" + port + "/api/notes";
    }
}

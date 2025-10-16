package com.notes.api.web.controller;

import com.notes.api.service.NoteService;
import com.notes.api.web.dto.CreateNoteRequest;
import com.notes.api.web.dto.NoteDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @GetMapping
    public ResponseEntity<List<NoteDto>> getNotes(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(noteService.findAllForUser(userDetails));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoteDto> getNoteById(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(noteService.findByIdForUser(id, userDetails));
    }

    @PostMapping
    public ResponseEntity<NoteDto> createNote(@RequestBody CreateNoteRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        return new ResponseEntity<>(noteService.create(request, userDetails), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteDto> updateNote(@PathVariable Long id, @RequestBody CreateNoteRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(noteService.update(id, request, userDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        noteService.delete(id, userDetails);
        return ResponseEntity.noContent().build();
    }
}

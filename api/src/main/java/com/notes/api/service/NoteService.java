package com.notes.api.service;

import com.notes.api.web.dto.CreateNoteRequest;
import com.notes.api.web.dto.NoteDto;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface NoteService {
    List<NoteDto> findAllForUser(UserDetails userDetails);
    NoteDto findByIdForUser(Long id, UserDetails userDetails);
    NoteDto create(CreateNoteRequest request, UserDetails userDetails);
    NoteDto update(Long id, CreateNoteRequest request, UserDetails userDetails);
    void delete(Long id, UserDetails userDetails);
}

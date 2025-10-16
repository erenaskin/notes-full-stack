package com.notes.api.domain.repository;

import com.notes.api.domain.entity.Note;
import com.notes.api.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByUserOrderByUpdatedAtDesc(User user);
}

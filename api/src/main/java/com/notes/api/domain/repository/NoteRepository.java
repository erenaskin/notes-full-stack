package com.notes.api.domain.repository;

import com.notes.api.domain.entity.Note;
import com.notes.api.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {


    // Soft delete için özel metodlar
    @Query("SELECT n FROM Note n WHERE n.user = :user AND n.isDeleted = false ORDER BY n.updatedAt DESC")
    List<Note> findByUserAndNotDeleted(User user);
    
    @Query("SELECT n FROM Note n WHERE n.id = :id AND n.isDeleted = false")
    Optional<Note> findByIdAndNotDeleted(Long id);
}

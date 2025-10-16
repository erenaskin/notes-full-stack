package com.notes.api.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoteDto {
    private Long id;
    private String title;
    private String content;
    private LocalDateTime updatedAt;
}

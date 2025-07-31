package com.example.studyapp.backend.dto.content;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ChapterDto {
    private Long id;
    private String title;
    private List<KnowledgePointDto> knowledgePoints;
}
package com.example.studyapp.backend.dto.content;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class SubjectDto {
    private Long id;
    private String name;
    private String iconName;
    private List<ChapterDto> chapters;
}
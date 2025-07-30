package com.example.studyapp.backend.dto.task;

import com.example.studyapp.backend.entity.enums.TimeInterval;
import com.example.studyapp.backend.entity.enums.TimingMode;
import lombok.Data;

// 用于接收创建新任务的请求数据
@Data
public class TaskCreateRequest {
    private String title;
    private TimeInterval timeInterval;
    private TimingMode timingMode;
    private int targetMinutes;
    private int targetCount;
}
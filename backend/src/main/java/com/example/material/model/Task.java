package com.example.material.model;
import java.time.LocalDateTime;
public record Task(String id, String userId, String templateId, String status, String stage, String errorMessage, String resultUrl, LocalDateTime createdAt, LocalDateTime updatedAt) {}

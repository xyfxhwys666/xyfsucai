package com.example.material.model;
public record Template(String id, String title, String category, String coverUrl, String description, int requiredImageCount, String acceptedMimeTypes, long maxFileSizeBytes) {}

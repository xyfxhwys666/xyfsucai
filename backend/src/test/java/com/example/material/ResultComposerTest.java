package com.example.material;

import com.example.material.model.Task;
import com.example.material.model.TaskAsset;
import com.example.material.repo.TaskAssetMapper;
import com.example.material.service.ResultComposer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ResultComposerTest {
    @TempDir Path storage;

    @Test
    void resultEmbedsTheCurrentTasksUploadedImages() throws Exception {
        TaskAssetMapper assets = mock(TaskAssetMapper.class);
        Task task = new Task("task-upload", "creator-lin", "demo-postcard", "PROCESSING", "PACKAGING", null, null, LocalDateTime.now(), LocalDateTime.now());
        Files.write(storage.resolve("first.jpg"), new byte[]{1, 2, 3, 4});
        Files.write(storage.resolve("second.png"), new byte[]{5, 6, 7, 8});
        when(assets.findByTask(task.id())).thenReturn(List.of(
                new TaskAsset(1, task.id(), "/uploads/first.jpg", "first.jpg", "image/jpeg"),
                new TaskAsset(2, task.id(), "/uploads/second.png", "second.png", "image/png")
        ));

        String resultUrl = new ResultComposer(assets, storage.toString()).create(task);
        String svg = Files.readString(storage.resolve("results/task-upload.svg"));

        assertTrue(resultUrl.endsWith("task-upload.svg"));
        assertTrue(svg.contains("data:image/jpeg;base64,AQIDBA=="));
        assertTrue(svg.contains("data:image/png;base64,BQYHCA=="));
    }

    @Test
    void magazineCoverUsesItsOwnLayoutAndUploadedImage() throws Exception {
        TaskAssetMapper assets = mock(TaskAssetMapper.class);
        Task task = new Task("task-magazine", "creator-mia", "demo-magazine-cover", "PROCESSING", "PACKAGING", null, null, LocalDateTime.now(), LocalDateTime.now());
        Files.write(storage.resolve("cover.webp"), new byte[]{9, 10, 11, 12});
        when(assets.findByTask(task.id())).thenReturn(List.of(
                new TaskAsset(1, task.id(), "/uploads/cover.webp", "cover.webp", "image/webp")
        ));

        new ResultComposer(assets, storage.toString()).create(task);
        String svg = Files.readString(storage.resolve("results/task-magazine.svg"));

        assertTrue(svg.contains("data:image/webp;base64,CQoLDA=="));
        assertTrue(svg.contains("FORM"));
        assertTrue(svg.contains("THE IMAGE EDIT"));
        assertTrue(svg.contains("offset=\"20%\""));
    }

    @Test
    void filmstripEmbedsAllThreeUploadedImages() throws Exception {
        TaskAssetMapper assets = mock(TaskAssetMapper.class);
        Task task = new Task("task-film", "creator-lin", "demo-filmstrip", "PROCESSING", "PACKAGING", null, null, LocalDateTime.now(), LocalDateTime.now());
        Files.write(storage.resolve("one.jpg"), new byte[]{1});
        Files.write(storage.resolve("two.jpg"), new byte[]{2});
        Files.write(storage.resolve("three.jpg"), new byte[]{3});
        when(assets.findByTask(task.id())).thenReturn(List.of(
                new TaskAsset(1, task.id(), "/uploads/one.jpg", "one.jpg", "image/jpeg"),
                new TaskAsset(2, task.id(), "/uploads/two.jpg", "two.jpg", "image/jpeg"),
                new TaskAsset(3, task.id(), "/uploads/three.jpg", "three.jpg", "image/jpeg")
        ));

        new ResultComposer(assets, storage.toString()).create(task);
        String svg = Files.readString(storage.resolve("results/task-film.svg"));

        assertTrue(svg.contains("data:image/jpeg;base64,AQ=="));
        assertTrue(svg.contains("data:image/jpeg;base64,Ag=="));
        assertTrue(svg.contains("data:image/jpeg;base64,Aw=="));
        assertTrue(svg.contains("FRAME 03"));
    }
}

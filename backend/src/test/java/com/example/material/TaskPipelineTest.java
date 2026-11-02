package com.example.material;

import com.example.material.model.Task;
import com.example.material.repo.TaskMapper;
import com.example.material.service.TaskRunner;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TaskPipelineTest {
    private Task newTask() {
        var now = LocalDateTime.now();
        return new Task("task-1", "demo-user", "demo-avatar", "QUEUED", "ASSET_CHECK", null, null, now, now);
    }

    @Test
    void successfulRunPersistsStepsInOrder() {
        TaskMapper mapper = mock(TaskMapper.class);
        Task task = newTask();
        when(mapper.find(task.id(), task.userId())).thenReturn(task);

        new TaskRunner(mapper, 0, "none").run(task);

        ArgumentCaptor<Task> updates = ArgumentCaptor.forClass(Task.class);
        verify(mapper, org.mockito.Mockito.times(4)).update(updates.capture());
        assertEquals(List.of("ASSET_CHECK", "GENERATING", "PACKAGING", "COMPLETED"),
                updates.getAllValues().stream().map(Task::stage).toList());
        assertEquals("SUCCEEDED", updates.getValue().status());
    }

    @Test
    void failedGenerationStopsBeforePackaging() {
        TaskMapper mapper = mock(TaskMapper.class);
        Task task = newTask();
        when(mapper.find(task.id(), task.userId())).thenReturn(task);

        new TaskRunner(mapper, 0, "GENERATING").run(task);

        ArgumentCaptor<Task> updates = ArgumentCaptor.forClass(Task.class);
        verify(mapper, org.mockito.Mockito.times(3)).update(updates.capture());
        assertEquals(List.of("ASSET_CHECK", "GENERATING", "GENERATING"),
                updates.getAllValues().stream().map(Task::stage).toList());
        assertEquals("FAILED", updates.getValue().status());
        assertEquals("模拟失败：生成服务超时", updates.getValue().errorMessage());
    }
}

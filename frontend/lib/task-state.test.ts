import { describe, expect, it } from 'vitest';
import { applyPollResults, isTerminalTask, type Task } from './task-state';

const task = (id: string, status: string, stage = 'ASSET_CHECK'): Task => ({
  id,
  templateId: 'demo-postcard',
  status,
  stage,
  createdAt: '2026-11-03T10:00:00',
});

describe('task polling state', () => {
  it('updates tasks by id without crossing another task state', () => {
    const first = task('first', 'PROCESSING');
    const second = task('second', 'QUEUED');
    const completed = { ...first, status: 'SUCCEEDED', stage: 'COMPLETED', resultUrl: '/result.jpg' };

    const result = applyPollResults([first, second], [
      { id: first.id, task: completed, requestFailed: false },
    ]);

    expect(result.tasks).toEqual([completed, second]);
    expect(result.pollingError).toBe(false);
  });

  it('keeps the previous task state when a query request fails', () => {
    const processing = task('processing', 'PROCESSING', 'GENERATING');
    const result = applyPollResults([processing], [
      { id: processing.id, requestFailed: true },
    ]);

    expect(result.tasks[0]).toEqual(processing);
    expect(result.tasks[0].status).not.toBe('FAILED');
    expect(result.pollingError).toBe(true);
  });

  it('treats a server-reported failed task as terminal', () => {
    const failed = { ...task('failed', 'FAILED', 'PACKAGING'), errorMessage: '结果整理失败' };
    expect(isTerminalTask(failed)).toBe(true);
    expect(isTerminalTask(task('active', 'PROCESSING'))).toBe(false);
  });
});

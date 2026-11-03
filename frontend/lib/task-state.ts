export type Task = {
  id: string;
  templateId: string;
  status: string;
  stage: string;
  errorMessage?: string;
  resultUrl?: string;
  createdAt: string;
};

export type PollResult = {
  id: string;
  task?: Task;
  requestFailed: boolean;
};

export function isTerminalTask(task: Task) {
  return task.status === 'SUCCEEDED' || task.status === 'FAILED';
}

export function applyPollResults(current: Task[], results: PollResult[]) {
  const updates = new Map(
    results.filter(result => result.task).map(result => [result.id, result.task!]),
  );

  return {
    tasks: current.map(task => updates.get(task.id) || task),
    pollingError: results.some(result => result.requestFailed),
  };
}

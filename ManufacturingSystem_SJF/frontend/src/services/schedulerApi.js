// The ONLY file that talks to the backend. Components never call fetch directly.

const SCHEDULE_URL = 'http://localhost:8080/api/schedule';

/** Error whose `messages` are already readable by the user. */
export class SchedulerApiError extends Error {
  constructor(messages) {
    super(messages[0]);
    this.name = 'SchedulerApiError';
    this.messages = messages;
  }
}

/**
 * Sends the task list to the Spring Boot backend, which runs the SJF algorithm.
 *
 * @param {{id: string, name: string, burstTime: number}[]} tasks
 * @returns the backend response:
 *   { results: [{ executionOrder, taskId, taskName, burstTime, startTime,
 *                 completionTime, waitingTime, turnaroundTime }],
 *     averageWaitingTime, averageTurnaroundTime, totalProductionTime }
 * @throws {SchedulerApiError} when the server is unreachable or rejects the request
 */
export async function requestSchedule(tasks) {
  let response;
  try {
    response = await fetch(SCHEDULE_URL, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ tasks }),
    });
  } catch {
    throw new SchedulerApiError([
      'Cannot reach the scheduling server at http://localhost:8080. Start the Spring Boot backend and try again.',
    ]);
  }

  if (response.ok) {
    return response.json();
  }
  throw new SchedulerApiError(await readErrorMessages(response));
}

/** Reads the backend's { status, error, messages } body, with fallbacks for other error formats. */
async function readErrorMessages(response) {
  try {
    const body = await response.json();
    if (Array.isArray(body.messages) && body.messages.length > 0) {
      return body.messages;
    }
    if (typeof body.detail === 'string') {
      return [body.detail]; // Spring's default error format (e.g. 405, 415)
    }
  } catch {
    // The error body was not JSON; use the generic message below.
  }
  return [`The server returned an unexpected error (HTTP ${response.status}).`];
}

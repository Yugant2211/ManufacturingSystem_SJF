import { useEffect, useRef, useState } from 'react';
import Header from './components/Header';
import SummaryCards from './components/SummaryCards';
import TaskForm from './components/TaskForm';
import TaskTable from './components/TaskTable';
import RunControls from './components/RunControls';
import ErrorBanner from './components/ErrorBanner';
import ResultsTable from './components/ResultsTable';
import GanttChart from './components/GanttChart';
import PerformanceAnalysis from './components/PerformanceAnalysis';
import { requestSchedule, SchedulerApiError } from './services/schedulerApi';
import { getRunValidationError } from './utils/validation';

/**
 * Owns all state. It never calculates scheduling values: the schedule is whatever the
 * backend returns, and the components only display it.
 */
export default function App() {
  const [tasks, setTasks] = useState([]); // [{ id, name, burstTime }]
  const [nextTaskNumber, setNextTaskNumber] = useState(1); // T1, T2, ... never reused
  const [schedule, setSchedule] = useState(null); // backend response, or null before a run
  const [error, setError] = useState(null); // { title, messages } or null
  const [isLoading, setIsLoading] = useState(false);
  const resultsRef = useRef(null);

  // Bring the results into view after a successful run.
  useEffect(() => {
    if (schedule) {
      const reduceMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
      resultsRef.current?.scrollIntoView({
        behavior: reduceMotion ? 'auto' : 'smooth',
        block: 'start',
      });
    }
  }, [schedule]);

  // The task list changed, so any existing schedule no longer matches it.
  function discardSchedule() {
    setSchedule(null);
    setError(null);
  }

  function addTask({ name, burstTime }) {
    setTasks((current) => [...current, { id: `T${nextTaskNumber}`, name, burstTime }]);
    setNextTaskNumber((number) => number + 1);
    discardSchedule();
  }

  function deleteTask(taskId) {
    setTasks((current) => current.filter((task) => task.id !== taskId));
    discardSchedule();
  }

  function clearAllTasks() {
    setTasks([]);
    setNextTaskNumber(1);
    discardSchedule();
  }

  async function runSchedule() {
    if (isLoading) return;

    const problem = getRunValidationError(tasks);
    if (problem) {
      setError({ title: 'No tasks to schedule', messages: [problem] });
      return;
    }

    setIsLoading(true);
    setError(null);
    try {
      setSchedule(await requestSchedule(tasks));
    } catch (caught) {
      setError({
        title: 'Scheduling failed',
        messages:
          caught instanceof SchedulerApiError
            ? caught.messages
            : ['Something unexpected went wrong. Please try again.'],
      });
    } finally {
      setIsLoading(false);
    }
  }

  let stage = 'tasks';
  if (isLoading) stage = 'scheduling';
  if (schedule) stage = 'results';

  return (
    <div className="min-h-screen">
      <Header stage={stage} />

      <main className="mx-auto max-w-7xl space-y-6 px-4 py-6 sm:px-6 lg:px-8">
        <SummaryCards totalTasks={tasks.length} schedule={schedule} />

        <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
          <TaskForm onAddTask={addTask} disabled={isLoading} />

          <div className="space-y-4 lg:col-span-2">
            <TaskTable tasks={tasks} onDeleteTask={deleteTask} disabled={isLoading} />
            <RunControls
              taskCount={tasks.length}
              hasSchedule={schedule !== null}
              isLoading={isLoading}
              onRun={runSchedule}
              onReset={discardSchedule}
              onClearAll={clearAllTasks}
            />
            <ErrorBanner
              title={error?.title}
              messages={error?.messages ?? []}
              onDismiss={() => setError(null)}
            />
          </div>
        </div>

        <div ref={resultsRef} className="scroll-mt-4 space-y-6">
          {schedule ? (
            <>
              <ResultsTable results={schedule.results} />
              <GanttChart results={schedule.results} />
              <PerformanceAnalysis schedule={schedule} />
            </>
          ) : (
            <section className="panel px-4 py-8" aria-label="Schedule results">
              <p className="max-w-xl text-base text-steel">
                No schedule yet. Add your tasks, then choose Run SJF Scheduling to see the
                optimized order, waiting and turnaround times, and the Gantt chart here.
              </p>
            </section>
          )}
        </div>
      </main>
    </div>
  );
}

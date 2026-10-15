function Spinner() {
  return (
    <svg
      className="h-5 w-5 animate-spin motion-reduce:animate-none"
      viewBox="0 0 24 24"
      fill="none"
      aria-hidden="true"
    >
      <circle cx="12" cy="12" r="9" stroke="currentColor" strokeWidth="3" opacity="0.25" />
      <path d="M21 12a9 9 0 0 0-9-9" stroke="currentColor" strokeWidth="3" strokeLinecap="round" />
    </svg>
  );
}

/** Run SJF, Reset Schedule and Clear All Tasks, including the loading state. */
export default function RunControls({
  taskCount,
  hasSchedule,
  isLoading,
  onRun,
  onReset,
  onClearAll,
}) {
  return (
    <div className="panel flex flex-col gap-3 p-4 sm:flex-row sm:items-center">
      {/* aria-disabled (not disabled) keeps the button readable while it loads */}
      <button
        type="button"
        onClick={onRun}
        aria-disabled={isLoading}
        className={`btn btn-primary py-3 sm:flex-1 ${isLoading ? 'cursor-wait' : ''}`}
      >
        {isLoading ? (
          <>
            <Spinner />
            Running SJF…
          </>
        ) : (
          'Run SJF Scheduling'
        )}
      </button>

      <button
        type="button"
        onClick={onReset}
        disabled={!hasSchedule || isLoading}
        className="btn btn-secondary py-3"
      >
        Reset Schedule
      </button>

      <button
        type="button"
        onClick={onClearAll}
        disabled={taskCount === 0 || isLoading}
        className="btn btn-danger py-3"
      >
        Clear All Tasks
      </button>

      <p role="status" className="sr-only">
        {isLoading ? 'Scheduling tasks, please wait.' : ''}
      </p>
    </div>
  );
}

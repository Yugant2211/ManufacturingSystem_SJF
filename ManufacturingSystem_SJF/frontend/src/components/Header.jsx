const FLOW_STEPS = [
  { key: 'tasks', label: 'Add tasks' },
  { key: 'scheduling', label: 'Run SJF' },
  { key: 'results', label: 'Read the schedule' },
];

/**
 * Page title plus a three-step workflow indicator.
 * `stage` is 'tasks', 'scheduling' or 'results' and decides which step is highlighted.
 */
export default function Header({ stage }) {
  const currentIndex = FLOW_STEPS.findIndex((step) => step.key === stage);

  return (
    <header className="bg-ink text-white">
      <div className="hazard-stripe h-3" aria-hidden="true" />

      <div className="mx-auto flex max-w-7xl flex-col gap-6 px-4 py-7 sm:px-6 lg:flex-row lg:items-end lg:justify-between lg:px-8">
        <div className="max-w-2xl">
          <h1 className="text-3xl font-bold leading-tight sm:text-4xl">🏭 Manufacturing System</h1>
          <p className="mt-1 text-xl font-semibold text-hazard">Shortest Job First Task Scheduler</p>
          <p className="mt-3 text-base leading-relaxed text-slate-300">
            Optimize production task scheduling using the Non-Preemptive SJF algorithm.
          </p>
        </div>

        <ol className="flex flex-wrap items-center gap-x-2 gap-y-3" aria-label="Workflow">
          {FLOW_STEPS.map((step, index) => {
            const isDone = index < currentIndex;
            const isCurrent = index === currentIndex;

            let badgeStyle = 'border-slate-500 text-slate-400';
            if (isCurrent) badgeStyle = 'border-hazard bg-hazard text-ink';
            if (isDone) badgeStyle = 'border-hazard text-hazard';

            return (
              <li
                key={step.key}
                className="flex items-center gap-2"
                aria-current={isCurrent ? 'step' : undefined}
              >
                {index > 0 && (
                  <span className="hidden h-0.5 w-6 bg-slate-500 sm:block" aria-hidden="true" />
                )}
                <span
                  className={`flex h-7 w-7 items-center justify-center border-2 text-sm font-bold ${badgeStyle}`}
                >
                  {isDone ? '✓' : index + 1}
                </span>
                <span
                  className={`text-sm font-semibold ${isCurrent ? 'text-white' : 'text-slate-400'}`}
                >
                  {step.label}
                </span>
              </li>
            );
          })}
        </ol>
      </div>
    </header>
  );
}

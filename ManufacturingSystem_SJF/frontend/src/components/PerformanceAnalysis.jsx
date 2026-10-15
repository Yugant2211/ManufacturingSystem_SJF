/** The three headline results plus a plain-language explanation. */
export default function PerformanceAnalysis({ schedule }) {
  const { results, averageWaitingTime, averageTurnaroundTime, totalProductionTime } = schedule;

  const metrics = [
    {
      label: 'Average waiting time',
      value: averageWaitingTime.toFixed(2), // display formatting only
      formula: 'Total waiting time ÷ number of tasks',
    },
    {
      label: 'Average turnaround time',
      value: averageTurnaroundTime.toFixed(2),
      formula: 'Total turnaround time ÷ number of tasks',
    },
    {
      label: 'Total production time',
      value: String(totalProductionTime),
      formula: 'Completion time of the last task',
    },
  ];

  const executionSequence = results.map((result) => result.taskName).join(' → ');

  return (
    <section className="panel" aria-labelledby="analysis-title">
      <div className="panel-head">
        <h2 id="analysis-title" className="panel-title">Performance analysis</h2>
      </div>

      <dl className="grid gap-px bg-line sm:grid-cols-3">
        {metrics.map((metric) => (
          <div key={metric.label} className="bg-panel px-5 py-4">
            <dt className="text-sm font-semibold text-steel">{metric.label}</dt>
            <dd className="mt-1 flex items-baseline gap-1.5">
              <span className="text-3xl font-bold leading-none">{metric.value}</span>
              <span className="text-sm font-semibold text-steel">min</span>
            </dd>
            <dd className="mt-2 text-sm text-steel">{metric.formula}</dd>
          </div>
        ))}
      </dl>

      <div className="space-y-3 border-t border-line p-4 text-base leading-relaxed">
        <p>
          <span className="font-bold">Execution sequence:</span> {executionSequence}
        </p>
        <p>
          Lower average waiting time indicates that tasks are being completed with less queue
          delay.
        </p>
        <p className="text-steel">
          When all tasks arrive together on one machine, running the shortest task first gives the
          lowest possible average waiting time, because short tasks never sit behind long ones.
        </p>
      </div>
    </section>
  );
}

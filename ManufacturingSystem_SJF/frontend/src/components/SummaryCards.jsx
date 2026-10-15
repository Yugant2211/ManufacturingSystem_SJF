/** One readout cell: a label and its value. */
function Readout({ label, value, unit }) {
  return (
    <div className="bg-panel px-5 py-4">
      <dt className="text-sm font-semibold text-steel">{label}</dt>
      <dd className="mt-1 flex items-baseline gap-1.5">
        <span className="text-3xl font-bold leading-none">{value}</span>
        {unit && <span className="text-sm font-semibold text-steel">{unit}</span>}
      </dd>
    </div>
  );
}

/**
 * Four headline numbers. The scheduling values come straight from the backend
 * response; before the first run they show a dash.
 */
export default function SummaryCards({ totalTasks, schedule }) {
  const hasSchedule = schedule !== null;
  const minutes = (value) => (hasSchedule ? value.toFixed(2) : '—'); // display formatting only

  return (
    <section aria-label="Summary" className="border border-line bg-line">
      <dl className="grid grid-cols-2 gap-px lg:grid-cols-4">
        <Readout label="Total tasks" value={totalTasks} />
        <Readout
          label="Average waiting time"
          value={minutes(schedule?.averageWaitingTime)}
          unit={hasSchedule ? 'min' : ''}
        />
        <Readout
          label="Average turnaround time"
          value={minutes(schedule?.averageTurnaroundTime)}
          unit={hasSchedule ? 'min' : ''}
        />
        <Readout
          label="Total production time"
          value={hasSchedule ? schedule.totalProductionTime : '—'}
          unit={hasSchedule ? 'min' : ''}
        />
      </dl>
    </section>
  );
}

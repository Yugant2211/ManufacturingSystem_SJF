/** The optimized schedule exactly as calculated by the backend. */
export default function ResultsTable({ results }) {
  return (
    <section className="panel" aria-labelledby="results-title">
      <div className="panel-head">
        <h2 id="results-title" className="panel-title">Optimized schedule</h2>
        <p className="text-sm text-steel">Shortest task first. All times are in minutes.</p>
      </div>

      <div className="overflow-x-auto">
        <table className="w-full min-w-[40rem] text-left text-base">
          <thead>
            <tr className="border-b-2 border-ink">
              <th scope="col" className="px-4 py-2 text-right font-bold">Execution Order</th>
              <th scope="col" className="px-4 py-2 font-bold">Task</th>
              <th scope="col" className="px-4 py-2 text-right font-bold">Burst Time</th>
              <th scope="col" className="px-4 py-2 text-right font-bold">Waiting Time</th>
              <th scope="col" className="px-4 py-2 text-right font-bold">Turnaround Time</th>
              <th scope="col" className="px-4 py-2 text-right font-bold">Completion Time</th>
            </tr>
          </thead>
          <tbody>
            {results.map((result) => (
              <tr key={result.taskId} className="border-b border-line last:border-b-0">
                <td className="px-4 py-2 text-right">
                  <span className="inline-flex h-7 w-7 items-center justify-center bg-ink text-sm font-bold text-white">
                    {result.executionOrder}
                  </span>
                </td>
                <td className="px-4 py-2">
                  <span className="font-bold">{result.taskName}</span>
                  <span className="ml-2 text-sm text-steel">{result.taskId}</span>
                </td>
                <td className="px-4 py-2 text-right">{result.burstTime}</td>
                <td className="px-4 py-2 text-right">{result.waitingTime}</td>
                <td className="px-4 py-2 text-right">{result.turnaroundTime}</td>
                <td className="px-4 py-2 text-right font-bold">{result.completionTime}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}

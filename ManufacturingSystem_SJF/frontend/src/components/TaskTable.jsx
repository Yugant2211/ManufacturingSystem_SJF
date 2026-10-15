/** The queue of tasks waiting to be scheduled, in the order they were added. */
export default function TaskTable({ tasks, onDeleteTask, disabled }) {
  return (
    <section className="panel" aria-labelledby="task-queue-title">
      <div className="panel-head">
        <h2 id="task-queue-title" className="panel-title">Task queue</h2>
        <p className="text-sm font-semibold text-steel">
          {tasks.length} {tasks.length === 1 ? 'task' : 'tasks'}
        </p>
      </div>

      {tasks.length === 0 ? (
        <p className="px-4 py-8 text-base text-steel">
          No tasks yet. Add a task with the form to start building the production queue.
        </p>
      ) : (
        <div className="max-h-96 overflow-auto">
          <table className="w-full text-left text-base">
            <thead className="sticky top-0 bg-panel">
              <tr className="border-b-2 border-ink">
                <th scope="col" className="px-3 py-2 sm:px-4 font-bold">ID</th>
                <th scope="col" className="px-3 py-2 sm:px-4 font-bold">Task Name</th>
                <th scope="col" className="px-3 py-2 sm:px-4 text-right font-bold">Processing Time</th>
                <th scope="col" className="px-3 py-2 sm:px-4 text-right font-bold">Action</th>
              </tr>
            </thead>
            <tbody>
              {tasks.map((task) => (
                <tr key={task.id} className="border-b border-line last:border-b-0">
                  <td className="px-3 py-2 sm:px-4 font-bold">{task.id}</td>
                  <td className="px-3 py-2 sm:px-4">{task.name}</td>
                  <td className="px-3 py-2 sm:px-4 text-right">{task.burstTime} min</td>
                  <td className="px-3 py-2 sm:px-4 text-right">
                    <button
                      type="button"
                      onClick={() => onDeleteTask(task.id)}
                      disabled={disabled}
                      aria-label={`Delete task ${task.id}, ${task.name}`}
                      className="btn btn-danger px-3 py-1 text-sm"
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}

import { useRef, useState } from 'react';
import { validateTaskInput } from '../utils/validation';

/** Form for adding one task. Checks the input, then hands clean values to the parent. */
export default function TaskForm({ onAddTask, disabled }) {
  const [name, setName] = useState('');
  const [processingTime, setProcessingTime] = useState('');
  const [errors, setErrors] = useState({});
  const nameInputRef = useRef(null);
  const timeInputRef = useRef(null);

  function handleSubmit(event) {
    event.preventDefault();

    const { isValid, errors: foundErrors, values } = validateTaskInput(name, processingTime);
    setErrors(foundErrors);

    if (!isValid) {
      // Put the cursor in the first field that needs fixing.
      (foundErrors.name ? nameInputRef : timeInputRef).current?.focus();
      return;
    }

    onAddTask(values);
    setName('');
    setProcessingTime('');
    nameInputRef.current?.focus(); // ready for the next task
  }

  function clearError(field) {
    setErrors((current) => ({ ...current, [field]: undefined }));
  }

  return (
    <section className="panel self-start" aria-labelledby="add-task-title">
      <div className="panel-head">
        <h2 id="add-task-title" className="panel-title">Add a task</h2>
      </div>

      <form onSubmit={handleSubmit} noValidate className="space-y-4 p-4">
        <div>
          <label htmlFor="task-name" className="block text-sm font-bold">
            Task name
          </label>
          <input
            id="task-name"
            ref={nameInputRef}
            type="text"
            value={name}
            onChange={(event) => {
              setName(event.target.value);
              clearError('name');
            }}
            placeholder="e.g. Cutting"
            autoComplete="off"
            disabled={disabled}
            aria-invalid={Boolean(errors.name)}
            aria-describedby={errors.name ? 'task-name-error' : undefined}
            className="field mt-1"
          />
          {errors.name && (
            <p id="task-name-error" className="mt-1 text-sm font-semibold text-alert">
              {errors.name}
            </p>
          )}
        </div>

        <div>
          <label htmlFor="task-time" className="block text-sm font-bold">
            Processing time (minutes)
          </label>
          <input
            id="task-time"
            ref={timeInputRef}
            type="text"
            inputMode="numeric"
            value={processingTime}
            onChange={(event) => {
              setProcessingTime(event.target.value);
              clearError('burstTime');
            }}
            placeholder="e.g. 8"
            autoComplete="off"
            disabled={disabled}
            aria-invalid={Boolean(errors.burstTime)}
            aria-describedby={errors.burstTime ? 'task-time-error' : undefined}
            className="field mt-1"
          />
          {errors.burstTime && (
            <p id="task-time-error" className="mt-1 text-sm font-semibold text-alert">
              {errors.burstTime}
            </p>
          )}
        </div>

        <button type="submit" disabled={disabled} className="btn btn-primary w-full">
          Add Task
        </button>

        <p className="text-sm leading-relaxed text-steel">
          All tasks arrive at time 0 and run on one production machine. Each task runs to the
          end once it starts.
        </p>
      </form>
    </section>
  );
}

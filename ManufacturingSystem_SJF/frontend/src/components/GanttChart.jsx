import { useState } from 'react';

// Very short tasks would shrink to nothing, so every block keeps a small minimum width.
const MIN_BLOCK_WIDTH_PX = 56;

/**
 * Timeline drawn like a measuring tape.
 * Everything comes from the backend result: each block's width is proportional to
 * `burstTime` (flex-grow), and the ruler labels are `startTime` and `completionTime`.
 */
export default function GanttChart({ results }) {
  const [activeIndex, setActiveIndex] = useState(null);
  const active = activeIndex === null ? null : results[activeIndex];
  const lastIndex = results.length - 1;

  return (
    <section className="panel" aria-labelledby="gantt-title">
      <div className="panel-head">
        <h2 id="gantt-title" className="panel-title">Gantt chart</h2>
        <p className="text-sm text-steel">
          Block width is proportional to processing time. Times are in minutes.
        </p>
      </div>

      <div className="p-4">
        <div className="overflow-x-auto pb-1">
          <div className="flex" role="list" aria-label="Execution timeline">
            {results.map((result, index) => (
              <div
                key={result.taskId}
                role="listitem"
                className="px-px"
                style={{ flexGrow: result.burstTime, flexBasis: 0, minWidth: MIN_BLOCK_WIDTH_PX }}
              >
                <div
                  tabIndex={0}
                  aria-label={`${result.taskName}: starts at ${result.startTime}, finishes at ${result.completionTime}, ${result.burstTime} minutes`}
                  title={`${result.taskName}: ${result.startTime} to ${result.completionTime} min`}
                  onMouseEnter={() => setActiveIndex(index)}
                  onMouseLeave={() => setActiveIndex(null)}
                  onFocus={() => setActiveIndex(index)}
                  onBlur={() => setActiveIndex(null)}
                  className={`flex h-16 flex-col justify-center overflow-hidden rounded-[3px] px-2 text-white transition-[filter] hover:brightness-125 ${
                    index % 2 === 0 ? 'bg-belt-a' : 'bg-belt-b'
                  }`}
                >
                  <span className="truncate text-sm font-bold">{result.taskName}</span>
                  <span className="truncate text-xs">{result.burstTime} min</span>
                </div>

                {/* Ruler: a tick and the start time at the left edge of every block */}
                <div className="relative mt-1 h-9 border-t-2 border-ink">
                  <span className="absolute left-0 top-0 h-2.5 w-0.5 bg-ink" aria-hidden="true" />
                  <span className="absolute left-1 top-2.5 text-xs font-semibold text-steel">
                    {result.startTime}
                  </span>

                  {/* The last block also closes the ruler with its completion time */}
                  {index === lastIndex && (
                    <>
                      <span
                        className="absolute right-0 top-0 h-2.5 w-0.5 bg-ink"
                        aria-hidden="true"
                      />
                      <span className="absolute right-1 top-2.5 text-xs font-semibold text-steel">
                        {result.completionTime}
                      </span>
                    </>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>

        <p className="mt-3 min-h-6 text-sm text-steel" aria-live="polite">
          {active ? (
            <>
              <span className="font-bold text-ink">{active.taskName}</span> runs from minute{' '}
              {active.startTime} to minute {active.completionTime} ({active.burstTime} min).
            </>
          ) : (
            'Hover over or focus a block to see when it starts and finishes.'
          )}
        </p>
      </div>
    </section>
  );
}

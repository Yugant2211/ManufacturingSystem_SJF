/** Shows what went wrong and how to fix it. Renders nothing when there are no messages. */
export default function ErrorBanner({ title, messages, onDismiss }) {
  if (messages.length === 0) {
    return null;
  }

  return (
    <div
      role="alert"
      className="flex items-start gap-3 border-2 border-alert bg-alert-soft p-4 text-alert"
    >
      <svg
        className="mt-0.5 h-6 w-6 shrink-0"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth="2.5"
        strokeLinecap="round"
        strokeLinejoin="round"
        aria-hidden="true"
      >
        <path d="M12 3 2 20h20L12 3Z" />
        <path d="M12 10v5M12 18h.01" />
      </svg>

      <div className="min-w-0 flex-1">
        <p className="font-bold">{title}</p>
        {messages.length === 1 ? (
          <p className="mt-1 text-base">{messages[0]}</p>
        ) : (
          <ul className="mt-1 list-disc space-y-1 pl-5 text-base">
            {messages.map((message) => (
              <li key={message}>{message}</li>
            ))}
          </ul>
        )}
      </div>

      <button
        type="button"
        onClick={onDismiss}
        className="btn btn-danger px-3 py-1 text-sm"
        aria-label="Dismiss error message"
      >
        Dismiss
      </button>
    </div>
  );
}

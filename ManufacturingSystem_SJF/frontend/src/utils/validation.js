// Basic input checks done in the browser for quick feedback.
// The backend validates again and remains the final authority.

export const MAX_NAME_LENGTH = 40;
export const MAX_PROCESSING_TIME = 10000;

/**
 * Checks the two form fields.
 * @param {string} nameText        raw text from the name field
 * @param {string} processingText  raw text from the processing time field
 * @returns {{ isValid: boolean, errors: {name?: string, burstTime?: string},
 *             values: {name: string, burstTime: number} | null }}
 */
export function validateTaskInput(nameText, processingText) {
  const name = nameText.trim();
  const processing = processingText.trim();
  const errors = {};

  if (name === '') {
    errors.name = 'Enter a task name.';
  } else if (name.length > MAX_NAME_LENGTH) {
    errors.name = `Use ${MAX_NAME_LENGTH} characters or fewer for the task name.`;
  }

  if (processing === '') {
    errors.burstTime = 'Enter a processing time in minutes.';
  } else if (!/^\d+$/.test(processing)) {
    errors.burstTime = 'Use a whole number such as 8. Decimals, letters and negative numbers are not allowed.';
  } else {
    const minutes = Number(processing);
    if (minutes < 1) {
      errors.burstTime = 'Processing time must be at least 1 minute.';
    } else if (minutes > MAX_PROCESSING_TIME) {
      errors.burstTime = `Processing time must be ${MAX_PROCESSING_TIME} minutes or less.`;
    }
  }

  const isValid = Object.keys(errors).length === 0;
  return {
    isValid,
    errors,
    values: isValid ? { name, burstTime: Number(processing) } : null,
  };
}

/** Returns a message when the schedule cannot be run yet, or null when it can. */
export function getRunValidationError(tasks) {
  if (tasks.length === 0) {
    return 'Add at least one task before running SJF scheduling.';
  }
  return null;
}

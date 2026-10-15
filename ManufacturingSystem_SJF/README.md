# Manufacturing System – SJF Task Scheduler

## Description

A full-stack manufacturing task scheduling system that applies the non-preemptive Shortest Job First (SJF) scheduling algorithm to a production line. Each manufacturing task has a name and a processing (burst) time. The system orders the tasks from shortest to longest and shows the execution order, waiting time, turnaround time, completion time, averages, total production time and a Gantt chart.

## Technology

**Backend**

- Java 17
- Spring Boot 3
- Maven

**Frontend**

- React
- Vite
- JavaScript
- Tailwind CSS

No database is used. Tasks live in the browser during the current session, and the backend is stateless.

## Algorithm

Non-preemptive Shortest Job First (SJF).

- All tasks are treated as arriving at time `0`.
- There is one production machine, and a task that has started runs until it finishes.
- Tasks are scheduled according to shortest burst/processing time.
- For equal burst times, original insertion order is preserved.

Formulas (arrival time is always 0):

```text
Completion Time = Previous Completion Time + Burst Time
Waiting Time    = Start Time = Previous Completion Time
Turnaround Time = Completion Time - Arrival Time
Average Waiting Time    = Total Waiting Time / Number of Tasks
Average Turnaround Time = Total Turnaround Time / Number of Tasks
Total Production Time   = Completion Time of the last task
```

The algorithm runs only in the backend (`SJFService`). The frontend sends the task list and displays what the backend returns.

## Requirements

- JDK 17 or newer
- Maven 3.6.3 or newer
- Node.js 20.19+ or 22.12+ (with npm)

## Backend

Run:

```powershell
cd backend
mvn spring-boot:run
```

Backend:

```text
http://localhost:8080
```

API:

```text
POST http://localhost:8080/api/schedule
```

Run the backend unit tests:

```powershell
cd backend
mvn test
```

## Frontend

Run in another terminal:

```powershell
cd frontend
npm install
npm run dev
```

Frontend:

```text
http://localhost:5173
```

The backend only accepts requests from `http://localhost:5173`, so open the app at that address (not `127.0.0.1`).

## Running the complete project

Terminal 1:

```powershell
cd backend
mvn spring-boot:run
```

Terminal 2:

```powershell
cd frontend
npm run dev
```

Then open:

```text
http://localhost:5173
```

## Example

Tasks: Cutting 8, Welding 3, Assembly 2, Painting 6, Quality Inspection 4.

| Order | Task | Burst | Waiting | Turnaround | Completion |
|---:|---|---:|---:|---:|---:|
| 1 | Assembly | 2 | 0 | 2 | 2 |
| 2 | Welding | 3 | 2 | 5 | 5 |
| 3 | Quality Inspection | 4 | 5 | 9 | 9 |
| 4 | Painting | 6 | 9 | 15 | 15 |
| 5 | Cutting | 8 | 15 | 23 | 23 |

Average Waiting Time = 6.20, Average Turnaround Time = 10.80, Total Production Time = 23.

# 🏭 Manufacturing System – SJF Task Scheduler

A full-stack manufacturing task scheduling application that uses the **Non-Preemptive Shortest Job First (SJF)** scheduling algorithm to optimize the execution order of manufacturing tasks.

The system allows users to create and manage manufacturing tasks, calculate their optimal execution sequence, analyze scheduling performance, and visualize the execution timeline using an interactive **Gantt Chart**.

---

## 📌 Project Overview

In a manufacturing environment, multiple tasks may need to be processed on a single production machine.

This project applies the **Non-Preemptive Shortest Job First (SJF)** scheduling algorithm to determine an efficient execution order by prioritizing tasks with the shortest processing time.

The application is divided into two major parts:

- **Frontend:** React + Vite + Tailwind CSS
- **Backend:** Java + Spring Boot + Maven

The frontend communicates with the backend through a REST API.

The backend performs the scheduling calculations, while the frontend provides an interactive interface for entering tasks and visualizing the results.

---

## ✨ Features

### 📋 Task Management

- Add manufacturing tasks
- Enter task name
- Enter processing/burst time
- Validate task inputs
- Prevent duplicate task IDs
- Reject invalid processing times
- Display all added tasks in a table

### ⚙️ SJF Scheduling

- Implements **Non-Preemptive Shortest Job First**
- Sorts tasks based on processing/burst time
- Preserves insertion order when processing times are equal
- Calculates scheduling metrics on the backend
- Returns structured scheduling results through REST API

### 📊 Performance Analysis

The application calculates:

- Execution Order
- Start Time
- Completion Time
- Waiting Time
- Turnaround Time
- Average Waiting Time
- Average Turnaround Time
- Total Production Time

### 📈 Visualization

The frontend provides:

- Interactive task table
- Scheduling results table
- Gantt chart
- Summary cards
- Performance analysis
- Validation messages
- Error handling

### 🧪 Testing

The backend includes automated tests for:

- SJF scheduling logic
- Service layer
- REST controller
- Request validation
- Error handling

---

# 🛠️ Technology Stack

## Frontend

| Technology | Version | Purpose |
|---|---|---|
| React | 19.3.0 | User Interface |
| React DOM | 19.3.0 | DOM Rendering |
| Vite | 8.3.4 | Development & Build Tool |
| Tailwind CSS | 4.3.3 | Styling |
| JavaScript | ES Modules | Frontend Logic |

## Backend

| Technology | Version | Purpose |
|---|---|---|
| Java | 17 | Backend Programming |
| Spring Boot | 3.3.5 | Backend Framework |
| Spring Web | 3.3.5 | REST API |
| Spring Validation | 3.3.5 | Request Validation |
| Maven | 3.6.3+ | Build & Dependency Management |
| JUnit 5 | Spring Boot Test | Automated Testing |
| MockMvc | Spring Boot Test | Controller Testing |

---

# 🏗️ System Architecture

```text
                 ┌─────────────────────────────┐
                 │       React Frontend        │
                 │                             │
                 │  Task Management             │
                 │  Task Validation             │
                 │  Results Table               │
                 │  Gantt Chart                 │
                 │  Performance Analysis        │
                 └──────────────┬──────────────┘
                                │
                                │ HTTP POST
                                │ /api/schedule
                                ▼
                 ┌─────────────────────────────┐
                 │     Spring Boot Backend     │
                 │                             │
                 │   SchedulingController      │
                 │            │                │
                 │            ▼                │
                 │        SJFService           │
                 │            │                │
                 │            ▼                │
                 │   Scheduling Calculations   │
                 └──────────────┬──────────────┘
                                │
                                ▼
                      Scheduling Results

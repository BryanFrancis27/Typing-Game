# 🎮 Typing Game Application

An interactive **JavaFX-based typing game** designed to help users improve typing speed, accuracy, and consistency.  
Built using **Java 17**, **JavaFX**, **Object-Oriented Programming (OOP)** principles, and multiple **data structures**, with real-time performance tracking and multiple game modes.

---

## 📋 Table of Contents

- [Features](#-features)
- [Data Structures](#-data-structures)
- [Requirements](#-requirements)
- [Installation](#-installation)
- [Running the Application](#-running-the-application)
- [Usage Guide](#-usage-guide)
- [Project Structure](#-project-structure)
- [Default Accounts](#-default-accounts)
- [Technical Details](#-technical-details)
- [Troubleshooting](#-troubleshooting)
- [Educational Value](#-educational-value)
- [License](#-license)

---

## ✨ Features

### Core Functionality

- **User Authentication**
  - Secure login system
  - Role-based access (Admin & Player)
  - User registration with validation
  - Session handling

- **Multiple Game Modes**
  - **Timed Mode** – Type as many words as possible in 60 seconds
  - **Free Practice Mode** – Unlimited typing without time constraints

- **Real-Time Performance Tracking**
  - Words Per Minute (WPM)
  - Accuracy percentage
  - Error count
  - Live statistic updates during gameplay

- **Score Management & Leaderboards**
  - Automatic ranking system
  - Global leaderboards
  - Personal best tracking
  - Performance grading (A+ to F)

- **Session History**
  - Complete game history
  - Detailed per-session statistics
  - Exportable performance data
  - Trend analysis

- **Admin Panel**
  - View all player statistics
  - Analyze performance reports
  - Access system-wide metrics

---

## 🔧 Data Structures

This project demonstrates **intentional and justified use** of different data structures:

| Data Structure | Implementation Class | Purpose | Time Complexity |
|---------------|---------------------|--------|-----------------|
| **HashMap** | `UserService` | User authentication & storage | O(1) lookup |
| **Queue (LinkedList)** | `GameService` | Sequential word delivery (FIFO) | O(1) |
| **Stack** | `PerformanceTracker` | Error tracking / undo operations | O(1) |
| **TreeMap** | `ScoreManager` | Sorted leaderboard ranking | O(log n) |
| **ArrayList** | `User` | Session history storage | O(1) append |
| **Graph (Adjacency List)** | `SceneManager` | Scene navigation validation | O(1) |

Each structure includes:
- Purpose explanation
- Justification for selection
- Time complexity discussion
- Alternative considerations (documented in code)

---

## 📦 Requirements

- **Java Development Kit (JDK)**: 17 or higher  
- **Maven**: 3.6 or higher  
- **JavaFX SDK**: 17.0.2 (managed via Maven)  
- **Operating System**: Windows, macOS, or Linux  

---

## 🚀 Installation

### 1️⃣ Clone or Download the Project

```bash
git clone <repository-url>
cd TypingGame-V2

# JSync 🚀

**JSync** is an offline-first task management app designed for reliability, speed, and seamless synchronization. It ensures that users can manage their tasks even without an internet connection, with automatic syncing when connectivity is restored.

---

## ✨ Features

* 📴 **Offline-First Architecture** – Works without internet connectivity
* 🔄 **Real-time Sync via WebSockets** – Keeps data consistent across devices
* 🔁 **Retry Mechanism** – Ensures failed operations are retried automatically
* ⚡ **Fast & Responsive UI** – Built using Jetpack Compose
* 🧱 **Clean Architecture** – Scalable and maintainable code structure
* 🗄️ **Persistent Storage** – Powered by PostgreSQL backend

---

## 🏗️ Tech Stack

### 📱 Frontend (Android)

* Kotlin
* Jetpack Compose
* Clean Architecture (MVVM + UseCases)
* Coroutines & StateFlow
* Room Database (Offline storage)

### 🌐 Backend

* FastAPI
* WebSockets (Real-time communication)
* PostgreSQL
* Async processing

---

## 📸 Screenshots & Demo


https://github.com/user-attachments/assets/a6fd2a66-fd71-4abf-b733-3d79c7e03166


<img width="180" height="480" alt="Screenshot_20260422-172735" src="https://github.com/user-attachments/assets/e34fa3d9-2344-4c02-ac7a-8d7e9129b562" /><img width="180" height="480" alt="Screenshot_20260422-172745" src="https://github.com/user-attachments/assets/fe3d8908-a8e3-41f7-a997-df4c360c8e9b" />

---

## ⚙️ How It Works

1. User creates/updates tasks locally (stored in Room DB)
2. Changes are queued for sync
3. WebSocket connection sends updates to backend
4. If network fails → retry mechanism kicks in
5. Backend syncs with PostgreSQL and broadcasts updates

---

## 🚀 Getting Started

### Prerequisites

* Android Studio
* Kotlin
* Python 3.10+
* PostgreSQL

---

### 🔧 Backend Setup

```bash
# Clone repo
 git clone https://github.com/jatinkumar2409/JSync_Backend.git
 cd jsync-backend

# Create virtual environment
 python -m venv venv
 source venv/bin/activate  # Windows: venv\Scripts\activate

# Install dependencies
 pip install -r requirements.txt

# Run server
 uvicorn main:app --reload
```

---

### 📱 Frontend Setup

```bash
# Clone repo
 git clone https://github.com/jatinkumar2409/JSync.git

# Open in Android Studio
# Sync Gradle & Run on emulator/device
```

---

## 📡 API & WebSocket

### WebSocket Endpoint

```
/ws/tasks?token=<TOKEN>&userId=<USER_ID>
```

### Supported Events

* Add Task
* Update Task
* Delete Task
* Mark Complete

---

## 🧠 Architecture Overview

```
Presentation (Compose UI)
        ↓
ViewModel (StateFlow)
        ↓
Domain (Use Cases)
        ↓
Data Layer (Repository)
        ↓
Local DB (Room) + Remote (WebSocket API)
```

---

## 🔁 Sync Strategy

* Local-first writes
* WorkManager enqueue
* Retry with exponential backoff
* Conflict resolution
---

## 📂 Project Structure

```
jsync/
 ├── app/
 ├── data/
 ├── domain/
 ├── presentation/
 ├── helpers/
```

---

## 🤝 Contributing

Contributions are welcome! Feel free to open issues or submit pull requests.

---

## 📄 License

This project is licensed under the MIT License.

---

## 💡 Author

**Jatin Kumar**

---

## ⭐ Support

If you like this project, consider giving it a star ⭐ on GitHub!

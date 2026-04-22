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

> Add your screenshots and demo videos here

```
/assets/screenshots/home.png
/assets/screenshots/task_detail.png
/assets/demo/demo.mp4
```

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
 git clone https://github.com/yourusername/jsync-backend.git
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
 git clone https://github.com/yourusername/jsync-android.git

# Open in Android Studio
# Sync Gradle & Run on emulator/device
```

---

## 📡 API & WebSocket

### WebSocket Endpoint

```
/ws/tasks?userId=<USER_ID>
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
* Operation queue
* Retry with exponential backoff
* Conflict resolution (last-write-wins or custom logic)

---

## 📂 Project Structure

```
jsync/
 ├── app/
 ├── data/
 ├── domain/
 ├── presentation/
 ├── backend/
 └── docs/
```

---

## 🛠️ Future Improvements

* 🔐 Authentication & Authorization
* 📊 Task analytics & insights
* 🌍 Multi-device sync enhancements
* 🧩 Plugin support

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

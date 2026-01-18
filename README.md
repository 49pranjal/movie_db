# 📽️ Movies Database App (TMDB)

An Android application built using **Kotlin** that uses the **TMDB API** to display trending and now playing movies, allows users to search and bookmark movies, view detailed information, and supports offline usage using a local database.

The app is built with **MVVM architecture**, follows the **Repository pattern**, and uses **Coroutines + Flow** for asynchronous and reactive data handling.

---

## 📌 Overview & Features

The application provides the following functionality:

- Home screen displaying **Trending** and **Now Playing** movies with Pagination support
- Movie Details screen with poster, overview, genres, runtime, ratings, and vote count
- Bookmark / unbookmark movies and view them in a dedicated **Bookmark** screen
- Search movies using TMDB with **debounced input** (API calls triggered after user stops typing)
- Offline support for Home, Bookmarks, and Movie Details screens
- Home, Bookmarks, and Movie Details screens automatically updated when bookmark state changes
- Share movies using a **custom deeplink** that opens the app directly to the Movie Details screen
- Home page refreshes automatically from the API while preserving bookmarked movies

---

## 🧱 Architecture

### Pattern
- **MVVM (Model–View–ViewModel)**
- **Single Activity + Multiple Fragments**
- **Repository Pattern**
- **Offline-first approach**

---

### Layers
```
UI (Activity / Fragments)
↓
ViewModel (StateFlow for UI state)
↓
Repository
↓
Room Database ←→ Retrofit API
```
---

## 🛠️ Tech Stack

| Area | Technology |
|---|---|
Language | Kotlin |
UI | XML + ViewBinding |
Architecture | MVVM |
Async | Kotlin Coroutines + Flow |
Networking | Retrofit + OkHttp |
JSON Parsing | Gson |
Local Storage | Room Database |
Dependency Injection | Hilt |
Image Loading | Glide |
Navigation | Jetpack Navigation Component |

---

## 🗂️ Project Structure
```
com.example.moviesdb/
│
├── data/
│ ├── local/ (Room entities, DAO, database)
│ ├── remote/ (API services, DTOs)
│ └── repository/ (AppRepository)
│
├── di/ (Hilt modules)
│
├── ui/
│ ├── home/
│ ├── search/
│ ├── bookmark/
│ └── movieDetails/
│ └── uiModels/
│
├── utils/ (Network utilities, constants)
│
├── MainActivity/
│
└── MoviesDbApplication/
```
---

## 📦 Data Handling Strategy

### Home & Bookmarks
- Data is **always observed from Room (Flow)**
- API responses update the database
- UI automatically reacts to database changes
- Pagination is session-based
- Bookmarks preserved across refreshes

### Movie Details
- Network-first strategy with database fallback
- Movie details are cached locally
- Bookmarking upserts the movie into the database if it does not already exist

### Search
- API-driven search
- Debounced input using `StateFlow`
- Results are not cached locally

---

## Setup Instructions
1. Clone the repository
2. Open the project in Android Studio
3. Add TMDB API key to `gradle.properties`:
   TMDB_API_KEY=your_api_key_here
4. Sync Gradle
5. Run the app

---

## Network Configuration Note

TMDB APIs may not work on certain networks (e.g., Jio Wi-Fi / JioFiber) due to DNS or IPv6 routing issues.

If movie data does not load:
1. Go to Android Settings → Network & Internet → Private DNS
2. Select "Private DNS provider hostname"
3. Enter one of the following:
   - dns.google
   - one.one.one.one
4. Save and retry the app

Alternatively, switching to a different network or mobile data may also resolve the issue.

---

## Deep Link Usage

The app supports a custom deep link format:

movieapp://movie/{movieId}

Since this is a custom scheme (not HTTPS), the link cannot be opened by clicking directly in browsers or emails.
To test the deep link:
1. Copy the link
2. Paste it directly into the mobile browser address bar
3. Press enter, and the app will open the Movie Details screen

---

## 🧪 Error Handling & UX

- Loading indicators for initial load
- Error messages shown only when both API and database fail
- Offline data displayed seamlessly when available
- Empty state handling for bookmarks and search screens

---

## ⚠️ Known Limitations

- **Search requires an active internet connection**  
  Movie search functionality depends on live TMDB API responses and does not support offline usage.

- **No real-time server sync for bookmark state**  
  Bookmark state is derived from the local database and is not reflected back to the TMDB backend.

- **Deeplink sharing is app-specific**  
  Shared deeplinks open the movie details screen only within the app and are not mapped to a web fallback.

---

## 🚧 Future Improvements

- **Improved bookmark synchronization**
  - Store bookmarks in a dedicated table
  - Combine movie and bookmark data to support user-specific bookmarks across devices
  
- **Enhanced pagination support**
  - Improve pagination handling and UX consistency across all listing screens
  - Support configurable page limits for search results

- **UI and Unit Testing**
  - Add ViewModel unit tests
  - Add UI tests using Espresso for core user flows

- **Better error handling & retry mechanisms**
  - Retry actions for failed network requests
  - Graceful handling of partial API failures

- **Performance optimizations**
  - Add background data refresh strategies
  - Optimize database cleanup policies for time-sensitive content

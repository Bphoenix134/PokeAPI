# PokeAPI

**PokeAPI** is an Android app for browsing Pokémon data, fetching details from the PokeAPI (https://pokeapi.co/), and storing them locally for offline access. Built with Clean Architecture, Jetpack Compose, and modern Android libraries.

---

## 🧰 Key Features

* **Pokémon List Screen**:
  * Displays a paginated list of Pokémon with names and images.
  * Supports pull-to-refresh for updating the list.
  * Includes a search bar for filtering Pokémon by name.
  * Features a filter menu to narrow down Pokémon by type.
* **Pokémon Detail Screen**:
  * Shows detailed information about a selected Pokémon, including types, height, weight, and abilities.
  * Fetches data from the API or retrieves it from the local cache if offline.
* **Local Storage**:
  * Persists Pokémon data and types in a Room database for offline access.
* **Navigation**:
  * Seamless navigation between the list and detail screens using Jetpack Compose Navigation.

---

## 📁 Project Structure

```
pokeapi/
├── data/                 # Data layer
│   ├── local/            # Room database, DAOs, and entities
│   ├── remote/           # Retrofit API service and DTOs
│   │   ├── dto/          # Data transfer objects for API responses
│   ├── repository/       # Repository implementations
├── di/                   # Dependency injection (Hilt)
├── domain/               # Business logic layer
│   ├── model/            # Domain models
│   ├── repository/       # Repository interfaces
│   ├── usecase/          # Use cases for business logic
├── presentation/         # UI layer (Jetpack Compose)
│   ├── navigation/       # Navigation setup
│   ├── ui/               # UI components and screens
│   │   ├── component/    # Reusable UI components
│   │   ├── screen/       # Screen composables
│   ├── viewmodel/        # ViewModels for state management
├── ui/                   # App theming
│   ├── theme/            # Theme and typography definitions
```

---

## ⚙️ Technologies Used

* **Kotlin**, **Jetpack Compose** for declarative UI.
* **Room** for local storage of Pokémon data and types.
* **Dagger Hilt** for dependency injection.
* **Retrofit** for API calls to https://pokeapi.co/.
* **Kotlin Coroutines + Flow** for asynchronous operations and state management.
* **Paging 3** for efficient loading of large Pokémon lists.
* **MVVM + Clean Architecture** for modular and maintainable design.

---

## 📊 Technical Highlights

* **API Integration**:
  * Fetches Pokémon data and types from https://pokeapi.co/ using Retrofit with Gson for JSON parsing.
* **Local Storage**:
  * Stores Pokémon and type data in a Room database, enabling offline access and caching.
* **Pagination**:
  * Uses Paging 3 to load Pokémon lists incrementally, improving performance.
* **Search and Filter**:
  * Supports real-time search by name and filtering by Pokémon types using Room queries.
* **State Management**:
  * Uses StateFlow and PagingData in ViewModels for reactive UI updates.
* **Offline Support**:
  * Falls back to cached data when no network connection is available.

---

## 📃 Code Style and Conventions

* Follows Clean Architecture with distinct data, domain, and presentation layers.
* Uses MVVM for separation of UI and business logic.
* Employs Dagger Hilt for dependency injection.
* Leverages Jetpack Compose for modern, declarative UI design.
* Ensures type safety with Kotlin’s nullability features.
* Uses coroutines and Flow for asynchronous operations.

---

## 🚀 Getting Started

1. Install Android Studio (latest stable version recommended).
2. Clone the repository:

   ```bash
   git clone https://github.com/Bphoenix134/pokeapi.git
   ```
3. Build and run the app:

   ```bash
   ./gradlew assembleDebug
   ```
4. Main entry point: `MainActivity`.

---

## 📸 Screenshots

Below are screenshots showcasing the app's key screens:

| Pokémon List Screen | Pokémon Filter Screen | Pokémon Detail Screen |
|---------------------|-----------------------|-----------------------|
| <img src="screenshots/pokemon_list_screen.jpg" width="200"/> | <img src="screenshots/pokemon_filter_screen.jpg" width="200"/> | <img src="screenshots/pokemon_detail_screen.jpg" width="200"/> |

---

## 📌 Notes

* All Pokémon data is cached locally using Room for offline access.
* External API (https://pokeapi.co/) is used for fetching Pokémon details and types.
* The app supports dynamic theming with light and dark modes using Material 3.
# Manual Dependency Injection — Android Learning Project

This project demonstrates how **Dependency Injection (DI) works without any framework** (no Hilt, no Dagger, no Koin). Everything is wired by hand so you can see exactly what a DI framework does under the hood.

---

## What is Dependency Injection?

A class has a **dependency** when it needs another object to do its job.

```kotlin
// WITHOUT DI — the class creates its own dependency
class PostsRepository {
    private val api = ApiService() // tightly coupled, hard to test or swap
}

// WITH DI — the dependency is given from outside
class PostsRepository(private val api: ApiService) // loosely coupled
```

DI simply means: **don't let a class create its own dependencies — receive them from outside.**

Benefits:
- Easy to swap implementations (e.g. fake API for tests)
- Easy to see what a class needs just by reading its constructor
- Single place to control how objects are created and shared

---

## Project Architecture

```
App
 └── ManualDIApp (Application)
      └── AppContainer          ← creates & holds all dependencies
           ├── OkHttpClient
           ├── Moshi
           ├── Retrofit
           ├── ApiService
           └── PostsRepository
                    │
                    ▼
          HomeViewModelFactory
                    │
                    ▼
            HomeViewModel
                    │
                    ▼
             HomeScreen (UI)
```

The dependency graph flows **top-down**: each layer receives what it needs from the layer above. Nothing creates its own dependencies.

---

## File-by-File Explanation

### 1. `ManualDIApp.kt` — The DI Root

```kotlin
class ManualDIApp : Application() {
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer()
    }
}
```

`Application` is the first object Android creates and the last it destroys — it lives for the entire app lifetime. This makes it the perfect place to hold the **dependency container**.

`AppContainer` is created once here and stored as a property. Any Activity or Fragment can access it via `application as ManualDIApp`.

> In Hilt, `@HiltAndroidApp` does exactly this — it generates and attaches a container to the Application class.

---

### 2. `AppContainer.kt` — The Dependency Container

```kotlin
class AppContainer {
    private val logging = HttpLoggingInterceptor().apply { ... }
    private val okHttpClient = OkHttpClient.Builder().addInterceptor(logging).build()
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://dummyjson.com")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(okHttpClient)
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
    val postsRepository: PostsRepository = PostsRepository(apiService)
}
```

This is the heart of manual DI. It:

1. **Builds the network stack** in the correct order: `OkHttpClient` → `Retrofit` → `ApiService`
2. **Injects `ApiService` into `PostsRepository`** — notice `PostsRepository` does not create its own `ApiService`
3. **Exposes `postsRepository`** as a `val` so it is a **singleton** — the same instance is reused everywhere

The order matters: each object is created after its own dependencies are ready.

> In Hilt/Dagger, `@Provides` and `@Singleton` annotations tell the framework to do this same wiring automatically.

---

### 3. `ApiService.kt` — The Network Interface

```kotlin
interface ApiService {
    @GET("/posts")
    suspend fun getPosts(): Response<posts>
}
```

A Retrofit interface. Retrofit generates the actual HTTP implementation at runtime via `retrofit.create(ApiService::class.java)`.

`suspend` means this function runs on a coroutine — it will not block the main thread while waiting for the network.

---

### 4. DTOs — Data Transfer Objects

```
posts.kt     — top-level response: total, limit, skip, list of Post
Post.kt      — a single post: id, title, body, tags, views, userId, reactions
Reactions.kt — likes and dislikes counts
```

DTOs map directly to the JSON structure from the API. Moshi reads the JSON and fills these data classes automatically.

```json
{
  "total": 251,
  "posts": [ { "id": 1, "title": "...", ... } ]
}
```

---

### 5. `PostsRepository.kt` — The Data Layer

```kotlin
class PostsRepository(private val api: ApiService) {
    suspend fun getPosts(): Result<posts> {
        return try {
            val response = api.getPosts()
            val body = response.body()
            if (body != null) Result.success(body)
            else Result.failure(Exception("Empty response"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

The repository:
- **Receives `ApiService` via its constructor** — this is DI in action
- **Abstracts the data source** from the ViewModel: the ViewModel does not know or care about Retrofit, HTTP, or JSON
- **Wraps the result** in Kotlin's `Result<T>` so the ViewModel gets either a success value or a failure, never a raw exception

---

### 6. `HomeViewModelFactory.kt` — Bridging DI and the ViewModel

```kotlin
class HomeViewModelFactory(
    private val repository: PostsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
```

Android creates ViewModels itself (to handle lifecycle and rotation). The problem: `HomeViewModel` needs a `PostsRepository` argument, but Android's default factory only knows how to create no-argument ViewModels.

`ViewModelProvider.Factory` solves this — you tell Android *how* to construct your ViewModel, and Android takes care of the lifecycle.

This is where manual DI meets Android's ViewModel system.

> In Hilt, `@HiltViewModel` + `@Inject constructor` eliminates the need for this factory entirely.

---

### 7. `HomeViewModel.kt` — State and Business Logic

```kotlin
class HomeViewModel(private val repository: PostsRepository) : ViewModel() {

    private val _total = MutableStateFlow<Int?>(null)
    val total: StateFlow<Int?> = _total

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun getPosts() {
        viewModelScope.launch {
            val result = repository.getPosts()
            result
                .onSuccess { _total.value = it.total }
                .onFailure { _error.value = it.message }
        }
    }
}
```

- **Receives `PostsRepository` via constructor** — DI again
- **Exposes state as `StateFlow`** — read-only outside the ViewModel (`val total` is `StateFlow`, not `MutableStateFlow`)
- **`viewModelScope`** — a coroutine scope tied to the ViewModel's lifecycle; automatically cancelled when the ViewModel is cleared
- The ViewModel survives screen rotation; the UI simply re-collects the same state

---

### 8. `HomeScreen.kt` — The UI

```kotlin
@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel) {
    val total by viewModel.total.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(...) {
        Button(onClick = { viewModel.getPosts() }) {
            Text("Get Posts")
        }
        when {
            total != null -> Text("Total posts: $total")
            error != null -> Text("Error: $error")
        }
    }
}
```

- **Receives `HomeViewModel` as a parameter** — the screen does not create the ViewModel itself
- **`collectAsState()`** converts `StateFlow` into Compose state; Compose recomposes the UI automatically whenever the value changes
- The UI only knows how to display state and report user actions — all logic stays in the ViewModel

---

### 9. `MainActivity.kt` — Connecting Everything

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appContainer = (application as ManualDIApp).appContainer
        val homeViewModelFactory = HomeViewModelFactory(appContainer.postsRepository)
        enableEdgeToEdge()
        setContent {
            ManualDIappTheme { }
        }
    }
}
```

The Activity:
1. Gets `AppContainer` from the Application
2. Creates `HomeViewModelFactory` with the repository from the container
3. Passes the factory to Compose so `viewModel(factory = ...)` can create `HomeViewModel`

This is the only place in the UI layer that touches the DI container directly — everything below receives dependencies through their constructors.

---

## The Full Dependency Chain

```
ManualDIApp.onCreate()
    └── AppContainer()
         ├── OkHttpClient  ──────────────────────┐
         ├── Moshi  ──────────────────────────── Retrofit
         │                                          │
         │                                     ApiService
         │                                          │
         └── PostsRepository(apiService) ◄──────────┘
                    │
         HomeViewModelFactory(postsRepository)
                    │
            HomeViewModel(repository)    ← created & cached by Android
                    │
             HomeScreen(viewModel)       ← UI reacts to StateFlow
```

Each arrow means "injects into". No class in this chain creates its own dependencies.

---

## Why Not Just Use Hilt?

| | Manual DI | Hilt |
|---|---|---|
| Boilerplate | You write `AppContainer`, factories | Generated by annotation processor |
| Visibility | You see every dependency explicitly | Hidden behind annotations |
| Learning | Understand what frameworks do | Faster to ship |
| Scoping | Manual (`val` = singleton) | `@Singleton`, `@ActivityScoped`, etc. |
| Testing | Swap dependencies manually | `@TestInstallIn` |

Manual DI is the best way to **understand** DI. Once you understand it, Hilt just automates the parts you wrote by hand here.

---

## Tech Stack

| Library | Purpose |
|---|---|
| Retrofit 2.11 | HTTP client, turns the interface into real API calls |
| Moshi 1.15 | JSON parsing into Kotlin data classes |
| OkHttp Logging Interceptor | Logs HTTP requests/responses in debug builds |
| Kotlin Coroutines | Async network calls without blocking the main thread |
| Kotlin StateFlow | Observable state stream from ViewModel to UI |
| Jetpack Compose | Declarative UI |
| Navigation Compose | Screen navigation |
| ViewModelProvider.Factory | Custom ViewModel construction with arguments |

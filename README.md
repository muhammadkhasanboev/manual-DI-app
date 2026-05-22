# Manual Dependency Injection — Android Learning Project

This project demonstrates how **Dependency Injection (DI) works without any framework** (no Hilt, no Dagger, no Koin). Everything is wired by hand so you can see exactly what a DI framework does under the hood.

The app fetches posts and quotes from [dummyjson.com](https://dummyjson.com) and lets the user read through them one by one.

---

## What is Dependency Injection?

A class has a **dependency** when it needs another object to do its job.

```kotlin
// WITHOUT DI — the class creates its own dependency
class PostsRepositoryImpl {
    private val api = ApiService() // tightly coupled, hard to test or swap
}

// WITH DI — the dependency is given from outside
class PostsRepositoryImpl(private val api: ApiService) // loosely coupled
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
      └── AppContainer               ← creates & holds all dependencies
           ├── OkHttpClient
           ├── Moshi
           ├── Retrofit
           ├── ApiService
           ├── PostsRepository (interface) ← PostsRepositoryImpl
           └── QuotesRepository (interface) ← QuotesRepositoryImpl
                    │
                    ▼
     ┌──────────────────────────────┐
     │                              │
HomeViewModelFactory   PostsViewModelFactory   QuotesViewModelFactory
     │                              │                    │
HomeViewModel      PostsViewModel       QuotesViewModel
     │                              │                    │
HomeScreen         PostsScreen          QuotesScreen
```

The dependency graph flows **top-down**: each layer receives what it needs from the layer above. Nothing creates its own dependencies.

---

## Screens

| Screen | Purpose |
|---|---|
| `HomeScreen` | Navigation hub — two cards to choose Posts or Quotes |
| `PostsScreen` | Loads all posts, shows one at a time, Next button to advance |
| `QuotesScreen` | Loads all quotes, shows one at a time, Next button to advance |

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

`AppContainer` is created once here and stored as a property. `MainActivity` accesses it via `application as ManualDIApp`.

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
    val postsRepository: PostsRepository = PostsRepositoryImpl(apiService)
    val quotesRepository: QuotesRepository = QuotesRepositoryImpl(apiService)
}
```

This is the heart of manual DI. It:

1. **Builds the network stack** in the correct order: `OkHttpClient` → `Retrofit` → `ApiService`
2. **Injects `ApiService` into each repository** — the `Impl` classes receive it, they do not create it
3. **Types repositories against their interfaces** (`PostsRepository`, not `PostsRepositoryImpl`) — nothing outside this container knows the `Impl` exists
4. **Exposes everything as `val`** — same instance reused everywhere (singleton behaviour)

> In Hilt/Dagger, `@Provides` and `@Singleton` tell the framework to do this wiring automatically.

---

### 3. Repository Interfaces — Abstraction Layer

```kotlin
interface PostsRepository {
    suspend fun getPosts(): Result<posts>
}

interface QuotesRepository {
    suspend fun getQuotes(): Result<Quotes>
}
```

The ViewModels depend on these interfaces, not the implementations. This means:
- You can swap `PostsRepositoryImpl` for a fake/test version without touching the ViewModel
- The ViewModel has no idea whether data comes from a network, a database, or a local file

> In Hilt, `@Binds` maps an interface to its implementation automatically.

---

### 4. Repository Implementations — The Data Layer

```kotlin
class PostsRepositoryImpl(
    private val api: ApiService
) : PostsRepository {
    override suspend fun getPosts(): Result<posts> {
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

- **Receives `ApiService` via constructor** — DI in action
- **Wraps the result** in Kotlin's `Result<T>` — the ViewModel gets a clean success or failure, never a raw exception
- **`QuotesRepositoryImpl`** follows the exact same pattern for quotes

---

### 5. `ApiService.kt` — The Network Interface

```kotlin
interface ApiService {
    @GET("/posts")
    suspend fun getPosts(): Response<posts>

    @GET("/quotes")
    suspend fun getQuotes(): Response<Quotes>
}
```

Retrofit generates the actual HTTP implementation at runtime via `retrofit.create(ApiService::class.java)`. `suspend` means calls run on a coroutine and never block the main thread.

---

### 6. DTOs — Data Transfer Objects

```
data/remote/dto/posts/
    posts.kt     — root response: total, limit, skip, List<Post>
    Post.kt      — single post: id, title, body, tags, views, userId, reactions
    Reactions.kt — likes and dislikes

data/remote/dto/quotes/
    Quotes.kt    — root response: total, limit, skip, List<Quote>
    Quote.kt     — single quote: id, quote, author
```

Moshi maps JSON directly into these data classes. The 3-level nesting of the posts JSON (`posts → post → reactions`) maps to the 3 data classes.

---

### 7. ViewModelFactories — Bridging DI and the ViewModel Lifecycle

```kotlin
class PostsViewModelFactory(
    private val repository: PostsRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PostsViewModel::class.java)) {
            return PostsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
```

Android creates ViewModels itself to handle lifecycle and screen rotation. The problem: ViewModels need constructor arguments (repositories), but Android's default factory only handles no-argument ViewModels.

`ViewModelProvider.Factory` solves this — you tell Android *how* to build the ViewModel, Android handles the rest.

The project has three factories: `HomeViewModelFactory`, `PostsViewModelFactory`, `QuotesViewModelFactory` — all follow this same pattern.

> In Hilt, `@HiltViewModel` + `@Inject constructor` eliminates the need for these factories entirely.

---

### 8. ViewModels — State and Business Logic

Each ViewModel exposes a single `StateFlow` with a **sealed class UiState** that covers all possible screen states:

```kotlin
sealed class PostsUIState {
    object Idle : PostsUIState()
    object Loading : PostsUIState()
    data class Success(val post: String) : PostsUIState()
    data class Error(val message: String) : PostsUIState()
}

class PostsViewModel(
    private val repository: PostsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PostsUIState>(PostsUIState.Idle)
    val uiState: StateFlow<PostsUIState> = _uiState

    private val _allPosts = mutableListOf<Post>()
    private var currentIndex = 0

    fun getPost() {
        _uiState.value = PostsUIState.Loading
        viewModelScope.launch {
            repository.getPosts()
                .onSuccess {
                    _allPosts.addAll(it.posts)
                    _uiState.value = PostsUIState.Success(_allPosts[currentIndex].body)
                }
                .onFailure {
                    _uiState.value = PostsUIState.Error(it.message ?: "Unknown error")
                }
        }
    }

    fun nextPost() {
        if (currentIndex < _allPosts.size - 1) {
            currentIndex++
            _uiState.value = PostsUIState.Success(_allPosts[currentIndex].body)
        }
    }
}
```

Key points:
- **Single flow** — one `_uiState` instead of multiple nullable flows
- **Sealed class** — the UI can never be in an ambiguous state; `null` never means "loading" or "error"
- **Private mutable, public read-only** — `_uiState` is `MutableStateFlow`, `uiState` is `StateFlow` — the UI can only read, never write
- **All posts loaded once** — `getPost()` fetches and stores the full list; `nextPost()` just advances the index with no extra network call
- **`viewModelScope`** — coroutine scope tied to the ViewModel lifecycle, cancelled automatically when the ViewModel is cleared
- `QuotesViewModel` follows the exact same pattern with `QuoteUIState`

---

### 9. Screens — The UI Layer

```kotlin
@Composable
fun PostsScreen(viewModel: PostsViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getPost()
    }

    Column(...) {
        when (val state = uiState) {
            is PostsUIState.Idle    -> Text("Tap button to get Post")
            is PostsUIState.Loading -> CircularProgressIndicator()
            is PostsUIState.Success -> Text(state.post)
            is PostsUIState.Error   -> Text(state.message)
        }
        Button(onClick = { viewModel.nextPost() }) {
            Text("Next post")
        }
    }
}
```

- **Receives ViewModel as a parameter** — does not create it
- **`collectAsState()`** — converts `StateFlow` into Compose state; recomposes automatically on each change
- **`LaunchedEffect(Unit)`** — runs once when the screen first appears, triggers the data fetch
- **`when (val state = uiState)`** — smart cast; inside each branch `state` is the specific type so `.post` and `.message` are accessible directly
- The compiler forces you to handle all 4 cases — no state can be missed

`HomeScreen` shows two `CustomCard` composables and calls `onPosts()` or `onQuotes()` lambdas on click — it does not navigate itself, it just reports the user action up to `AppNavigation`.

---

### 10. `AppNavigation.kt` — Navigation and ViewModel Scoping

```kotlin
@Composable
fun AppNavigation(
    homeViewModelFactory: HomeViewModelFactory,
    postsViewModelFactory: PostsViewModelFactory,
    quotesViewModelFactory: QuotesViewModelFactory
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onPosts = { navController.navigate(Routes.POSTS) },
                onQuotes = { navController.navigate(Routes.QUOTES) },
                viewModel = viewModel(factory = postsViewModelFactory)
            )
        }
        composable(Routes.POSTS) {
            PostsScreen(viewModel = viewModel(factory = postsViewModelFactory))
        }
        composable(Routes.QUOTES) {
            QuotesScreen(viewModel = viewModel(factory = quotesViewModelFactory))
        }
    }
}
```

- Receives all three factories — it is the only Composable that touches factories directly
- `viewModel(factory = ...)` creates the ViewModel scoped to the nav back stack entry — it survives recomposition but is cleared when you leave the screen
- Navigation callbacks (`onPosts`, `onQuotes`) are passed as lambdas — screens never touch `navController` directly

---

### 11. `MainActivity.kt` — Connecting Everything

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appContainer = (application as ManualDIApp).appContainer
        val homeViewModelFactory = HomeViewModelFactory(appContainer.postsRepository)
        val postsViewModelFactory = PostsViewModelFactory(appContainer.postsRepository)
        val quotesViewModelFactory = QuotesViewModelFactory(appContainer.quotesRepository)
        enableEdgeToEdge()
        setContent {
            ManualDIappTheme {
                AppNavigation(homeViewModelFactory, postsViewModelFactory, quotesViewModelFactory)
            }
        }
    }
}
```

The Activity is the **only place** in the UI layer that touches `AppContainer` directly. It creates all factories, then hands them to `AppNavigation`. Everything below receives its dependencies through parameters.

---

## The Full Dependency Chain

```
ManualDIApp.onCreate()
    └── AppContainer()
         ├── OkHttpClient
         ├── Moshi           ──► Retrofit ──► ApiService
         │                                       │
         ├── PostsRepository  ◄── PostsRepositoryImpl(apiService)
         └── QuotesRepository ◄── QuotesRepositoryImpl(apiService)
                  │
         MainActivity
         ├── HomeViewModelFactory(postsRepository)
         ├── PostsViewModelFactory(postsRepository)
         └── QuotesViewModelFactory(quotesRepository)
                  │
         AppNavigation(homeFactory, postsFactory, quotesFactory)
         ├── HomeViewModel(repository)    ← scoped to nav entry
         ├── PostsViewModel(repository)   ← scoped to nav entry
         └── QuotesViewModel(repository)  ← scoped to nav entry
                  │
         Screens collect StateFlow<UiState> and render
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
| Interface binding | Manual in `AppContainer` | `@Binds` annotation |
| Factory | Write `ViewModelProvider.Factory` | `@HiltViewModel` + `@Inject constructor` |
| Testing | Swap in `AppContainer` | `@TestInstallIn` |

Manual DI is the best way to **understand** DI. Once you understand it, Hilt just automates the parts you wrote by hand here.

---

## Tech Stack

| Library | Purpose |
|---|---|
| Retrofit 2.11 | HTTP client, turns the `ApiService` interface into real network calls |
| Moshi 1.15 | JSON parsing into Kotlin data classes |
| OkHttp Logging Interceptor | Logs full HTTP requests/responses in debug builds |
| Kotlin Coroutines | Async network calls without blocking the main thread |
| Kotlin StateFlow | Observable state stream from ViewModel to UI |
| Sealed Classes | Type-safe UiState — Loading, Success, Error, Idle |
| Jetpack Compose | Declarative UI |
| Navigation Compose | Screen navigation and back stack management |
| ViewModelProvider.Factory | Custom ViewModel construction with constructor arguments |

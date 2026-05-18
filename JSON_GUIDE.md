# How to Access Fields from the API Response

## The JSON Structure

The API returns this shape:

```
{
  "posts": [ ...list of posts... ],
  "total": 251,
  "skip": 0,
  "limit": 30
}
```

Inside each post:

```
{
  "id": 1,
  "title": "His mother had always taught him",
  "body": "...",
  "tags": ["history", "american", "crime"],
  "reactions": {
    "likes": 192,
    "dislikes": 25
  },
  "views": 305,
  "userId": 121
}
```

So it is 3 levels deep:
- **Level 1** — the root object: `total`, `skip`, `limit`, `posts`
- **Level 2** — a single post inside the `posts` list: `id`, `title`, `body`, `tags`, `reactions`, `views`, `userId`
- **Level 3** — inside `reactions`: `likes`, `dislikes`

---

## Your Kotlin DTOs Already Match This

You already have 3 data classes, one for each level:

```kotlin
// Level 1 — the root
data class posts(
    val total: Int,
    val skip: Int,
    val limit: Int,
    val posts: List<Post>
)

// Level 2 — a single post
data class Post(
    val id: Int,
    val title: String,
    val body: String,
    val tags: List<String>,
    val reactions: Reactions,   // <-- points to level 3
    val views: Int,
    val userId: Int
)

// Level 3 — inside reactions
data class Reactions(
    val likes: Int,
    val dislikes: Int
)
```

Moshi reads the JSON and fills these classes automatically. You don't do any parsing yourself.

---

## Step-by-Step: How to Get to Any Field

After `repository.getPosts()` succeeds, you have a `posts` object. Let's call it `response`.

---

### Step 1 — Get the total number of posts

```kotlin
val total = response.total
// total = 251
```

This is level 1. Direct field on the root object.

---

### Step 2 — Get the list of all posts

```kotlin
val allPosts = response.posts
// allPosts is a List<Post> with 30 items (one page)
```

---

### Step 3 — Get a specific post by index

```kotlin
val firstPost = response.posts[0]   // first post (id = 1)
val secondPost = response.posts[1]  // second post (id = 2)
```

Lists in Kotlin start at index 0.

---

### Step 4 — Get fields from a specific post

```kotlin
val firstPost = response.posts[0]

val id    = firstPost.id       // 1
val title = firstPost.title    // "His mother had always taught him"
val body  = firstPost.body     // "His mother had always taught him not to..."
val views = firstPost.views    // 305
val userId = firstPost.userId  // 121
```

---

### Step 5 — Get the tags (it's a list)

```kotlin
val tags = firstPost.tags
// tags = ["history", "american", "crime"]

val firstTag = firstPost.tags[0]   // "history"
val secondTag = firstPost.tags[1]  // "american"
```

---

### Step 6 — Get likes and dislikes (level 3)

```kotlin
val likes    = firstPost.reactions.likes     // 192
val dislikes = firstPost.reactions.dislikes  // 25
```

You go through `reactions` to reach `likes` and `dislikes` because in JSON they are nested one level deeper inside the post.

---

## The Full Path Written Out

```kotlin
// After getting the response:
val response = repository.getPosts().getOrNull() ?: return

// Root level
response.total          // 251
response.limit          // 30
response.skip           // 0

// A specific post (first one)
response.posts[0].id        // 1
response.posts[0].title     // "His mother had always taught him"
response.posts[0].body      // "His mother had always taught him not to..."
response.posts[0].views     // 305
response.posts[0].userId    // 121
response.posts[0].tags[0]   // "history"

// Reactions inside the first post (level 3)
response.posts[0].reactions.likes     // 192
response.posts[0].reactions.dislikes  // 25
```

---

## Visual Map

```
response                          ← posts object (level 1)
├── total = 251
├── skip = 0
├── limit = 30
└── posts = [
      posts[0]                    ← Post object (level 2)
      ├── id = 1
      ├── title = "His mother..."
      ├── body = "His mother had always..."
      ├── views = 305
      ├── userId = 121
      ├── tags = ["history", "american", "crime"]
      └── reactions               ← Reactions object (level 3)
          ├── likes = 192
          └── dislikes = 25

      posts[1]                    ← second Post
      ├── id = 2
      ├── title = "He was an expert..."
      └── reactions
          ├── likes = 859
          └── dislikes = 32

      ... and so on up to posts[29]
   ]
```

---

## Summary

| What you want | How to get it |
|---|---|
| Total posts count | `response.total` |
| All posts | `response.posts` |
| First post | `response.posts[0]` |
| Title of first post | `response.posts[0].title` |
| Views of first post | `response.posts[0].views` |
| Tags of first post | `response.posts[0].tags` |
| First tag of first post | `response.posts[0].tags[0]` |
| Likes of first post | `response.posts[0].reactions.likes` |
| Dislikes of first post | `response.posts[0].reactions.dislikes` |
| Second post's likes | `response.posts[1].reactions.likes` |

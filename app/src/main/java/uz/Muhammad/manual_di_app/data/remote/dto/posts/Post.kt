package uz.Muhammad.manual_di_app.data.remote.dto.posts

import uz.Muhammad.manual_di_app.data.remote.dto.posts.Reactions

data class Post(
    val body: String,
    val id: Int,
    val reactions: Reactions,
    val tags: List<String>,
    val title: String,
    val userId: Int,
    val views: Int
)
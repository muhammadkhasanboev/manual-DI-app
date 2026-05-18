package uz.Muhammad.manual_di_app.data.remote.dto

data class posts(
    val limit: Int,
    val posts: List<Post>,
    val skip: Int,
    val total: Int
)
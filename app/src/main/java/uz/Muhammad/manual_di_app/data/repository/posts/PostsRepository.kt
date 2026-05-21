package uz.Muhammad.manual_di_app.data.repository.posts

import uz.Muhammad.manual_di_app.data.remote.dto.posts.posts

interface PostsRepository {

    suspend fun getPosts(): Result<posts>

}
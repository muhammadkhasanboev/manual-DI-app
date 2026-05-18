package uz.Muhammad.manual_di_app.data.repository.posts

import uz.Muhammad.manual_di_app.data.remote.api.ApiService
import uz.Muhammad.manual_di_app.data.remote.dto.posts

class PostsRepository(
    private val api: ApiService
){
    suspend fun getPosts(): Result<posts>{
        return try {
            val response = api.getPosts()
            val body = response.body()
            if(body!=null){
                Result.success(body)
            }else{
                Result.failure(Exception("Empty response"))
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}
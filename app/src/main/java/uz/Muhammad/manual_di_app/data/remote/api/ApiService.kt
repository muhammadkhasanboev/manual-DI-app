package uz.Muhammad.manual_di_app.data.remote.api

import retrofit2.Response
import retrofit2.http.GET
import uz.Muhammad.manual_di_app.data.remote.dto.posts.posts
import uz.Muhammad.manual_di_app.data.remote.dto.quotes.Quotes

interface ApiService {
    @GET("/posts")
    suspend fun getPosts(): Response<posts>

    @GET("/quotes")
    suspend fun getQuotes(): Response<Quotes>
}
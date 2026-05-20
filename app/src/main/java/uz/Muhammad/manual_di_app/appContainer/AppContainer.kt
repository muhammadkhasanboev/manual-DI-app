package uz.Muhammad.manual_di_app.appContainer

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import uz.Muhammad.manual_di_app.BuildConfig
import uz.Muhammad.manual_di_app.data.remote.api.ApiService
import uz.Muhammad.manual_di_app.data.repository.posts.PostsRepository
import uz.Muhammad.manual_di_app.data.repository.quotes.QuotesRepository
import kotlin.apply

class AppContainer {
    private val logging = HttpLoggingInterceptor().apply{
        level = if(BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
        else HttpLoggingInterceptor.Level.NONE
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://dummyjson.com")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(okHttpClient)
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
    val postsRepository: PostsRepository = PostsRepository(apiService)
    val quotesRepository: QuotesRepository = QuotesRepository(apiService)
}
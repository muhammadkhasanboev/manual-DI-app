package uz.Muhammad.manual_di_app.data.repository.quotes

import uz.Muhammad.manual_di_app.data.remote.api.ApiService
import uz.Muhammad.manual_di_app.data.remote.dto.quotes.Quotes

class QuotesRepositoryImpl(
    private val api: ApiService
): QuotesRepository {
    override suspend fun getQuotes(): Result<Quotes>{
        return try{
            val response = api.getQuotes()
            val body = response.body()

            if(body!=null){
                Result.success(body)
            }else{
                Result.failure(Exception("Empty response body"))
            }
        } catch (e: Exception){
            Result.failure(e)
        }
    }
}
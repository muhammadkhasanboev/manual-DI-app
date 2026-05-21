package uz.Muhammad.manual_di_app.data.repository.quotes

import uz.Muhammad.manual_di_app.data.remote.dto.quotes.Quotes

interface QuotesRepository {
    suspend fun getQuotes(): Result<Quotes>
}
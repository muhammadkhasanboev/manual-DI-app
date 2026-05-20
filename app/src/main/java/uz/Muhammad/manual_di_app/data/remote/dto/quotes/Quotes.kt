package uz.Muhammad.manual_di_app.data.remote.dto.quotes

data class Quotes(
    val limit: Int,
    val quotes: List<Quote>,
    val skip: Int,
    val total: Int
)
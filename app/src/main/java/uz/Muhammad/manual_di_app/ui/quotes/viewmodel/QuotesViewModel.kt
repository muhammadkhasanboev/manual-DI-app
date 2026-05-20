package uz.Muhammad.manual_di_app.ui.quotes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.Muhammad.manual_di_app.data.remote.dto.quotes.Quote
import uz.Muhammad.manual_di_app.data.remote.dto.quotes.Quotes
import uz.Muhammad.manual_di_app.data.repository.quotes.QuotesRepository

class QuotesViewModel(
    private val repository: QuotesRepository
): ViewModel() {
    private val _quote = MutableStateFlow<String?>(null)
    val quote : StateFlow<String?> = _quote

    private val _allQuotes = mutableListOf<Quote>()
    private var currentIndex = 0

    fun nextQuote(){
        if(currentIndex<_allQuotes.size-1){
            currentIndex++
            _quote.value = _allQuotes[currentIndex].quote
        }
    }

    fun getQuote(){
        viewModelScope.launch {
            val result = repository.getQuotes()
            result.onSuccess {
                _allQuotes.addAll(it.quotes)
                _quote.value = it.quotes[currentIndex].quote
            }
        }
    }
}
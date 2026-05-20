package uz.Muhammad.manual_di_app.ui.quotes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.Muhammad.manual_di_app.data.repository.quotes.QuotesRepository

class QuotesViewModel(
    private val repository: QuotesRepository
): ViewModel() {
    private val _quote = MutableStateFlow<String?>(null)
    val quote : StateFlow<String?> = _quote

    fun getQuote(){
        viewModelScope.launch {
            val result = repository.getQuotes()
            result.onSuccess {
                _quote.value = it.quotes[0].quote
            }
        }
    }
}
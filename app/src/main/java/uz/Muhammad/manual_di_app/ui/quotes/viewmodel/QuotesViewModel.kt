package uz.Muhammad.manual_di_app.ui.quotes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.Muhammad.manual_di_app.data.remote.dto.quotes.Quote
import uz.Muhammad.manual_di_app.data.repository.quotes.QuotesRepository

class QuotesViewModel(
    private val repository: QuotesRepository
): ViewModel() {

    private val _allQuotes = mutableListOf<Quote>()
    private var currentIndex = 0

    private val _uiState = MutableStateFlow<QuoteUIState>(QuoteUIState.Idle)
    val uiState: StateFlow<QuoteUIState> = _uiState

    fun nextQuote(){
        if(currentIndex<_allQuotes.size-1){
            currentIndex++
            _uiState.value = QuoteUIState.Success(_allQuotes[currentIndex].quote)
        }
    }

    fun getQuote(){
        _uiState.value = QuoteUIState.Loading
        viewModelScope.launch {
            val result = repository.getQuotes()
            result.onSuccess {
                _allQuotes.addAll(it.quotes)
                _uiState.value = QuoteUIState.Success(_allQuotes[currentIndex].quote)
            }
                .onFailure{
                    _uiState.value = QuoteUIState.Error("Unknown error")
                }
        }
    }
}


sealed class QuoteUIState{
    object Idle: QuoteUIState()
    object Loading: QuoteUIState()
    data class Error(val message: String): QuoteUIState()
    data class Success(val quote: String): QuoteUIState()
}
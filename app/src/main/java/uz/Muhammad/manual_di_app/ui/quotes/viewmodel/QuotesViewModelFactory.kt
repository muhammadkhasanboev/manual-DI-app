package uz.Muhammad.manual_di_app.ui.quotes.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import uz.Muhammad.manual_di_app.data.repository.quotes.QuotesRepository
import uz.Muhammad.manual_di_app.data.repository.quotes.QuotesRepositoryImpl

class QuotesViewModelFactory(
    private val repository: QuotesRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(QuotesViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return QuotesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package uz.Muhammad.manual_di_app.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.Muhammad.manual_di_app.data.repository.posts.PostsRepository
import uz.Muhammad.manual_di_app.data.repository.posts.PostsRepositoryImpl

class HomeViewModel(
    private val repository: PostsRepository
) : ViewModel() {

    private val _total = MutableStateFlow<Int?>(null)
    val total: StateFlow<Int?> = _total

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _post = MutableStateFlow<String?>(null)
    val post: StateFlow<String?> = _post

    fun getPosts() {
        viewModelScope.launch {
            val result = repository.getPosts()
            result
                .onSuccess {
                    _total.value = it.total
                    _post.value = it.posts[0].body
                }
                .onFailure { _error.value = it.message }
        }
    }
}
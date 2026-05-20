package uz.Muhammad.manual_di_app.ui.posts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.Muhammad.manual_di_app.data.repository.posts.PostsRepository

class PostsViewModel(
    private val repository: PostsRepository
): ViewModel() {

    private val _post = MutableStateFlow<String?>(null)
    val post: StateFlow<String?> = _post

    fun getPost(){
        viewModelScope.launch {
            val result = repository.getPosts()
            result .onSuccess{
                _post.value = it.posts[0].body
            }
        }
    }
}
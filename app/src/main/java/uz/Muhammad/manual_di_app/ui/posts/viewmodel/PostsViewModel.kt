package uz.Muhammad.manual_di_app.ui.posts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.Muhammad.manual_di_app.data.remote.dto.posts.Post
import uz.Muhammad.manual_di_app.data.repository.posts.PostsRepository

class PostsViewModel(
    private val repository: PostsRepository
): ViewModel() {

   private val _uiState = MutableStateFlow<PostsUIState>(PostsUIState.Idle)
    val uiState: StateFlow<PostsUIState> = _uiState

    private val _allPosts = mutableListOf<Post>()
    private var currentIndex = 0

    fun nextPost(){
        if(currentIndex< _allPosts.size - 1){
            currentIndex++
            _uiState.value = PostsUIState.Success(_allPosts[currentIndex].body)
        }
    }

    fun getPost(){
        viewModelScope.launch {
            val result = repository.getPosts()
            result .onSuccess{
                _allPosts.addAll(it.posts)
                _uiState.value = PostsUIState.Success(_allPosts[currentIndex].body)
            }
        }
    }
}

sealed class PostsUIState{
    object Idle: PostsUIState()
    object Loading: PostsUIState()
    data class Success(val post: String): PostsUIState()
    data class Error(val message: String): PostsUIState()
}
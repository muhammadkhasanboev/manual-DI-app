package uz.Muhammad.manual_di_app.ui.posts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import uz.Muhammad.manual_di_app.data.remote.dto.posts.Post
import uz.Muhammad.manual_di_app.data.repository.posts.PostsRepository
import uz.Muhammad.manual_di_app.data.repository.posts.PostsRepositoryImpl

class PostsViewModel(
    private val repository: PostsRepository
): ViewModel() {

    private val _post = MutableStateFlow<String?>(null)
    val post: StateFlow<String?> = _post

    private val _allPosts = mutableListOf<Post>()
    private var currentIndex = 0

    fun nextPost(){
        if(currentIndex< _allPosts.size - 1){
            currentIndex++
            _post.value = _allPosts[currentIndex].body
        }
    }

    fun getPost(){
        viewModelScope.launch {
            val result = repository.getPosts()
            result .onSuccess{
                _allPosts.addAll(it.posts)
                _post.value = it.posts[currentIndex].body
            }
        }
    }
}
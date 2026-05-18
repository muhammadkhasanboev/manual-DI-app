package uz.Muhammad.manual_di_app.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uz.Muhammad.manual_di_app.data.repository.posts.PostsRepository

class HomeViewModel(
    private val repository: PostsRepository
): ViewModel() {
    fun getPosts(){
        viewModelScope.launch {
            val result = repository.getPosts()
        }
    }
}
package uz.Muhammad.manual_di_app.ui.posts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import uz.Muhammad.manual_di_app.data.repository.posts.PostsRepository
import uz.Muhammad.manual_di_app.data.repository.posts.PostsRepositoryImpl

class PostsViewModelFactory(
    private val repository: PostsRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(PostsViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return PostsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
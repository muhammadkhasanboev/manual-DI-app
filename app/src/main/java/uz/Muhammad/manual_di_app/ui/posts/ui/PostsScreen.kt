package uz.Muhammad.manual_di_app.ui.posts.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.Muhammad.manual_di_app.ui.home.viewmodel.HomeViewModel
import uz.Muhammad.manual_di_app.ui.posts.viewmodel.PostsViewModel

@Composable
fun PostsScreen(
    viewModel: PostsViewModel
){
    val post by viewModel.post.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.getPost()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(25.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        when {
            post != null -> Text("$post")
            else -> Text("Network problems")
        }
    }
}
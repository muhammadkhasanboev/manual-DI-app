package uz.Muhammad.manual_di_app.ui.posts.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.Muhammad.manual_di_app.ui.home.viewmodel.HomeViewModel

@Composable
fun PostsScreen(
    viewModel: HomeViewModel
){
    val post by viewModel.post.collectAsState()

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
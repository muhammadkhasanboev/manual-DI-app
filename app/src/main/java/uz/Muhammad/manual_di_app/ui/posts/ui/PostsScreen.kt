package uz.Muhammad.manual_di_app.ui.posts.ui

import android.widget.Button
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.Muhammad.manual_di_app.ui.home.viewmodel.HomeViewModel
import uz.Muhammad.manual_di_app.ui.posts.viewmodel.PostsUIState
import uz.Muhammad.manual_di_app.ui.posts.viewmodel.PostsViewModel

@Composable
fun PostsScreen(
    viewModel: PostsViewModel
){
    val uiState by viewModel.uiState.collectAsState()
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
        when(val state = uiState) {
            is PostsUIState.Idle -> Text("Tap button to get Post")
            is PostsUIState.Loading -> CircularProgressIndicator()
            is PostsUIState.Success -> Text(state.post)
            is PostsUIState.Error -> Text(state.message)
        }

        Button(
            onClick={viewModel.nextPost()}
        ){
            Text("Next post")
        }
    }
}
package uz.Muhammad.manual_di_app.ui.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import uz.Muhammad.manual_di_app.ui.home.components.CustomCard
import uz.Muhammad.manual_di_app.ui.posts.viewmodel.PostsViewModel

@Composable
fun HomeScreen(
    onPosts: () -> Unit,
    onQuotes: () -> Unit,
    viewModel: PostsViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(25.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "Choose what you want to read:",
            modifier = Modifier.fillMaxWidth().padding(top=30.dp),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        CustomCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onPosts()
            }
        ) {
            Text(
                text="Posts",
                modifier = Modifier.padding(10.dp),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        CustomCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onQuotes()
            }) {
            Text(
                text="Quotes",
                modifier = Modifier.padding(10.dp),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }

    }
}
package uz.Muhammad.manual_di_app.ui.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import uz.Muhammad.manual_di_app.ui.home.components.CustomCard
import uz.Muhammad.manual_di_app.ui.home.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onPosts: () -> Unit,
    onQuotes: () -> Unit,
    viewModel: HomeViewModel
) {
//    val total by viewModel.total.collectAsState()
//    val error by viewModel.error.collectAsState()
//    val post by viewModel.post.collectAsState()
//
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Button(onClick = { viewModel.getPosts() }) {
//            Text("Get Posts")
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        when {
////            total != null -> Text("Total posts: $total")
//            post !=null->Text("$post \n $total")
//            error != null -> Text("Error: $error")
//        }
//    }

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
            }) {
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
            onClick = { }) {
            Text(
                text="Quotes",
                modifier = Modifier.padding(10.dp),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }

    }
}
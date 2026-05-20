package uz.Muhammad.manual_di_app.ui.quotes.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import uz.Muhammad.manual_di_app.ui.quotes.viewmodel.QuotesViewModel

@Composable
fun QuotesScreen(
    viewModel: QuotesViewModel
){
    val quote by viewModel.quote.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.getQuote()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(25.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        when {
                quote != null -> Text("$quote")
            else -> Text("Network problems")
        }

        Button(
            onClick = {viewModel.nextQuote()}
        ){
            Text("Next quote")
        }
    }
}
package uz.Muhammad.manual_di_app.ui.quotes.ui

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
import uz.Muhammad.manual_di_app.ui.quotes.viewmodel.QuoteUIState
import uz.Muhammad.manual_di_app.ui.quotes.viewmodel.QuotesViewModel

@Composable
fun QuotesScreen(
    viewModel: QuotesViewModel
){
    val uiState by viewModel.uiState.collectAsState()

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
        when(val state = uiState){
            is QuoteUIState.Idle -> Text("Press button to load")
            is QuoteUIState.Loading -> CircularProgressIndicator()
            is QuoteUIState.Success -> Text(state.quote)
            is QuoteUIState.Error -> Text(state.message)
        }
        Button(
            onClick = {viewModel.nextQuote()}
        ){
            Text("Next quote")
        }
    }
}
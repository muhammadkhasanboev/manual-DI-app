package uz.Muhammad.manual_di_app.ui.home.components

import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import uz.Muhammad.manual_di_app.ui.theme.Purple80

@Composable
fun CustomCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
){
    ElevatedCard(
        modifier = modifier,
        shape = CardDefaults.elevatedShape,
        colors = CardDefaults.cardColors(
            containerColor = Purple80,
            contentColor = Color.White,
            disabledContentColor = Color.White,
            disabledContainerColor = Purple80
        )
    ) {
        content()
    }
}
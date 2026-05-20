package uz.Muhammad.manual_di_app.ui.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import uz.Muhammad.manual_di_app.ui.theme.Purple80

@Composable
fun CustomCard(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable () -> Unit
){
    ElevatedCard(
        modifier = modifier.clickable(
            onClick = { }
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp
        ),
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
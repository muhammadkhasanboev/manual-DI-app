package uz.Muhammad.manual_di_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import uz.Muhammad.manual_di_app.ui.home.viewmodel.HomeViewModelFactory
import uz.Muhammad.manual_di_app.ui.theme.ManualDIappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appContainer = (application as ManualDIApp).appContainer
        val homeViewModelFactory = HomeViewModelFactory(appContainer.postsRepository)
        enableEdgeToEdge()
        setContent {
            ManualDIappTheme {
            }
        }
    }
}

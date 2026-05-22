package uz.Muhammad.manual_di_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import uz.Muhammad.manual_di_app.navigation.AppNavigation
import uz.Muhammad.manual_di_app.ui.posts.viewmodel.PostsViewModelFactory
import uz.Muhammad.manual_di_app.ui.quotes.viewmodel.QuotesViewModelFactory
import uz.Muhammad.manual_di_app.ui.theme.ManualDIappTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appContainer = (application as ManualDIApp).appContainer
        val postsViewModelFactory = PostsViewModelFactory(appContainer.postsRepository)
        val quotesViewModelFactory = QuotesViewModelFactory(appContainer.quotesRepository)
        enableEdgeToEdge()
        setContent {
            ManualDIappTheme {
                AppNavigation(
                    postsViewModelFactory,
                    quotesViewModelFactory)
            }
        }
    }
}

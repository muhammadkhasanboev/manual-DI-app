package uz.Muhammad.manual_di_app.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uz.Muhammad.manual_di_app.ui.home.ui.HomeScreen
import uz.Muhammad.manual_di_app.ui.posts.ui.PostsScreen
import uz.Muhammad.manual_di_app.ui.posts.viewmodel.PostsViewModelFactory
import uz.Muhammad.manual_di_app.ui.quotes.ui.QuotesScreen
import uz.Muhammad.manual_di_app.ui.quotes.viewmodel.QuotesViewModelFactory

@Composable
fun AppNavigation(
    postsViewModelFactory: PostsViewModelFactory,
    quotesViewModelFactory: QuotesViewModelFactory){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ){
        composable(Routes.HOME){
            HomeScreen(
                onPosts = {navController.navigate(Routes.POSTS)},
                onQuotes = {navController.navigate((Routes.QUOTES))},
                viewModel = viewModel(factory = postsViewModelFactory)
            )
        }
        composable(Routes.POSTS) {
            PostsScreen(
                viewModel = viewModel(factory = postsViewModelFactory)
            )
        }
        composable(Routes.QUOTES) {
            QuotesScreen(
                viewModel = viewModel(factory = quotesViewModelFactory)
            )
        }
    }
}
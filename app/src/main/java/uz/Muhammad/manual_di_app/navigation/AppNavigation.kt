package uz.Muhammad.manual_di_app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.RoundRect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import uz.Muhammad.manual_di_app.ui.home.ui.HomeScreen
import uz.Muhammad.manual_di_app.ui.home.viewmodel.HomeViewModelFactory
import uz.Muhammad.manual_di_app.ui.posts.ui.PostsScreen
import uz.Muhammad.manual_di_app.ui.posts.viewmodel.PostsViewModel
import uz.Muhammad.manual_di_app.ui.posts.viewmodel.PostsViewModelFactory
import uz.Muhammad.manual_di_app.ui.quotes.viewmodel.QuotesViewModelFactory

@Composable
fun AppNavigation(
    homeViewModelFactory: HomeViewModelFactory,
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
                onQuotes = {},
                viewModel = viewModel(factory = postsViewModelFactory)
            )
        }
        composable(Routes.POSTS) {
            PostsScreen(
                viewModel = viewModel(factory = postsViewModelFactory)
            )
        }
        composable(Routes.QUOTES) {

        }
    }
}
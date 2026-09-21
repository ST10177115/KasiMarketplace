package com.kasimarketplace.app.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kasimarketplace.app.ui.screens.*
import com.kasimarketplace.app.util.AppContainer
import com.kasimarketplace.app.util.ViewModelFactory

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val PRODUCT_DETAIL = "product/{productId}"
    const val CART = "cart"
    const val SETTINGS = "settings"

    fun productDetail(productId: String) = "product/$productId"
}

@Composable
fun KasiNavGraph(container: AppContainer) {
    val navController: NavHostController = rememberNavController()
    val factory = ViewModelFactory(container)

    NavHost(navController = navController, startDestination = Routes.LOGIN) {

        composable(Routes.LOGIN) {
            LoginScreen(
                viewModel = viewModel(factory = factory),
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                viewModel = viewModel(factory = factory),
                onRegisterSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                viewModel = viewModel(factory = factory),
                cartItemCount = container.cartRepository.items.value.sumOf { it.quantity },
                onProductClick = { productId -> navController.navigate(Routes.productDetail(productId)) },
                onCartClick = { navController.navigate(Routes.CART) },
                onSettingsClick = { navController.navigate(Routes.SETTINGS) }
            )
        }

        composable(
            route = Routes.PRODUCT_DETAIL,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: return@composable
            ProductDetailScreen(
                productId = productId,
                viewModel = viewModel(factory = factory),
                onNavigateBack = { navController.popBackStack() },
                onGoToCart = { navController.navigate(Routes.CART) }
            )
        }

        composable(Routes.CART) {
            CartScreen(
                viewModel = viewModel(factory = factory),
                onNavigateBack = { navController.popBackStack() },
                onOrderPlaced = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                viewModel = viewModel(factory = factory),
                onNavigateBack = { navController.popBackStack() },
                onLoggedOut = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

package com.mzenskprokat.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mzenskprokat.app.ui.screens.*
import com.mzenskprokat.app.ui.theme.MzenskProkatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MzenskProkatTheme {
                MainApp()
            }
        }
    }
}

// Навигационные маршруты
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Главная", Icons.Default.Home)
    object Catalog : Screen("catalog", "Каталог", Icons.AutoMirrored.Filled.List)
    object Contacts : Screen("contacts", "Контакты", Icons.Default.Phone)
    object Order : Screen("order", "Заказать", Icons.Default.ShoppingCart)
    object ProductDetail : Screen("product/{productId}", "Продукт", Icons.Default.Info) {
        fun createRoute(productId: String) = "product/$productId"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        Screen.Home,
        Screen.Catalog,
        Screen.Order,
        Screen.Contacts
    )

    // Определяем, показывать ли нижнюю панель
    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        topBar = {
            // Показываем заголовок только НЕ на главной странице
            if (currentRoute != Screen.Home.route) {
                TopAppBar(
                    title = {
                        Text(
                            text = when (currentRoute) {
                                Screen.Catalog.route -> "Каталог продукции"
                                Screen.Order.route -> "Корзина"
                                Screen.Contacts.route -> "Контакты"
                                else -> "Мценскпрокат"
                            }
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                if (currentRoute != screen.route) {
                                    navController.navigate(screen.route) {
                                        // Очищаем весь стек до Home
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = false
                                        }
                                        // Не сохраняем и не восстанавливаем состояние
                                        launchSingleTop = true
                                        restoreState = false
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavigationGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Composable
fun NavigationGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier // <-- Принимаем modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier // <-- Применяем modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToCatalog = {
                    navController.navigate(Screen.Catalog.route)
                },
                onNavigateToOrder = {
                    navController.navigate(Screen.Order.route)
                }
            )
        }

        composable(Screen.Catalog.route) {
            CatalogScreen(
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                }
            )
        }

        composable(Screen.Contacts.route) {
            ContactsScreen()
        }

        composable(Screen.Order.route) {
            OrderScreen()
        }

        composable(Screen.ProductDetail.route) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")
            if (productId != null) {
                ProductDetailScreen(
                    productId = productId,
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onOrderClick = {
                        navController.navigate(Screen.Order.route)
                    }
                )
            }
        }
    }
}
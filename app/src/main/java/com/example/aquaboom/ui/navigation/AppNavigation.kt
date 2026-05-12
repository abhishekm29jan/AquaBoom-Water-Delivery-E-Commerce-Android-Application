package com.example.aquaboom.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aquaboom.ui.screens.AddAddressScreen
import com.example.aquaboom.ui.screens.AdminBannerScreen
import com.example.aquaboom.ui.screens.AdminOtpScreen
import com.example.aquaboom.ui.screens.CartScreen
import com.example.aquaboom.ui.screens.HomeScreen
import com.example.aquaboom.ui.screens.LoginScreen
import com.example.aquaboom.ui.screens.OrderDetailsScreen
import com.example.aquaboom.ui.screens.OrderSuccessScreen
import com.example.aquaboom.ui.screens.OrderSummaryScreen
import com.example.aquaboom.ui.screens.OrdersScreen
import com.example.aquaboom.ui.screens.PersonalInformationScreen
import com.example.aquaboom.ui.screens.ProductScreen
import com.example.aquaboom.ui.screens.ProfileScreen
import com.example.aquaboom.ui.screens.RegisterScreen
import com.example.aquaboom.ui.screens.SavedAddressesScreen
import com.example.aquaboom.ui.screens.AdminOrdersScreen
import com.example.aquaboom.ui.screens.HelpSupportScreen
import com.example.aquaboom.ui.screens.SplashScreen
import com.example.aquaboom.ui.screens.TrackingScreen
import com.example.aquaboom.utils.Routes
import com.example.aquaboom.viewmodel.CartViewModel
import com.example.aquaboom.viewmodel.OrderViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val cartViewModel: CartViewModel = viewModel()
    val orderViewModel: OrderViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        composable("splash") {
            SplashScreen(navController)
        }

        composable("login") {
            LoginScreen(navController)
        }

        composable("admin") {
            AdminOrdersScreen(navController)
        }

        composable("banner_admin") {
            AdminBannerScreen(navController)
        }

        composable("admin_otp") {
            AdminOtpScreen(navController)
        }

        composable("register") {
            RegisterScreen(navController)
        }

        composable("home") {
            HomeScreen(navController, cartViewModel)
        }

        composable("product") {
            ProductScreen(navController, cartViewModel)
        }

        composable("cart") {
            CartScreen(navController, cartViewModel)
        }

        composable("orderSummary") {
            OrderSummaryScreen(navController, cartViewModel)
        }

        composable("orderSuccess") {
            OrderSuccessScreen(navController)
        }

        composable("myOrders") {
            OrdersScreen(navController, orderViewModel)
        }

        composable("orderDetails") {
            val order = orderViewModel.selectedOrder.value

            if (order != null) {
                OrderDetailsScreen(navController, order)
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Order not found",
                        color = Color.White
                    )
                }
            }
        }

        composable(
            route = "tracking/{orderId}"
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            TrackingScreen(
                navController = navController,
                orderId = orderId
            )
        }

        composable("profile") {
            ProfileScreen(navController)
        }

        composable("personalInfo") {
            PersonalInformationScreen(navController)
        }

        composable("addresses") {
            SavedAddressesScreen(navController)
        }

        composable("addAddress") {
            AddAddressScreen(navController)
        }

        composable("editAddress/{index}") { backStackEntry ->

            val index = backStackEntry.arguments
                ?.getString("index")
                ?.toInt() ?: 0

            AddAddressScreen(
                navController = navController,
                editIndex = index
            )
        }

        composable(Routes.HELP_SUPPORT) {
            HelpSupportScreen(
            )
        }
    }
}
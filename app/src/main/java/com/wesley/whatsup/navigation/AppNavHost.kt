package com.wesley.whatsup.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.wesley.whatsup.ui.screen.Home
import com.wesley.whatsup.ui.viewmodels.HomeViewModel

@Composable
fun AppNavHost(
  modifier: Modifier = Modifier,
  navController: NavHostController,
  startNavigation: String = NavigationItem.Home.route
) {
  NavHost(
    modifier = modifier,
    navController = navController,
    startDestination = startNavigation
  ) {
    composable(NavigationItem.Home.route) {
      val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
      Home(navController, homeViewModel)
    }
  }
}
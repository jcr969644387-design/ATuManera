package com.educalab.atumanera.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.educalab.atumanera.data.repository.CityRepository
import com.educalab.atumanera.domain.model.InfraCategory
import com.educalab.atumanera.ui.CityViewModel
import com.educalab.atumanera.ui.CityViewModelFactory
import com.educalab.atumanera.ui.CollectionViewModel
import com.educalab.atumanera.ui.CollectionViewModelFactory
import com.educalab.atumanera.ui.MissionsViewModel
import com.educalab.atumanera.ui.MissionsViewModelFactory
import com.educalab.atumanera.ui.screens.BuildScreen
import com.educalab.atumanera.ui.screens.HomeScreen
import com.educalab.atumanera.ui.screens.IndicatorsCollectionScreen
import com.educalab.atumanera.ui.screens.MissionsScreen
import com.educalab.atumanera.ui.screens.OnboardingScreen
import com.educalab.atumanera.ui.screens.ProfileScreen
import com.educalab.atumanera.ui.screens.SettingsScreen
import com.educalab.atumanera.util.AppPreferences

object Routes {
    const val ONBOARDING = "onboarding"
    const val PROFILE = "profile"
    const val HOME = "home"
    const val BUILD = "build/{category}"
    const val MISSIONS = "missions"
    const val INDICATORS = "indicators"
    const val SETTINGS = "settings"

    fun build(category: InfraCategory) = "build/${category.name}"
}

@Composable
fun AppNavGraph(
    repository: CityRepository,
    preferences: AppPreferences,
    navController: NavHostController = rememberNavController()
) {
    val cityViewModel: CityViewModel = viewModel(factory = CityViewModelFactory(repository))
    val missionsViewModel: MissionsViewModel = viewModel(factory = MissionsViewModelFactory(repository))
    val collectionViewModel: CollectionViewModel = viewModel(factory = CollectionViewModelFactory(repository))

    val startDestination = if (preferences.onboardingCompleted) Routes.HOME else Routes.ONBOARDING

    NavHost(navController = navController, startDestination = startDestination, modifier = Modifier) {
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onFinished = {
                    preferences.onboardingCompleted = true
                    navController.navigate(Routes.PROFILE) { popUpTo(Routes.ONBOARDING) { inclusive = true } }
                }
            )
        }
        composable(Routes.PROFILE) {
            ProfileScreen(
                viewModel = cityViewModel,
                onDone = { navController.navigate(Routes.HOME) { popUpTo(Routes.PROFILE) { inclusive = true } } }
            )
        }
        composable(Routes.HOME) {
            HomeScreen(
                cityViewModel = cityViewModel,
                missionsViewModel = missionsViewModel,
                onOpenBuild = { category -> navController.navigate(Routes.build(category)) },
                onOpenMissions = { navController.navigate(Routes.MISSIONS) },
                onOpenIndicators = { navController.navigate(Routes.INDICATORS) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }
        composable(
            route = Routes.BUILD,
            arguments = listOf(navArgument("category") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("category") ?: InfraCategory.ROAD.name
            BuildScreen(
                category = InfraCategory.valueOf(categoryName),
                viewModel = cityViewModel,
                preferences = preferences,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.MISSIONS) {
            MissionsScreen(viewModel = missionsViewModel, onBack = { navController.popBackStack() })
        }
        composable(Routes.INDICATORS) {
            IndicatorsCollectionScreen(
                cityViewModel = cityViewModel,
                collectionViewModel = collectionViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(preferences = preferences, onBack = { navController.popBackStack() })
        }
    }
}

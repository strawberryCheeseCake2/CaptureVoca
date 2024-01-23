package com.strawberryCodeCake.capturevoca.ui.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.strawberryCodeCake.capturevoca.ui.imageCropper.ImageCropper
import com.strawberryCodeCake.capturevoca.ui.imageCropper.ImageCropperDestination
import com.strawberryCodeCake.capturevoca.ui.pdf.PdfGenerator
import com.strawberryCodeCake.capturevoca.ui.pdf.PdfGeneratorDestination
import com.strawberryCodeCake.capturevoca.ui.setting.SettingsDestination
import com.strawberryCodeCake.capturevoca.ui.setting.SettingsScreen
import com.strawberryCodeCake.capturevoca.ui.voca.VocaEntryDestination
import com.strawberryCodeCake.capturevoca.ui.voca.VocaEntryScreen
import com.strawberryCodeCake.capturevoca.ui.voca.VocaListDestination
import com.strawberryCodeCake.capturevoca.ui.voca.VocaListScreen

/**
 * Provides Navigation graph for the application.
 */

@Composable
fun CaptureVocaNavHost(
    context: Context,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {

    NavHost(
        navController = navController,
        startDestination = VocaListDestination.route,
        modifier = modifier
    ) {
        composable(route = VocaListDestination.route) {
            VocaListScreen(
                navigateToVocaUpdate = { vocaId ->
                    navController.navigate("${VocaEntryDestination.route}/${vocaId}")
                },
                navigateToImageCropper = {
                    navController.navigate(ImageCropperDestination.route)
                },
                navigateToPdfGenerator = {
                    navController.navigate(PdfGeneratorDestination.route)
                },
                navigateToSettings = {
                    navController.navigate(SettingsDestination.route)
                }
            )
        }

        composable(
            route = VocaEntryDestination.routeWithArgs,
            arguments = listOf(navArgument(VocaEntryDestination.vocaIdArg) {
                type = NavType.IntType
            }) // pass argument to savedStateHandle automatically
        ) {
            VocaEntryScreen(navigateBack = {
                navController.navigateUp()
            })
        }

        composable(
            route = ImageCropperDestination.route
        ) {
            ImageCropper(
                navigateUp = {
                    navController.navigateUp()
                })
        }

        composable(
            route = PdfGeneratorDestination.route,
        ) {
            PdfGenerator(navigateUp = {
                navController.navigateUp()
            })
        }

        composable(
            route = SettingsDestination.route,
        ) {
            SettingsScreen(navigateUp = {
                navController.navigateUp()
            })
        }


    }

}
package com.strawberryCodeCake.capturevoca.ui.imageCropper

import android.graphics.ImageDecoder
import android.widget.Toast
import androidx.activity.compose.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.canhub.cropper.*
import com.strawberryCodeCake.capturevoca.R
import com.strawberryCodeCake.capturevoca.ui.navigation.NavigationDestination
import androidx.lifecycle.viewmodel.compose.viewModel
import com.strawberryCodeCake.capturevoca.ui.AppViewModelProvider
import com.strawberryCodeCake.capturevoca.ui.shared.components.PermissionDialog
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState


object ImageCropperDestination : NavigationDestination {
    override val route: String = "image_cropper"
    override val titleRes: Int = R.string.image_cropper_title
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ImageCropper(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ImageCropperViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val uiState = viewModel.uiState.collectAsState()

    val context = LocalContext.current

    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->

        if (result.isSuccessful) {
            // use the cropped image

            val resultUri = result.uriContent ?: run {
                navigateUp()
                return@rememberLauncherForActivityResult
            }

            val source = ImageDecoder.createSource(context.contentResolver, resultUri)
            val bitmap = ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                decoder.isMutableRequired = true
            }

            try {
                viewModel.addHighlightedVoca(context = context, bitmap) {
                    navigateUp()
                }
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }


        } else {
            // an error occurred cropping
            val exception = result.error
            navigateUp()
        }

    }



    fun launchImageCropper() {
        imageCropLauncher.launch(
            CropImageContractOptions(
                uri = null,
                cropImageOptions = CropImageOptions(
                    imageSourceIncludeGallery = false,
                )
            )
        )
    }


    LaunchedEffect(cameraPermissionState.status.isGranted) {
        if (cameraPermissionState.status.isGranted) {
            launchImageCropper()
        }
    }



    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        if (!cameraPermissionState.status.isGranted) {
            PermissionDialog(
                onDismissRequest = { navigateUp() },
                onConfirmation = {
                    cameraPermissionState.launchPermissionRequest()
                },
                dialogTitle = stringResource(R.string.camera_permission_title),
                dialogText = stringResource(R.string.camera_permission_text)
            )
        } else if (uiState.value.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

        }
    }


}

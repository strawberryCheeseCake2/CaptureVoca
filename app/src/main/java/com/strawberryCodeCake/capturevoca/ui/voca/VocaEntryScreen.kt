package com.strawberryCodeCake.capturevoca.ui.voca

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.strawberryCodeCake.capturevoca.CaptureVocaTopAppBar
import com.strawberryCodeCake.capturevoca.R
import com.strawberryCodeCake.capturevoca.ui.AppViewModelProvider
import com.strawberryCodeCake.capturevoca.ui.navigation.NavigationDestination
import com.strawberryCodeCake.capturevoca.ui.voca.components.VocaInputForm

object VocaEntryDestination : NavigationDestination {
    override val route = "voca_entry"
    override val titleRes = R.string.voca_entry_title
    const val vocaIdArg = "vocaId"
    val routeWithArgs = "$route/{$vocaIdArg}"
}

@Composable
fun VocaEntryScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VocaEntryViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState = viewModel.uiState.collectAsState()

    val context = LocalContext.current

    Scaffold(
        topBar = {
            CaptureVocaTopAppBar(
                title = stringResource(VocaEntryDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateBack,
                enableDone = true,
                donePressed = {
                    viewModel.saveVoca(completionHandler = navigateBack)
                }
            )
        },
        modifier = modifier

    ) { innerPadding ->
        VocaEntryBody(
            vocaEntryUiState = uiState.value,
            modifier = modifier
                .padding(innerPadding),
            onVocaValueChange = viewModel::updateUiState,
            onWordFocusLost = {
                try {
                    viewModel.loadDictionaryEntry {
                        Toast.makeText(
                            context,
                            context.getString(R.string.autocompletion_toast),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()

                }

            }
        )

    }
}

@Composable
fun VocaEntryBody(
    vocaEntryUiState: VocaEntryUiState,
    onVocaValueChange: (VocaDetails) -> Unit,
    onWordFocusLost: () -> Unit = {},
    modifier: Modifier = Modifier
) {

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(scrollState)
    ) {
        VocaInputForm(
            vocaDetails = vocaEntryUiState.vocaDetails,
            onValueChange = { newWord ->
                onVocaValueChange(newWord)
            },
            onWordFocusLost = {
                onWordFocusLost()
            })
    }
}
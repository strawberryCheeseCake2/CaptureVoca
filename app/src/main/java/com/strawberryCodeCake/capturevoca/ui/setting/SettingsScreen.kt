package com.strawberryCodeCake.capturevoca.ui.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.strawberryCodeCake.capturevoca.CaptureVocaTopAppBar
import com.strawberryCodeCake.capturevoca.R
import com.strawberryCodeCake.capturevoca.ui.navigation.NavigationDestination
import androidx.lifecycle.viewmodel.compose.viewModel
import com.strawberryCodeCake.capturevoca.ui.AppViewModelProvider

object SettingsDestination : NavigationDestination {
    override val route: String = "settings"
    override val titleRes: Int = R.string.settings_title
}

@Composable
fun SettingsScreen(
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CaptureVocaTopAppBar(
                title = stringResource(SettingsDestination.titleRes),
                canNavigateBack = true,
                navigateUp = navigateUp,
            )
        },
        modifier = modifier

    ) { innerPadding ->
        SettingsBody(modifier.padding(innerPadding), uiState.translateMeaning ,setTranslateToKr = {
            viewModel.updateTranslateMeaningSetting(translateMeaningSetting = it)
        })
    }
}

@Composable
fun SettingsBody(modifier: Modifier = Modifier, shouldTranslateToKr: Boolean, setTranslateToKr: (Boolean) -> Unit) {


    val context = LocalContext.current

    Column(
        modifier = modifier.padding(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "단어 의미 한국어로 번역하기", fontSize = 16.sp)
            Switch(
                checked = shouldTranslateToKr,
                onCheckedChange = {
                    setTranslateToKr(it)
                }
            ) // Switch END
        }


    }

}
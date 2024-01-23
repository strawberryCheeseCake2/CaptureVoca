package com.strawberryCodeCake.capturevoca

import com.strawberryCodeCake.capturevoca.R
import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.strawberryCodeCake.capturevoca.ui.navigation.CaptureVocaNavHost

/**
 * Top level composable that represents screens for the application.
 */

@Composable
fun CaptureVocaApp(context: Context, navController: NavHostController = rememberNavController()) {
    CaptureVocaNavHost(context = context, navController = navController)

}

/**
 * App bar to display title and conditionally display the back navigation.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaptureVocaTopAppBar(
    title: String,
    fontWeight: FontWeight = FontWeight.Normal,
    textStyle: TextStyle? = null,
    canNavigateBack: Boolean,
    enableDone: Boolean = false,
    enableExtraOptions: Boolean = false,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit = {},
    donePressed: () -> Unit = {},
    extraOptions: List<@Composable () -> Unit> = emptyList(),
) {
    var isExpanded by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                style = textStyle ?: TextStyle.Default,
                fontWeight = fontWeight,
                fontSize = 20.sp,
            )
        },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)

                    )
                }
            }
        },

        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
        actions = {
            if (enableDone) {
                IconButton(onClick = donePressed) {
                    Text(stringResource(id = R.string.done_button))
                }
            }

            if (enableExtraOptions) {
                IconButton(onClick = {
                    isExpanded = true
                }) {
                    Icon(imageVector = Icons.Outlined.MoreVert, contentDescription = null)
                }

                DropdownMenu(expanded = isExpanded, onDismissRequest = { isExpanded = false }) {
                    extraOptions.forEach {
                        it()
                    }
                }
            }


        },

    ) // CenterAlignedTopAppBar END
}


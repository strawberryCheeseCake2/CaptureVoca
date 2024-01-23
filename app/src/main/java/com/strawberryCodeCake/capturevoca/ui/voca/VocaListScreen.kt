package com.strawberryCodeCake.capturevoca.ui.voca

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.strawberryCodeCake.capturevoca.CaptureVocaTopAppBar
import com.strawberryCodeCake.capturevoca.R
import com.strawberryCodeCake.capturevoca.data.room.voca.model.Voca
import com.strawberryCodeCake.capturevoca.ui.shared.components.VocaCard
import com.strawberryCodeCake.capturevoca.ui.AppViewModelProvider
import com.strawberryCodeCake.capturevoca.ui.navigation.NavigationDestination
import com.strawberryCodeCake.capturevoca.ui.shared.components.VerticallyExpandableFAB
import com.strawberryCodeCake.capturevoca.ui.theme.Typography


object VocaListDestination : NavigationDestination {
    override val route: String = "voca_list"
    override val titleRes: Int = R.string.voca_list_title
}

@Composable
fun VocaListScreen(
    navigateToVocaUpdate: (Int) -> Unit,
    navigateToImageCropper: () -> Unit,
    navigateToPdfGenerator: () -> Unit,
    navigateToSettings: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VocaListViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {

    val context = LocalContext.current

    val vocaListUiState by viewModel.vocaListUiState.collectAsState()

    // Triggers a recomposition on a vocaListUiState change
    LaunchedEffect(vocaListUiState) { }

    Scaffold(
        topBar = {
            CaptureVocaTopAppBar(
                title = stringResource(VocaListDestination.titleRes),
                fontWeight = FontWeight.SemiBold,
                textStyle = Typography.displayMedium,
                canNavigateBack = false,
                navigateUp = { },
                enableDone = false,
                enableExtraOptions = true,
                extraOptions = listOf {
                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.generate_pdf_dropdown_item)) },
                        onClick = {
                            if (vocaListUiState.vocaList.isNotEmpty()) {
                                navigateToPdfGenerator()
                            } else {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.empty_voca_list_message),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )

                    DropdownMenuItem(
                        text = { Text(text = stringResource(id = R.string.settings_dropdown_item)) },
                        onClick = {
                            navigateToSettings()
                        }
                    )
                }
            )
        },
        floatingActionButton = {
            VerticallyExpandableFAB(
                onClick1 = navigateToImageCropper,
                onClick2 = {
                    navigateToVocaUpdate(0)
                }, fabIcon = Icons.Default.Add
            )
        },
    ) { innerPadding ->
        if (vocaListUiState.vocaList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(64.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.empty_voca_list_message), textAlign = TextAlign.Center)
            }
        } else {
            VocaListBody(
                vocaList = vocaListUiState.vocaList,
                onItemClick = navigateToVocaUpdate,
                onSwipeItem = { id ->
                    viewModel.deleteVoca(id)
                },
                modifier = Modifier
                    .padding(innerPadding)
            )
        }

    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocaListBody(
    vocaList: List<Voca>,
    onItemClick: (Int) -> Unit,
    onSwipeItem: (Int) -> Unit,
    modifier: Modifier = Modifier
) {


    LazyColumn(
        modifier = modifier,
//            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(20.dp)
    ) {
        items(items = vocaList, key = { it.id }) { voca ->

            val dismissState = rememberDismissState(
                confirmValueChange = {
                    if (it == DismissValue.DismissedToStart) {
                        onSwipeItem(voca.id)
                        return@rememberDismissState true
                    }

                    false
                },
                positionalThreshold = { density ->
                    density * 0.65f
                }
            )

            SwipeToDismiss(
                state = dismissState,
                background = {
                    val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
                    val color by animateColorAsState(
                        when (dismissState.targetValue) {
                            DismissValue.Default -> Color.LightGray
                            DismissValue.DismissedToEnd -> Color.Green
                            DismissValue.DismissedToStart -> Color.Red
                        }, label = "swipe_color"
                    )
                    val alignment = when (direction) {
                        DismissDirection.StartToEnd -> Alignment.CenterStart
                        DismissDirection.EndToStart -> Alignment.CenterEnd
                    }
                    val icon = when (direction) {
                        DismissDirection.StartToEnd -> Icons.Default.Done
                        DismissDirection.EndToStart -> Icons.Default.Delete
                    }
                    val scale by animateFloatAsState(
                        if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f,
                        label = "swipe_scale"
                    )

                    Box(
                        Modifier
                            .fillMaxSize()
                            .background(color, shape = RoundedCornerShape(16.dp))
                            .padding(horizontal = 20.dp),

                        contentAlignment = alignment,
                    ) {
                        Icon(
                            icon,
                            contentDescription = "Localized description",
                            modifier = Modifier.scale(scale)
                        )
                    }
                },
                dismissContent = {
                    VocaCard(voca = voca, onItemClick = onItemClick)
                },

                directions = setOf(DismissDirection.EndToStart)


            ) // SwipeToDismiss END

        }
    }
}
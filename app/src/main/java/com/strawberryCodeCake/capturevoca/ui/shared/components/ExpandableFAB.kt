package com.strawberryCodeCake.capturevoca.ui.shared.components

import com.strawberryCodeCake.capturevoca.R
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Custom fab that allows for displaying extended content
@Composable
fun VerticallyExpandableFAB(
    onClick1: () -> Unit,
    onClick2: () -> Unit,
    fabIcon: ImageVector
) {

    val expandable = true

    var isExpanded by remember { mutableStateOf(false) }
    if (!expandable) { // Close the expanded fab if you change to non expandable nav destination
        isExpanded = false
    }

    val fabSize = 64.dp
    val expandedFabWidth by animateDpAsState(
        targetValue = if (isExpanded) 200.dp else fabSize,
        animationSpec = spring(dampingRatio = 1f), label = ""
    )
    val expandedFabHeight by animateDpAsState(
        targetValue = if (isExpanded) 58.dp else fabSize,
        animationSpec = spring(dampingRatio = 1f), label = ""
    )

    fun setIsExpanded(to: Boolean) {
        isExpanded = to
    }

    Column {

        // Expanded Surface over the FAB
        Surface(
            shadowElevation = 6.dp,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .offset(y = (27).dp)
                .size(
                    width = expandedFabWidth,
                    height = (animateDpAsState(
                        if (isExpanded) 144.dp else 0.dp,
                        animationSpec = spring(dampingRatio = 1.2f), label = ""
                    )).value
                ),
            color = MaterialTheme.colorScheme.background,

        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(0.dp, 0.dp, 0.dp, 27.dp)
            ) {

                SubFAB(
                    onClick = {
                        onClick1()
                        setIsExpanded(false)
                    },
                    iconVector = Icons.Outlined.CameraAlt,
                    title = stringResource(id = R.string.take_a_photo_fab_item)
                )
                SubFAB(
                    onClick = {
                        onClick2()
                        setIsExpanded(false)
                    },
                    iconVector = Icons.Default.Edit,
                    title = stringResource(id = R.string.add_manually_fab_item)
                )

            }
        }

        MainFAB(
            isExpanded = isExpanded,
            setIsExpanded = ::setIsExpanded,
            expandedWidth = expandedFabWidth,
            expandedHeight = expandedFabHeight
        )
    }
}

@Composable
private fun MainFAB(
    isExpanded: Boolean,
    setIsExpanded: (to: Boolean) -> Unit,
    expandedWidth: Dp,
    expandedHeight: Dp,
    expandedIconVector: ImageVector = Icons.Rounded.Close,
    defaultIconVector: ImageVector = Icons.Default.Add
) {
    FloatingActionButton(
        onClick = {
            setIsExpanded(!isExpanded)
        },
        modifier = Modifier
            .width(expandedWidth)
            .height(expandedHeight),
        shape = RoundedCornerShape(18.dp),
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 6.dp
        ),
        containerColor = MaterialTheme.colorScheme.tertiary,
        contentColor = MaterialTheme.colorScheme.inverseSurface
    ) {

        Icon(
            imageVector = if (isExpanded) {
                expandedIconVector
            } else {
                defaultIconVector
            },
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .offset(
                    x = animateDpAsState(
                        if (isExpanded) (-70).dp else 0.dp,
                        animationSpec = spring(dampingRatio = 1f), label = ""
                    ).value
                )
        )

        Text(
            text = stringResource(id = R.string.close_fab_item),
            softWrap = false,
            modifier = Modifier
                .offset(
                    x = animateDpAsState(
                        if (isExpanded) 10.dp else 50.dp,
                        animationSpec = spring(dampingRatio = 1f), label = ""
                    ).value
                )
                .alpha(
                    animateFloatAsState(
                        targetValue = if (isExpanded) 1f else 0f,
                        animationSpec = tween(
                            durationMillis = if (isExpanded) 350 else 100,
                            delayMillis = if (isExpanded) 100 else 0,
                            easing = EaseIn
                        ), label = ""
                    ).value
                )
        )

    }
}

@Composable
private fun SubFAB(onClick: () -> Unit, iconVector: ImageVector, title: String) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        containerColor = Color.Transparent,
        elevation = FloatingActionButtonDefaults.elevation(0.dp, 0.dp),
    ) {
        Icon(
            imageVector = iconVector,
            contentDescription = null,
            modifier = Modifier.offset(x = (-70).dp)
        )
        Text(modifier = Modifier.offset(x = 10.dp), text = title)
    }
}
package com.example.androiddesign.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddesign.model.ComponentType
import com.example.androiddesign.model.UiElement
import com.example.androiddesign.ui.theme.AndroidDesignTheme
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun MockupCanvas(
    elements: List<UiElement>,
    selectedElementId: String?,
    isGridVisible: Boolean,
    isLandscape: Boolean = false,
    onSelectElement: (String?) -> Unit,
    onUpdateElementPosition: (String, Float, Float) -> Unit,
    onUpdateElementSize: (String, Int, Int) -> Unit,
    onDeleteElement: (String) -> Unit,
    onDuplicateElement: (String) -> Unit,
    onEditElement: (String) -> Unit,
    onToggleLock: (String, Boolean) -> Unit = { _, _ -> },
    onDragStateChange: (Boolean) -> Unit = {},
    graphicsLayer: GraphicsLayer? = null,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)

    // Outer workspace background
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onSelectElement(null)
            },
        contentAlignment = Alignment.Center
    ) {
        // Phone Device Outer Shell (Simulating smartphone screen frame with rounded corners & camera notch)
        Surface(
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            shadowElevation = 12.dp,
            modifier = Modifier
                .padding(16.dp)
                // Adapt the outer shell ratio based on landscape or portrait
                .fillMaxWidth(if (isLandscape) 0.98f else 0.95f)
                .fillMaxHeight(if (isLandscape) 0.92f else 0.96f)
                .border(
                    width = 6.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(32.dp)
                )
                .clip(RoundedCornerShape(32.dp))
                .then(
                    if (graphicsLayer != null) {
                        Modifier.drawWithContent {
                            graphicsLayer.record {
                                this@drawWithContent.drawContent()
                            }
                            drawLayer(graphicsLayer)
                        }
                    } else Modifier
                )
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onSelectElement(null)
                    }
            ) {
                val maxWidthDp = maxWidth
                val maxHeightDp = maxHeight

                // Top Smartphone Camera Punch-hole Notch (Adjusts position based on orientation)
                if (isLandscape) {
                    // Left side notch for landscape mode
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 8.dp)
                            .width(18.dp)
                            .height(70.dp)
                            .background(
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(10.dp)
                            )
                    )
                } else {
                    // Top side notch for portrait mode
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 8.dp)
                            .width(70.dp)
                            .height(18.dp)
                            .background(
                                color = MaterialTheme.colorScheme.outline,
                                shape = RoundedCornerShape(10.dp)
                            )
                    )
                }

                // Grid lines overlay
                if (isGridVisible) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val gridSpacingPx = with(density) { 20.dp.toPx() }
                        val widthPx = size.width
                        val heightPx = size.height

                        var x = gridSpacingPx
                        while (x < widthPx) {
                            drawLine(
                                color = gridColor,
                                start = Offset(x, 0f),
                                end = Offset(x, heightPx),
                                strokeWidth = 1f
                            )
                            x += gridSpacingPx
                        }

                        var y = gridSpacingPx
                        while (y < heightPx) {
                            drawLine(
                                color = gridColor,
                                start = Offset(0f, y),
                                end = Offset(widthPx, y),
                                strokeWidth = 1f
                            )
                            y += gridSpacingPx
                        }
                    }
                }

                // Render components on canvas. PRO OPTIMIZATION: List is pre-sorted by Z-order from ViewModel.
                elements.forEach { element ->
                    val isSelected = element.id == selectedElementId

                    // Bound constraints so element doesn't drag outside
                    val elementXDp = element.xDp.coerceIn(0f, max(0f, maxWidthDp.value - element.widthDp))
                    val elementYDp = element.yDp.coerceIn(0f, max(0f, maxHeightDp.value - element.heightDp))

                    // Local drag accumulator for smooth, stutter-free movement
                    var dragAccumX by remember(element.id, element.xDp) { mutableFloatStateOf(elementXDp) }
                    var dragAccumY by remember(element.id, element.yDp) { mutableFloatStateOf(elementYDp) }

                    Box(
                        modifier = Modifier
                            .offset {
                                IntOffset(
                                    with(density) { elementXDp.dp.roundToPx() },
                                    with(density) { elementYDp.dp.roundToPx() }
                                )
                            }
                            .size(element.widthDp.dp, element.heightDp.dp)
                            .then(
                                if (isSelected) {
                                    Modifier.border(
                                        width = 2.dp,
                                        color = if (element.isLocked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                } else Modifier
                            )
                            .then(
                                if (!element.isLocked) {
                                    Modifier.pointerInput(element.id) {
                                        detectDragGestures(
                                            onDragStart = {
                                                onSelectElement(element.id)
                                                dragAccumX = elementXDp
                                                dragAccumY = elementYDp
                                                onDragStateChange(true)
                                            },
                                            onDragEnd = { onDragStateChange(false) },
                                            onDragCancel = { onDragStateChange(false) },
                                            onDrag = { change, dragAmount ->
                                                change.consume()
                                                val deltaDpX = with(density) { dragAmount.x.toDp().value }
                                                val deltaDpY = with(density) { dragAmount.y.toDp().value }

                                                dragAccumX += deltaDpX
                                                dragAccumY += deltaDpY

                                                onUpdateElementPosition(
                                                    element.id,
                                                    dragAccumX,
                                                    dragAccumY
                                                )
                                            }
                                        )
                                    }
                                } else Modifier
                            )
                            .clickable {
                                onSelectElement(element.id)
                            }
                    ) {
                        // Render element visual mockup
                        RenderElementMockup(
                            element = element,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Render Selection Controls, Drag Anchor Handle, and Resize Handle
                        if (isSelected) {
                            // Top Quick Action Bar
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                tonalElevation = 6.dp,
                                shadowElevation = 6.dp,
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .offset(y = (-44).dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    // Lock / Unlock Button
                                    IconButton(
                                        onClick = { onToggleLock(element.id, !element.isLocked) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (element.isLocked) Icons.Default.LockOpen else Icons.Default.Lock,
                                            contentDescription = if (element.isLocked) "Déverrouiller" else "Verrouiller",
                                            tint = if (element.isLocked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = { onEditElement(element.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Éditer",
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { onDuplicateElement(element.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Dupliquer",
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { onDeleteElement(element.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Supprimer",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            // DEDICATED DRAG ANCHOR HANDLE (Ancre de déplacement ✥) at Top-Left (ONLY WHEN UNLOCKED)
                            if (!element.isLocked) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .offset(x = (-14).dp, y = (-14).dp)
                                        .size(32.dp)
                                        .shadow(6.dp, CircleShape)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                        .border(2.dp, Color.White, CircleShape)
                                        .pointerInput(element.id) {
                                            detectDragGestures(
                                                onDragStart = {
                                                    onSelectElement(element.id)
                                                    dragAccumX = elementXDp
                                                    dragAccumY = elementYDp
                                                    onDragStateChange(true)
                                                },
                                                onDragEnd = { onDragStateChange(false) },
                                                onDragCancel = { onDragStateChange(false) },
                                                onDrag = { change, dragAmount ->
                                                    change.consume()
                                                    val deltaDpX = with(density) { dragAmount.x.toDp().value }
                                                    val deltaDpY = with(density) { dragAmount.y.toDp().value }

                                                    dragAccumX += deltaDpX
                                                    dragAccumY += deltaDpY

                                                    onUpdateElementPosition(
                                                        element.id,
                                                        dragAccumX,
                                                        dragAccumY
                                                    )
                                                }
                                            )
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.OpenWith,
                                        contentDescription = "Ancre de déplacement",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            // CORNER RESIZE HANDLE (↘) at Bottom-Right (ONLY WHEN UNLOCKED)
                            if (!element.isLocked) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .offset(x = 10.dp, y = 10.dp)
                                        .size(28.dp)
                                        .shadow(4.dp, CircleShape)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.secondary)
                                        .border(2.dp, Color.White, CircleShape)
                                        .pointerInput(element.id) {
                                            detectDragGestures(
                                                onDragStart = { onDragStateChange(true) },
                                                onDragEnd = { onDragStateChange(false) },
                                                onDragCancel = { onDragStateChange(false) },
                                                onDrag = { change, dragAmount ->
                                                    change.consume()
                                                    val dxDp = with(density) { dragAmount.x.toDp().value }
                                                    val dyDp = with(density) { dragAmount.y.toDp().value }

                                                    val newWidth = max(40, (element.widthDp + dxDp).roundToInt())
                                                    val newHeight = max(20, (element.heightDp + dyDp).roundToInt())

                                                    onUpdateElementSize(element.id, newWidth, newHeight)
                                                }
                                            )
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "↘",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MockupCanvasPreview() {
    AndroidDesignTheme {
        val sampleElements = listOf(
            UiElement(
                id = "1",
                type = ComponentType.TOP_APP_BAR,
                label = "Titre Application",
                xDp = 10f,
                yDp = 30f,
                widthDp = 280,
                heightDp = 50
            ),
            UiElement(
                id = "2",
                type = ComponentType.TEXT_INPUT,
                label = "Nom d'utilisateur",
                xDp = 20f,
                yDp = 100f,
                widthDp = 260,
                heightDp = 60
            ),
            UiElement(
                id = "3",
                type = ComponentType.BUTTON,
                label = "Se connecter",
                xDp = 20f,
                yDp = 180f,
                widthDp = 260,
                heightDp = 50
            )
        )
        MockupCanvas(
            elements = sampleElements,
            selectedElementId = "2",
            isGridVisible = true,
            isLandscape = false,
            onSelectElement = {},
            onUpdateElementPosition = { _, _, _ -> },
            onUpdateElementSize = { _, _, _ -> },
            onDeleteElement = {},
            onDuplicateElement = {},
            onEditElement = {}
        )
    }
}

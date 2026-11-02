package com.example.androiddesign.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.androiddesign.model.ComponentType
import com.example.androiddesign.model.UiElement
import com.example.androiddesign.ui.theme.AndroidDesignTheme

@Composable
fun RenderElementMockup(
    element: UiElement,
    modifier: Modifier = Modifier
) {
    // PRO ARCHITECTURE: Render non-functional visual mockups using static, non-interactive Surfaces/Boxes.
    // This prevents Material widgets (Button, Checkbox, Switch) from consuming click pointer events,
    // ensuring the canvas element remains 100% clickable and selectable even when locked!
    Box(modifier = modifier) {
        when (element.type) {
            ComponentType.TEXT_LABEL -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (element.showBorder) {
                                Modifier
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.outline,
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            } else Modifier
                        ),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = element.label,
                        fontSize = element.fontSizeSp.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            ComponentType.BUTTON -> {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = element.label,
                            fontSize = element.fontSizeSp.sp,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            ComponentType.TEXT_INPUT -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = if (element.showBorder) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceContainerLow,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .then(
                            if (element.showBorder) {
                                Modifier.border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(4.dp)
                                )
                            } else Modifier
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Column {
                        Text(
                            text = element.label,
                            fontSize = (element.fontSizeSp * 0.75f).coerceAtLeast(10f).sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Entrer le texte...",
                            fontSize = element.fontSizeSp.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            ComponentType.CHECKBOX -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✓", color = MaterialTheme.colorScheme.onPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = element.label,
                        fontSize = element.fontSizeSp.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            ComponentType.SWITCH -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = element.label,
                        fontSize = element.fontSizeSp.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Box(
                        modifier = Modifier
                            .width(44.dp)
                            .height(24.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .padding(2.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(MaterialTheme.colorScheme.onPrimary, CircleShape)
                        )
                    }
                }
            }

            ComponentType.IMAGE_PLACEHOLDER -> {
                if (!element.imageUri.isNullOrEmpty()) {
                    AsyncImage(
                        model = element.imageUri,
                        contentDescription = element.label,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp))
                            .then(
                                if (element.showBorder) {
                                    Modifier.border(
                                        width = 1.5.dp,
                                        color = MaterialTheme.colorScheme.outline,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                } else Modifier
                            )
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .then(
                                if (element.showBorder) {
                                    Modifier.border(
                                        width = 1.5.dp,
                                        color = MaterialTheme.colorScheme.outline,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                } else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(28.dp)
                            )
                            Text(
                                text = element.label,
                                fontSize = element.fontSizeSp.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }

            ComponentType.CARD_CONTAINER -> {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                    ),
                    border = if (element.showBorder) BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline) else null,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = element.label,
                            fontSize = element.fontSizeSp.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            ComponentType.DIVIDER -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    HorizontalDivider(
                        thickness = 2.dp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            ComponentType.TOP_APP_BAR -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = element.label,
                                fontSize = element.fontSizeSp.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            ComponentType.MENU_HAMBURGER -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .then(
                            if (element.showBorder) {
                                Modifier.border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(8.dp)
                                )
                            } else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = element.label,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ElementRendererPreview() {
    AndroidDesignTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RenderElementMockup(
                element = UiElement(
                    type = ComponentType.TEXT_LABEL,
                    label = "Label Texte avec Bordure",
                    xDp = 0f,
                    yDp = 0f,
                    widthDp = 200,
                    heightDp = 36,
                    showBorder = true
                ),
                modifier = Modifier.height(36.dp)
            )

            RenderElementMockup(
                element = UiElement(
                    type = ComponentType.BUTTON,
                    label = "Bouton Valider",
                    xDp = 0f,
                    yDp = 0f,
                    widthDp = 180,
                    heightDp = 48,
                    showBorder = true
                ),
                modifier = Modifier.height(48.dp)
            )

            RenderElementMockup(
                element = UiElement(
                    type = ComponentType.MENU_HAMBURGER,
                    label = "Menu Principal",
                    xDp = 0f,
                    yDp = 0f,
                    widthDp = 48,
                    heightDp = 48,
                    showBorder = true
                ),
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

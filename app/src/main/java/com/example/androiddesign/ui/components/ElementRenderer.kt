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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
                Button(
                    onClick = { /* Non-functional mockup */ },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = element.label,
                        fontSize = element.fontSizeSp.sp
                    )
                }
            }

            ComponentType.TEXT_INPUT -> {
                if (element.showBorder) {
                    OutlinedTextField(
                        value = "",
                        onValueChange = { /* Non-functional mockup */ },
                        label = { Text(element.label) },
                        placeholder = { Text("Entrer le texte...") },
                        readOnly = true,
                        enabled = false,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    TextField(
                        value = "",
                        onValueChange = { /* Non-functional mockup */ },
                        label = { Text(element.label) },
                        placeholder = { Text("Entrer le texte...") },
                        readOnly = true,
                        enabled = false,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        ),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            ComponentType.CHECKBOX -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Checkbox(
                        checked = true,
                        onCheckedChange = null,
                        enabled = false
                    )
                    Text(
                        text = element.label,
                        fontSize = element.fontSizeSp.sp,
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
                        .padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = element.label,
                        fontSize = element.fontSizeSp.sp
                    )
                    Switch(
                        checked = true,
                        onCheckedChange = null,
                        enabled = false
                    )
                }
            }

            ComponentType.IMAGE_PLACEHOLDER -> {
                // PRO OPTIMIZATION: Using Coil AsyncImage for fast, memory-safe, non-blocking disk I/O
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

            RenderElementMockup(
                element = UiElement(
                    type = ComponentType.CARD_CONTAINER,
                    label = "Carte avec Bordure Visible",
                    xDp = 0f,
                    yDp = 0f,
                    widthDp = 240,
                    heightDp = 80,
                    showBorder = true
                ),
                modifier = Modifier.height(80.dp)
            )
        }
    }
}

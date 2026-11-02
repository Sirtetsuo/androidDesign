package com.example.androiddesign.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FlipToBack
import androidx.compose.material.icons.filled.FlipToFront
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddesign.model.ComponentType
import com.example.androiddesign.model.UiElement
import com.example.androiddesign.ui.theme.AndroidDesignTheme

@Composable
fun PropertyInspector(
    element: UiElement,
    isLandscape: Boolean = false,
    onUpdateLabel: (String) -> Unit,
    onUpdateWidth: (Int) -> Unit,
    onUpdateHeight: (Int) -> Unit,
    onUpdateFontSize: (Int) -> Unit,
    onUpdateShowBorder: (Boolean) -> Unit,
    onUnlock: () -> Unit = {},
    onBringForward: () -> Unit,
    onSendBackward: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = if (isLandscape) {
            RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp)
        } else {
            RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        },
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .then(if (isLandscape) Modifier.fillMaxHeight() else Modifier)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Propriétés du composant",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${element.type.displayName} • Verrouillé 🔒",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row {
                    // UNLOCK BUTTON 🔓
                    IconButton(onClick = onUnlock, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.LockOpen,
                            contentDescription = "Déverrouiller pour déplacer",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onDuplicate, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Dupliquer",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Supprimer",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = onClose, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fermer",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.size(12.dp))

            // Label Input and Border Toggle Checkbox
            if (isLandscape) {
                OutlinedTextField(
                    value = element.label,
                    onValueChange = onUpdateLabel,
                    label = { Text("Texte / Intitulé") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onUpdateShowBorder(!element.showBorder) }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = element.showBorder,
                        onCheckedChange = onUpdateShowBorder
                    )
                    Text(
                        text = "Afficher la bordure",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = element.label,
                        onValueChange = onUpdateLabel,
                        label = { Text("Texte / Intitulé") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )

                    // Show Border Toggle Option
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { onUpdateShowBorder(!element.showBorder) }
                            .padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = element.showBorder,
                            onCheckedChange = onUpdateShowBorder
                        )
                        Text(
                            text = "Bordure",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.size(12.dp))

            // Width & Height Sliders
            if (isLandscape) {
                PropertySlider(
                    label = "Largeur: ${element.widthDp} dp",
                    value = element.widthDp.toFloat(),
                    valueRange = 40f..1200f,
                    onValueChange = onUpdateWidth
                )
                Spacer(modifier = Modifier.size(8.dp))
                PropertySlider(
                    label = "Hauteur: ${element.heightDp} dp",
                    value = element.heightDp.toFloat(),
                    valueRange = 20f..1200f,
                    onValueChange = onUpdateHeight
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PropertySlider(
                        label = "Largeur: ${element.widthDp} dp",
                        value = element.widthDp.toFloat(),
                        valueRange = 40f..1200f,
                        onValueChange = onUpdateWidth,
                        modifier = Modifier.weight(1f)
                    )
                    PropertySlider(
                        label = "Hauteur: ${element.heightDp} dp",
                        value = element.heightDp.toFloat(),
                        valueRange = 20f..1200f,
                        onValueChange = onUpdateHeight,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Font Size Slider & Z-Index
            if (isLandscape) {
                Spacer(modifier = Modifier.size(8.dp))
                PropertySlider(
                    label = "Taille texte: ${element.fontSizeSp} sp",
                    value = element.fontSizeSp.toFloat(),
                    valueRange = 10f..64f,
                    onValueChange = onUpdateFontSize
                )
                Spacer(modifier = Modifier.size(12.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = onSendBackward,
                        contentPadding = ButtonDefaults.ContentPadding,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.FlipToBack, contentDescription = "Arrière-plan", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.size(4.dp))
                        Text("Reculer")
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    OutlinedButton(
                        onClick = onBringForward,
                        contentPadding = ButtonDefaults.ContentPadding,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.FlipToFront, contentDescription = "Premier plan", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.size(4.dp))
                        Text("Avancer")
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PropertySlider(
                        label = "Taille texte: ${element.fontSizeSp} sp",
                        value = element.fontSizeSp.toFloat(),
                        valueRange = 10f..64f,
                        onValueChange = onUpdateFontSize,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.size(12.dp))

                    // Z-index Order Controls
                    Row {
                        OutlinedButton(
                            onClick = onSendBackward,
                            contentPadding = ButtonDefaults.ContentPadding
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlipToBack,
                                contentDescription = "Arrière-plan",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.size(8.dp))
                        OutlinedButton(
                            onClick = onBringForward,
                            contentPadding = ButtonDefaults.ContentPadding
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlipToFront,
                                contentDescription = "Premier plan",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PropertySlider(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Slider(
            value = value,
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = valueRange
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PropertyInspectorPreview() {
    AndroidDesignTheme {
        PropertyInspector(
            element = UiElement(
                type = ComponentType.BUTTON,
                label = "Valider ma commande",
                xDp = 0f,
                yDp = 0f,
                widthDp = 200,
                heightDp = 50,
                fontSizeSp = 16,
                showBorder = true,
                isLocked = true
            ),
            isLandscape = false,
            onUpdateLabel = {},
            onUpdateWidth = {},
            onUpdateHeight = {},
            onUpdateFontSize = {},
            onUpdateShowBorder = {},
            onUnlock = {},
            onBringForward = {},
            onSendBackward = {},
            onDuplicate = {},
            onDelete = {},
            onClose = {}
        )
    }
}

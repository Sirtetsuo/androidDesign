package com.example.androiddesign.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CropSquare
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.SmartButton
import androidx.compose.material.icons.filled.Title
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material.icons.filled.WebAsset
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddesign.model.ComponentType
import com.example.androiddesign.ui.theme.AndroidDesignTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddComponentSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSelectComponent: (ComponentType) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        AddComponentSheetContent(
            onSelectComponent = {
                onSelectComponent(it)
                onDismiss()
            }
        )
    }
}

@Composable
fun AddComponentSheetContent(
    onSelectComponent: (ComponentType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Ajouter un composant UI",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Sélectionnez un composant à placer sur la page de travail",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(ComponentType.entries.toTypedArray()) { type ->
                ComponentTypeCard(
                    type = type,
                    onClick = {
                        onSelectComponent(type)
                    }
                )
            }
        }
    }
}

@Composable
fun ComponentTypeCard(
    type: ComponentType,
    onClick: () -> Unit
) {
    val icon: ImageVector = when (type) {
        ComponentType.TEXT_LABEL -> Icons.Default.Title
        ComponentType.BUTTON -> Icons.Default.SmartButton
        ComponentType.TEXT_INPUT -> Icons.Default.Edit
        ComponentType.CHECKBOX -> Icons.Default.CheckBox
        ComponentType.SWITCH -> Icons.Default.ToggleOn
        ComponentType.IMAGE_PLACEHOLDER -> Icons.Default.Image
        ComponentType.CARD_CONTAINER -> Icons.Default.CropSquare
        ComponentType.DIVIDER -> Icons.Default.HorizontalRule
        ComponentType.TOP_APP_BAR -> Icons.Default.WebAsset
        ComponentType.MENU_HAMBURGER -> Icons.Default.Menu
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.size(12.dp))
            Text(
                text = type.displayName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddComponentSheetPreview() {
    AndroidDesignTheme {
        Surface {
            AddComponentSheetContent(onSelectComponent = {})
        }
    }
}

package com.example.androiddesign.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderZip
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddesign.model.MockupDesign
import com.example.androiddesign.ui.theme.AndroidDesignTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedDesignsSheet(
    sheetState: SheetState,
    savedDesigns: List<MockupDesign>,
    currentDesignId: String,
    onDismiss: () -> Unit,
    onSelectDesign: (MockupDesign) -> Unit,
    onOpenFolder: (MockupDesign) -> Unit,
    onCreateNewDesign: () -> Unit,
    onDeleteDesign: (String) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        SavedDesignsSheetContent(
            savedDesigns = savedDesigns,
            currentDesignId = currentDesignId,
            onDismiss = onDismiss,
            onSelectDesign = onSelectDesign,
            onOpenFolder = onOpenFolder,
            onCreateNewDesign = onCreateNewDesign,
            onDeleteDesign = onDeleteDesign
        )
    }
}

@Composable
fun SavedDesignsSheetContent(
    savedDesigns: List<MockupDesign>,
    currentDesignId: String,
    onDismiss: () -> Unit,
    onSelectDesign: (MockupDesign) -> Unit,
    onOpenFolder: (MockupDesign) -> Unit,
    onCreateNewDesign: () -> Unit,
    onDeleteDesign: (String) -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Mes Maquettes",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = {
                    onCreateNewDesign()
                    onDismiss()
                },
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text("Nouvelle", maxLines = 1)
            }
        }

        Spacer(modifier = Modifier.size(16.dp))

        if (savedDesigns.isEmpty()) {
            Text(
                text = "Aucune maquette enregistrée pour le moment.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 24.dp)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(320.dp)
            ) {
                items(savedDesigns) { design ->
                    val isSelected = design.id == currentDesignId
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected)
                                MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceContainer
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectDesign(design)
                                onDismiss()
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhoneAndroid,
                                    contentDescription = null,
                                    tint = if (isSelected)
                                        MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.size(12.dp))
                                Column {
                                    Text(
                                        text = design.title,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isSelected)
                                            MaterialTheme.colorScheme.onPrimaryContainer
                                        else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${design.elements.size} composant(s) • ${dateFormat.format(Date(design.timestamp))}",
                                        fontSize = 12.sp,
                                        color = if (isSelected)
                                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        onOpenFolder(design)
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FolderZip,
                                        contentDescription = "Ouvrir le dossier",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }

                                IconButton(
                                    onClick = { onDeleteDesign(design.id) }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Supprimer",
                                        tint = MaterialTheme.colorScheme.error
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
fun SavedDesignsSheetPreview() {
    AndroidDesignTheme {
        Surface {
            SavedDesignsSheetContent(
                savedDesigns = listOf(
                    MockupDesign(id = "1", title = "Maquette Connexion"),
                    MockupDesign(id = "2", title = "Maquette Profil")
                ),
                currentDesignId = "1",
                onDismiss = {},
                onSelectDesign = {},
                onOpenFolder = {},
                onCreateNewDesign = {},
                onDeleteDesign = {}
            )
        }
    }
}

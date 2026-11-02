package com.example.androiddesign.ui

import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.outlined.GridOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.androiddesign.model.ComponentType
import com.example.androiddesign.model.UiElement
import com.example.androiddesign.ui.components.AddComponentSheet
import com.example.androiddesign.ui.components.DesignFolderDialog
import com.example.androiddesign.ui.components.ImageChoiceDialog
import com.example.androiddesign.ui.components.MockupCanvas
import com.example.androiddesign.ui.components.PropertyInspector
import com.example.androiddesign.ui.components.SavedDesignsSheet
import com.example.androiddesign.ui.theme.AndroidDesignTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MockupAppScreen(
    context: android.content.Context = LocalContext.current,
    viewModel: MockupViewModel = viewModel(factory = MockupViewModelFactory(context))
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val addSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val savedSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var renameText by remember { mutableStateOf(uiState.currentDesign.title) }
    var isDraggingOrResizing by remember { mutableStateOf(false) }
    var showSaveCaptureDialog by remember { mutableStateOf(false) }
    var showImageChoiceDialog by remember { mutableStateOf(false) }
    
    // NEW STATE: Control visibility of the top menu bar to maximize canvas workspace
    var isTopBarVisible by remember { mutableStateOf(false) }

    // Photo Gallery Picker Launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.addElement(ComponentType.IMAGE_PLACEHOLDER, imageUri = it.toString())
        }
    }

    // Toast and Snackbar notification listener
    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
            }
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearToastMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            AnimatedVisibility(
                visible = isTopBarVisible,
                enter = slideInVertically(initialOffsetY = { -it }) + expandVertically(),
                exit = slideOutVertically(targetOffsetY = { -it }) + shrinkVertically()
            ) {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable {
                                    renameText = uiState.currentDesign.title
                                    viewModel.openRenameDialog()
                                }
                                .padding(vertical = 4.dp, horizontal = 6.dp)
                        ) {
                            Column {
                                Text(
                                    text = uiState.currentDesign.title,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "Taper pour renommer",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                )
                            }
                            Spacer(modifier = Modifier.size(6.dp))
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Renommer",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    },
                    actions = {
                        // Grid Toggle Button
                        IconButton(onClick = { viewModel.toggleGrid() }) {
                            Icon(
                                imageVector = if (uiState.isGridVisible) Icons.Default.GridOn else Icons.Outlined.GridOff,
                                contentDescription = "Grille de repère",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        // Saved Designs List
                        IconButton(onClick = { viewModel.openSavedDesignsSheet() }) {
                            Icon(
                                imageVector = Icons.Default.Folder,
                                contentDescription = "Mes Maquettes",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                        // Save Local Button
                        IconButton(onClick = { showSaveCaptureDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = "Sauvegarder",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        
                        // Collapse Top Bar Button
                        IconButton(onClick = { isTopBarVisible = false }) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowUp,
                                contentDescription = "Masquer le menu",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        }
    ) { innerPadding ->
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLandscape) {
                Row(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Left Canvas Area (60%)
                    Box(modifier = Modifier.weight(0.6f).fillMaxHeight()) {
                        MockupCanvas(
                            elements = uiState.currentDesign.elements,
                            selectedElementId = uiState.selectedElementId,
                            isGridVisible = uiState.isGridVisible,
                            isLandscape = true,
                            onSelectElement = { id -> viewModel.selectElement(id) },
                            onUpdateElementPosition = { id, x, y -> viewModel.updateElementPosition(id, x, y) },
                            onUpdateElementSize = { id, w, h -> viewModel.updateElementSize(id, w, h) },
                            onDeleteElement = { id -> viewModel.deleteElement(id) },
                            onDuplicateElement = { id -> viewModel.duplicateElement(id) },
                            onEditElement = { id -> viewModel.selectElement(id) },
                            onToggleLock = { id, locked -> viewModel.toggleElementLock(id, locked) },
                            onDragStateChange = { dragging -> isDraggingOrResizing = dragging },
                            graphicsLayer = graphicsLayer
                        )

                        FloatingActionButton(
                            onClick = { viewModel.openAddComponentSheet() },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            shape = CircleShape,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                                .size(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Ajouter un composant",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Right Property Inspector Area (40%)
                    val selectedElement = uiState.currentDesign.elements.find { it.id == uiState.selectedElementId }
                    AnimatedVisibility(
                        visible = selectedElement != null && selectedElement.isLocked && !isDraggingOrResizing,
                        enter = slideInHorizontally(initialOffsetX = { it }),
                        exit = slideOutHorizontally(targetOffsetX = { it }),
                        modifier = Modifier.weight(0.4f).fillMaxHeight()
                    ) {
                        selectedElement?.let { element ->
                            PropertyInspector(
                                element = element,
                                isLandscape = true,
                                onUpdateLabel = { newLabel -> viewModel.updateElementLabel(element.id, newLabel) },
                                onUpdateWidth = { newWidth -> viewModel.updateElementSize(element.id, newWidth, element.heightDp) },
                                onUpdateHeight = { newHeight -> viewModel.updateElementSize(element.id, element.widthDp, newHeight) },
                                onUpdateFontSize = { newSize -> viewModel.updateElementFontSize(element.id, newSize) },
                                onUpdateShowBorder = { showBorder -> viewModel.updateElementShowBorder(element.id, showBorder) },
                                onUnlock = { viewModel.toggleElementLock(element.id, false) },
                                onBringForward = { viewModel.bringForward(element.id) },
                                onSendBackward = { viewModel.sendBackward(element.id) },
                                onDuplicate = { viewModel.duplicateElement(element.id) },
                                onDelete = { viewModel.deleteElement(element.id) },
                                onClose = { viewModel.selectElement(null) }
                            )
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Main Canvas Workspace
                    MockupCanvas(
                        elements = uiState.currentDesign.elements,
                        selectedElementId = uiState.selectedElementId,
                        isGridVisible = uiState.isGridVisible,
                        isLandscape = false,
                        onSelectElement = { id -> viewModel.selectElement(id) },
                        onUpdateElementPosition = { id, x, y -> viewModel.updateElementPosition(id, x, y) },
                        onUpdateElementSize = { id, w, h -> viewModel.updateElementSize(id, w, h) },
                        onDeleteElement = { id -> viewModel.deleteElement(id) },
                        onDuplicateElement = { id -> viewModel.duplicateElement(id) },
                        onEditElement = { id -> viewModel.selectElement(id) },
                        onToggleLock = { id, locked -> viewModel.toggleElementLock(id, locked) },
                        onDragStateChange = { dragging -> isDraggingOrResizing = dragging },
                        graphicsLayer = graphicsLayer
                    )

                    // Middle-Right Small Floating Action Button "+"
                    FloatingActionButton(
                        onClick = { viewModel.openAddComponentSheet() },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = CircleShape,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 12.dp)
                            .size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Ajouter un composant",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Property Inspector when an element is LOCKED (HIDDEN WHEN UNLOCKED OR DRAGGING/RESIZING)
                    val selectedElement = uiState.currentDesign.elements.find { it.id == uiState.selectedElementId }
                    AnimatedVisibility(
                        visible = selectedElement != null && selectedElement.isLocked && !isDraggingOrResizing,
                        enter = slideInVertically(initialOffsetY = { it }),
                        exit = slideOutVertically(targetOffsetY = { it }),
                        modifier = Modifier.align(Alignment.BottomCenter)
                    ) {
                        selectedElement?.let { element ->
                            PropertyInspector(
                                element = element,
                                isLandscape = false,
                                onUpdateLabel = { newLabel -> viewModel.updateElementLabel(element.id, newLabel) },
                                onUpdateWidth = { newWidth -> viewModel.updateElementSize(element.id, newWidth, element.heightDp) },
                                onUpdateHeight = { newHeight -> viewModel.updateElementSize(element.id, element.widthDp, newHeight) },
                                onUpdateFontSize = { newSize -> viewModel.updateElementFontSize(element.id, newSize) },
                                onUpdateShowBorder = { showBorder -> viewModel.updateElementShowBorder(element.id, showBorder) },
                                onUnlock = { viewModel.toggleElementLock(element.id, false) },
                                onBringForward = { viewModel.bringForward(element.id) },
                                onSendBackward = { viewModel.sendBackward(element.id) },
                                onDuplicate = { viewModel.duplicateElement(element.id) },
                                onDelete = { viewModel.deleteElement(element.id) },
                                onClose = { viewModel.selectElement(null) }
                            )
                        }
                    }
                }
            }

            // Top Toggle Pill Button to Reveal Menu (when hidden)
            AnimatedVisibility(
                visible = !isTopBarVisible,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                    shadowElevation = 6.dp,
                    modifier = Modifier.clickable { isTopBarVisible = true }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Afficher le menu",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.size(8.dp))
                        Text(
                            text = "Afficher le menu",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }
        }
    }

    // Modal Sheet: Add Component
    if (uiState.isAddComponentSheetOpen) {
        AddComponentSheet(
            sheetState = addSheetState,
            onDismiss = { viewModel.closeAddComponentSheet() },
            onSelectComponent = { type ->
                if (type == ComponentType.IMAGE_PLACEHOLDER) {
                    showImageChoiceDialog = true
                } else {
                    viewModel.addElement(type)
                }
            }
        )
    }

    // Choice Dialog: Image Placeholder vs Gallery Import
    if (showImageChoiceDialog) {
        ImageChoiceDialog(
            onSelectRawPlaceholder = {
                viewModel.addElement(ComponentType.IMAGE_PLACEHOLDER)
            },
            onSelectFromGallery = {
                galleryLauncher.launch("image/*")
            },
            onDismiss = { showImageChoiceDialog = false }
        )
    }

    // Modal Sheet: Saved Designs
    if (uiState.isSavedDesignsSheetOpen) {
        SavedDesignsSheet(
            sheetState = savedSheetState,
            savedDesigns = uiState.savedDesigns,
            currentDesignId = uiState.currentDesign.id,
            onDismiss = { viewModel.closeSavedDesignsSheet() },
            onSelectDesign = { design -> viewModel.loadDesign(design) },
            onOpenFolder = { design -> viewModel.inspectDesignFolder(design) },
            onCreateNewDesign = { viewModel.createNewDesign() },
            onDeleteDesign = { id -> viewModel.deleteDesign(id) }
        )
    }

    // Design Folder File Inspection Dialog
    uiState.folderDesignToInspect?.let { folderDesign ->
        DesignFolderDialog(
            design = folderDesign,
            onLoadInEditor = { designToLoad -> viewModel.loadDesign(designToLoad) },
            onDismiss = { viewModel.inspectDesignFolder(null) }
        )
    }

    // Rename Dialog
    if (uiState.isRenameDialogOpen) {
        AlertDialog(
            onDismissRequest = { viewModel.closeRenameDialog() },
            title = { Text("Renommer la maquette") },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    label = { Text("Nom de la maquette") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.renameCurrentDesign(renameText) }) {
                    Text("Valider")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeRenameDialog() }) {
                    Text("Annuler")
                }
            }
        )
    }

    // Save & Screenshot Capture Confirmation Dialog
    if (showSaveCaptureDialog) {
        AlertDialog(
            onDismissRequest = { showSaveCaptureDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = { Text("Sauvegarder & Capturer l'écran") },
            text = {
                Text("Cette action va enregistrer la structure JSON, la spécification IA (.txt) et capturer une image screenshot (.png) de la maquette.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSaveCaptureDialog = false
                        viewModel.selectElement(null) // Deselect overlay handles for clean screenshot
                        scope.launch {
                            try {
                                val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()
                                viewModel.exportAsImage(bitmap)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                viewModel.saveDesignLocally()
                            }
                        }
                    }
                ) {
                    Text("Capturer & Sauvegarder")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveCaptureDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MockupAppScreenPreview() {
    AndroidDesignTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            MockupCanvas(
                elements = listOf(
                    UiElement(
                        type = ComponentType.TOP_APP_BAR,
                        label = "Mon Application",
                        xDp = 10f,
                        yDp = 20f,
                        widthDp = 280,
                        heightDp = 50
                    ),
                    UiElement(
                        type = ComponentType.BUTTON,
                        label = "Valider",
                        xDp = 20f,
                        yDp = 120f,
                        widthDp = 240,
                        heightDp = 48
                    )
                ),
                selectedElementId = null,
                isGridVisible = true,
                onSelectElement = {},
                onUpdateElementPosition = { _, _, _ -> },
                onUpdateElementSize = { _, _, _ -> },
                onDeleteElement = {},
                onDuplicateElement = {},
                onEditElement = {}
            )
        }
    }
}

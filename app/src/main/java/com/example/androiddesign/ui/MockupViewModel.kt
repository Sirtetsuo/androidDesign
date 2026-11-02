package com.example.androiddesign.ui

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.androiddesign.data.MockupRepository
import com.example.androiddesign.model.ComponentType
import com.example.androiddesign.model.MockupDesign
import com.example.androiddesign.model.UiElement
import com.example.androiddesign.util.DesignExportManager
import com.example.androiddesign.util.ImageExportUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class MockupUiState(
    val currentDesign: MockupDesign = MockupDesign(),
    val selectedElementId: String? = null,
    val isGridVisible: Boolean = true,
    val savedDesigns: List<MockupDesign> = emptyList(),
    val isAddComponentSheetOpen: Boolean = false,
    val isSavedDesignsSheetOpen: Boolean = false,
    val isRenameDialogOpen: Boolean = false,
    val folderDesignToInspect: MockupDesign? = null,
    val exportedImagePath: String? = null,
    val toastMessage: String? = null
)

class MockupViewModel(
    private val repository: MockupRepository,
    private val designExportManager: DesignExportManager,
    private val imageExportUtil: ImageExportUtil
) : ViewModel() {

    private val _uiState = MutableStateFlow(MockupUiState())
    val uiState: StateFlow<MockupUiState> = _uiState.asStateFlow()

    init {
        loadSavedDesigns()
    }

    private fun loadSavedDesigns() {
        viewModelScope.launch {
            val list = repository.getAllDesigns()
            if (list.isNotEmpty()) {
                _uiState.update {
                    it.copy(
                        savedDesigns = list,
                        currentDesign = list.first()
                    )
                }
            } else {
                val initialDesign = MockupDesign(title = "Maquette #1")
                repository.saveDesign(initialDesign)
                _uiState.update {
                    it.copy(
                        savedDesigns = listOf(initialDesign),
                        currentDesign = initialDesign
                    )
                }
            }
        }
    }

    fun addElement(type: ComponentType, imageUri: String? = null) {
        val elements = _uiState.value.currentDesign.elements
        val newCount = elements.size + 1
        val newElement = UiElement(
            type = type,
            label = when (type) {
                ComponentType.TEXT_LABEL -> "Label Texte $newCount"
                ComponentType.BUTTON -> "Bouton $newCount"
                ComponentType.TEXT_INPUT -> "Champ Texte $newCount"
                ComponentType.CHECKBOX -> "Option $newCount"
                ComponentType.SWITCH -> "Activer $newCount"
                ComponentType.IMAGE_PLACEHOLDER -> "Image $newCount"
                ComponentType.CARD_CONTAINER -> "Carte $newCount"
                ComponentType.DIVIDER -> ""
                ComponentType.TOP_APP_BAR -> "Titre de l'application"
                ComponentType.MENU_HAMBURGER -> "Menu Principal"
            },
            xDp = 40f + (newCount * 10 % 100),
            yDp = 60f + (newCount * 25 % 300),
            zOrder = elements.size,
            showBorder = true,
            imageUri = imageUri
        )

        val updatedElements = elements + newElement
        updateCurrentDesignElements(updatedElements, selectElementId = newElement.id)
    }

    fun selectElement(elementId: String?) {
        _uiState.update { it.copy(selectedElementId = elementId) }
    }

    fun updateElementPosition(id: String, xDp: Float, yDp: Float) {
        val updatedElements = _uiState.value.currentDesign.elements.map { elem ->
            if (elem.id == id) {
                elem.copy(xDp = xDp.coerceAtLeast(0f), yDp = yDp.coerceAtLeast(0f))
            } else elem
        }
        updateCurrentDesignElements(updatedElements, selectElementId = id)
    }

    fun updateElementSize(id: String, widthDp: Int, heightDp: Int) {
        val updatedElements = _uiState.value.currentDesign.elements.map { elem ->
            if (elem.id == id) {
                elem.copy(
                    widthDp = widthDp.coerceAtLeast(30),
                    heightDp = heightDp.coerceAtLeast(20)
                )
            } else elem
        }
        updateCurrentDesignElements(updatedElements, selectElementId = id)
    }

    fun updateElementLabel(id: String, label: String) {
        val updatedElements = _uiState.value.currentDesign.elements.map { elem ->
            if (elem.id == id) elem.copy(label = label) else elem
        }
        updateCurrentDesignElements(updatedElements, selectElementId = id)
    }

    fun updateElementFontSize(id: String, fontSizeSp: Int) {
        val updatedElements = _uiState.value.currentDesign.elements.map { elem ->
            if (elem.id == id) elem.copy(fontSizeSp = fontSizeSp) else elem
        }
        updateCurrentDesignElements(updatedElements, selectElementId = id)
    }

    fun updateElementShowBorder(id: String, showBorder: Boolean) {
        val updatedElements = _uiState.value.currentDesign.elements.map { elem ->
            if (elem.id == id) elem.copy(showBorder = showBorder) else elem
        }
        updateCurrentDesignElements(updatedElements, selectElementId = id)
    }

    fun toggleElementLock(id: String, isLocked: Boolean) {
        val updatedElements = _uiState.value.currentDesign.elements.map { elem ->
            if (elem.id == id) elem.copy(isLocked = isLocked) else elem
        }
        updateCurrentDesignElements(updatedElements, selectElementId = id)
    }

    fun duplicateElement(id: String) {
        val target = _uiState.value.currentDesign.elements.find { it.id == id } ?: return
        val copyElement = target.copy(
            id = UUID.randomUUID().toString(),
            xDp = target.xDp + 20f,
            yDp = target.yDp + 20f,
            zOrder = _uiState.value.currentDesign.elements.size
        )
        val updatedElements = _uiState.value.currentDesign.elements + copyElement
        updateCurrentDesignElements(updatedElements, selectElementId = copyElement.id)
    }

    fun deleteElement(id: String) {
        val updatedElements = _uiState.value.currentDesign.elements.filterNot { it.id == id }
        val newSelectedId = if (_uiState.value.selectedElementId == id) null else _uiState.value.selectedElementId
        updateCurrentDesignElements(updatedElements, selectElementId = newSelectedId)
    }

    fun bringForward(id: String) {
        val elements = _uiState.value.currentDesign.elements.toMutableList()
        val index = elements.indexOfFirst { it.id == id }
        if (index >= 0 && index < elements.size - 1) {
            val item = elements.removeAt(index)
            elements.add(index + 1, item)
            // Re-assign proper zOrder sequence
            val reordered = elements.mapIndexed { idx, elem -> elem.copy(zOrder = idx) }
            updateCurrentDesignElements(reordered, selectElementId = id)
        }
    }

    fun sendBackward(id: String) {
        val elements = _uiState.value.currentDesign.elements.toMutableList()
        val index = elements.indexOfFirst { it.id == id }
        if (index > 0) {
            val item = elements.removeAt(index)
            elements.add(index - 1, item)
            // Re-assign proper zOrder sequence
            val reordered = elements.mapIndexed { idx, elem -> elem.copy(zOrder = idx) }
            updateCurrentDesignElements(reordered, selectElementId = id)
        }
    }

    fun toggleGrid() {
        _uiState.update { it.copy(isGridVisible = !it.isGridVisible) }
    }

    fun openAddComponentSheet() {
        _uiState.update { it.copy(isAddComponentSheetOpen = true) }
    }

    fun closeAddComponentSheet() {
        _uiState.update { it.copy(isAddComponentSheetOpen = false) }
    }

    fun openSavedDesignsSheet() {
        _uiState.update { it.copy(isSavedDesignsSheetOpen = true) }
    }

    fun closeSavedDesignsSheet() {
        _uiState.update { it.copy(isSavedDesignsSheetOpen = false) }
    }

    fun inspectDesignFolder(design: MockupDesign?) {
        _uiState.update { it.copy(folderDesignToInspect = design) }
    }

    fun openRenameDialog() {
        _uiState.update { it.copy(isRenameDialogOpen = true) }
    }

    fun closeRenameDialog() {
        _uiState.update { it.copy(isRenameDialogOpen = false) }
    }

    fun renameCurrentDesign(newTitle: String) {
        val updatedDesign = _uiState.value.currentDesign.copy(
            title = newTitle.ifBlank { "Maquette" },
            timestamp = System.currentTimeMillis()
        )
        // Optimistic UI Update
        _uiState.update {
            it.copy(
                currentDesign = updatedDesign,
                isRenameDialogOpen = false,
                toastMessage = "Maquette renommée avec succès"
            )
        }
        viewModelScope.launch {
            repository.saveDesign(updatedDesign)
            val updatedList = repository.getAllDesigns()
            _uiState.update { it.copy(savedDesigns = updatedList) }
        }
    }

    fun saveDesignLocally() {
        val designToSave = _uiState.value.currentDesign.copy(timestamp = System.currentTimeMillis())
        
        viewModelScope.launch {
            repository.saveDesign(designToSave)
            
            // Export folder files (JSON + AI Spec TXT) runs asynchronously 
            launch(Dispatchers.IO) {
                designExportManager.exportDesignFolderSync(designToSave)
            }
            
            val updatedList = repository.getAllDesigns()
            _uiState.update {
                it.copy(
                    savedDesigns = updatedList,
                    toastMessage = "Dossier de la maquette '${designToSave.title}' mis à jour !"
                )
            }
        }
    }

    fun createNewDesign() {
        val count = _uiState.value.savedDesigns.size + 1
        val newDesign = MockupDesign(title = "Maquette #$count")
        
        _uiState.update {
            it.copy(
                currentDesign = newDesign,
                selectedElementId = null,
                toastMessage = "Nouvelle page vierge créée"
            )
        }
        viewModelScope.launch {
            repository.saveDesign(newDesign)
            val updatedList = repository.getAllDesigns()
            _uiState.update { it.copy(savedDesigns = updatedList) }
        }
    }

    fun loadDesign(design: MockupDesign) {
        _uiState.update {
            it.copy(
                currentDesign = design,
                selectedElementId = null,
                toastMessage = "Maquette '${design.title}' chargée"
            )
        }
    }

    fun deleteDesign(designId: String) {
        viewModelScope.launch {
            repository.deleteDesign(designId)
            val remaining = repository.getAllDesigns()
            if (remaining.isNotEmpty()) {
                val nextDesign = remaining.first()
                _uiState.update {
                    it.copy(
                        savedDesigns = remaining,
                        currentDesign = nextDesign,
                        selectedElementId = null,
                        toastMessage = "Maquette supprimée"
                    )
                }
            } else {
                createNewDesign()
            }
        }
    }

    fun exportAsImage(bitmap: Bitmap) {
        viewModelScope.launch {
            val design = _uiState.value.currentDesign
            val title = design.title
            
            // IO Operations delegated to properly decoupled components
            val galleryResult = kotlinx.coroutines.withContext(Dispatchers.IO) {
                imageExportUtil.saveBitmapToGallery(bitmap, title)
            }
            
            val folderResult = kotlinx.coroutines.withContext(Dispatchers.IO) {
                designExportManager.exportDesignFolderSync(design, bitmap)
            }
            
            if (galleryResult.isSuccess && folderResult.isSuccess) {
                _uiState.update {
                    it.copy(
                        exportedImagePath = galleryResult.getOrNull(),
                        toastMessage = "Dossier et Screenshot sauvegardés !"
                    )
                }
            } else {
                _uiState.update {
                    it.copy(toastMessage = "Erreur lors de la sauvegarde d'image")
                }
            }
        }
    }

    fun clearToastMessage() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    private fun updateCurrentDesignElements(elements: List<UiElement>, selectElementId: String?) {
        // PRO OPTIMIZATION: Always ensure the list is sorted by zOrder whenever state is modified.
        // This prevents Compose from having to sort the list dynamically at 60fps during a drag operation.
        val sortedElements = elements.sortedBy { it.zOrder }
        val updatedDesign = _uiState.value.currentDesign.copy(
            elements = sortedElements,
            timestamp = System.currentTimeMillis()
        )
        
        // Optimistic UI Update: We update state immediately for 0-latency feedback
        _uiState.update {
            it.copy(
                currentDesign = updatedDesign,
                selectedElementId = selectElementId
            )
        }
        
        // Launch a coroutine to persist data asynchronously without blocking UI
        viewModelScope.launch {
            repository.saveDesign(updatedDesign)
            val updatedList = repository.getAllDesigns()
            _uiState.update { it.copy(savedDesigns = updatedList) }
        }
    }
}

class MockupViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MockupViewModel::class.java)) {
            val repo = MockupRepository(context.applicationContext)
            val designExportManager = DesignExportManager(context.applicationContext)
            val imageExportUtil = ImageExportUtil(context.applicationContext)
            
            @Suppress("UNCHECKED_CAST")
            return MockupViewModel(repo, designExportManager, imageExportUtil) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

package com.example.androiddesign.model

import java.util.UUID

enum class ComponentType(val displayName: String, val iconName: String, val defaultWidthDp: Int, val defaultHeightDp: Int) {
    TEXT_LABEL("Label Texte", "title", 180, 40),
    BUTTON("Bouton", "smart_button", 160, 50),
    TEXT_INPUT("Champ Texte", "edit", 260, 60),
    CHECKBOX("Case à cocher", "check_box", 180, 40),
    SWITCH("Interrupteur", "toggle_on", 180, 40),
    IMAGE_PLACEHOLDER("Image", "image", 200, 150),
    CARD_CONTAINER("Conteneur / Carte", "crop_square", 280, 180),
    DIVIDER("Ligne de séparation", "horizontal_rule", 280, 20),
    TOP_APP_BAR("Barre de navigation", "web_asset", 320, 60),
    MENU_HAMBURGER("Menu Principal (☰)", "menu", 48, 48)
}

data class UiElement(
    val id: String = UUID.randomUUID().toString(),
    val type: ComponentType,
    val label: String,
    val xDp: Float,
    val yDp: Float,
    val widthDp: Int = type.defaultWidthDp,
    val heightDp: Int = type.defaultHeightDp,
    val fontSizeSp: Int = 16,
    val zOrder: Int = 0,
    val showBorder: Boolean = true,
    val imageUri: String? = null,
    val isLocked: Boolean = false
)

data class MockupDesign(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Ma Maquette",
    val timestamp: Long = System.currentTimeMillis(),
    val elements: List<UiElement> = emptyList()
)

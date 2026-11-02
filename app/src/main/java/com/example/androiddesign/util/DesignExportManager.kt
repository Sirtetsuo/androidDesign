package com.example.androiddesign.util

import android.content.Context
import android.graphics.Bitmap
import com.example.androiddesign.model.MockupDesign
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

data class DesignFolderFiles(
    val folderPath: String,
    val jsonFile: File,
    val specFile: File,
    val imageFile: File?,
    val specText: String
)

class DesignExportManager(private val context: Context) {

    private fun generateAiSpecText(design: MockupDesign): String {
        val sb = StringBuilder()
        sb.appendLine("=== MAQUETTE D'INTERFACE ANDROID ===")
        sb.appendLine("Titre: ${design.title}")
        sb.appendLine("Nombre de composants: ${design.elements.size}")
        sb.appendLine("----------------------------------------")

        design.elements.sortedBy { it.zOrder }.forEachIndexed { index, elem ->
            sb.appendLine("${index + 1}. [${elem.type.name}] ${elem.type.displayName}")
            sb.appendLine("   - Texte / Libellé : \"${elem.label}\"")
            sb.appendLine("   - Position        : x=${elem.xDp.toInt()}dp, y=${elem.yDp.toInt()}dp")
            sb.appendLine("   - Dimensions      : ${elem.widthDp}dp x ${elem.heightDp}dp")
            sb.appendLine("   - Taille Police   : ${elem.fontSizeSp}sp")
            sb.appendLine("   - Bordure Visible : ${if (elem.showBorder) "Oui" else "Non"}")
            sb.appendLine()
        }

        sb.appendLine("----------------------------------------")
        sb.appendLine("Consigne AI: Génère le code Jetpack Compose correspondant à ce visuel.")
        return sb.toString()
    }

    fun exportDesignFolderSync(
        design: MockupDesign,
        bitmap: Bitmap? = null
    ): Result<DesignFolderFiles> {
        return try {
            val cleanTitle = design.title.replace("[^a-zA-Z0-9_]".toRegex(), "_")
            val parentDir = File(context.filesDir, "Maquettes/$cleanTitle")
            if (!parentDir.exists()) {
                parentDir.mkdirs()
            }

            // 1. JSON file
            val jsonFile = File(parentDir, "layout.json")
            val jsonContent = serializeDesignToJson(design)
            jsonFile.writeText(jsonContent)

            // 2. Spec TXT file for AI
            val specText = generateAiSpecText(design)
            val specFile = File(parentDir, "spec_assistant_ai.txt")
            specFile.writeText(specText)

            // 3. PNG Image preview
            var imageFile: File? = null
            if (bitmap != null) {
                val img = File(parentDir, "screenshot.png")
                FileOutputStream(img).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                imageFile = img
            } else {
                val existingImg = File(parentDir, "screenshot.png")
                if (existingImg.exists()) {
                    imageFile = existingImg
                }
            }

            Result.success(
                DesignFolderFiles(
                    folderPath = parentDir.absolutePath,
                    jsonFile = jsonFile,
                    specFile = specFile,
                    imageFile = imageFile,
                    specText = specText
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    private fun serializeDesignToJson(design: MockupDesign): String {
        val obj = org.json.JSONObject()
        obj.put("id", design.id)
        obj.put("title", design.title)
        obj.put("timestamp", design.timestamp)

        val array = org.json.JSONArray()
        design.elements.forEach { elem ->
            val eObj = org.json.JSONObject()
            eObj.put("id", elem.id)
            eObj.put("type", elem.type.name)
            eObj.put("label", elem.label)
            eObj.put("xDp", elem.xDp)
            eObj.put("yDp", elem.yDp)
            eObj.put("widthDp", elem.widthDp)
            eObj.put("heightDp", elem.heightDp)
            eObj.put("fontSizeSp", elem.fontSizeSp)
            eObj.put("zOrder", elem.zOrder)
            eObj.put("showBorder", elem.showBorder)
            array.put(eObj)
        }
        obj.put("elements", array)
        return obj.toString(2)
    }
}

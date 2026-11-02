package com.example.androiddesign.data

import android.content.Context
import com.example.androiddesign.model.ComponentType
import com.example.androiddesign.model.MockupDesign
import com.example.androiddesign.model.UiElement
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class MockupRepository(context: Context) {
    private val prefs = context.getSharedPreferences("mockup_designs_prefs", Context.MODE_PRIVATE)

    suspend fun getAllDesigns(): List<MockupDesign> = withContext(Dispatchers.IO) {
        val jsonString = prefs.getString("saved_designs_json", null) ?: return@withContext emptyList()
        try {
            val jsonArray = JSONArray(jsonString)
            val list = mutableListOf<MockupDesign>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(deserializeDesign(obj))
            }
            // Tri par date le plus récent en premier
            list.sortedByDescending { it.timestamp }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun saveDesign(design: MockupDesign) = withContext(Dispatchers.IO) {
        val currentList = getAllDesigns().toMutableList()
        val index = currentList.indexOfFirst { it.id == design.id }
        if (index >= 0) {
            currentList[index] = design
        } else {
            currentList.add(0, design)
        }
        persistList(currentList)
    }

    suspend fun deleteDesign(designId: String) = withContext(Dispatchers.IO) {
        val currentList = getAllDesigns().filterNot { it.id == designId }
        persistList(currentList)
    }

    private fun persistList(list: List<MockupDesign>) {
        val jsonArray = JSONArray()
        list.forEach { design ->
            jsonArray.put(serializeDesign(design))
        }
        prefs.edit().putString("saved_designs_json", jsonArray.toString()).apply()
    }

    private fun serializeDesign(design: MockupDesign): JSONObject {
        val obj = JSONObject()
        obj.put("id", design.id)
        obj.put("title", design.title)
        obj.put("timestamp", design.timestamp)

        val elementsArray = JSONArray()
        design.elements.forEach { elem ->
            val elemObj = JSONObject()
            elemObj.put("id", elem.id)
            elemObj.put("type", elem.type.name)
            elemObj.put("label", elem.label)
            elemObj.put("xDp", elem.xDp.toDouble())
            elemObj.put("yDp", elem.yDp.toDouble())
            elemObj.put("widthDp", elem.widthDp)
            elemObj.put("heightDp", elem.heightDp)
            elemObj.put("fontSizeSp", elem.fontSizeSp)
            elemObj.put("zOrder", elem.zOrder)
            elemObj.put("showBorder", elem.showBorder)
            if (elem.imageUri != null) {
                elemObj.put("imageUri", elem.imageUri)
            }
            elementsArray.put(elemObj)
        }
        obj.put("elements", elementsArray)
        return obj
    }

    private fun deserializeDesign(obj: JSONObject): MockupDesign {
        val id = obj.getString("id")
        val title = obj.optString("title", "Maquette Sans Titre")
        val timestamp = obj.optLong("timestamp", System.currentTimeMillis())

        val elements = mutableListOf<UiElement>()
        val elementsArray = obj.optJSONArray("elements") ?: JSONArray()
        for (i in 0 until elementsArray.length()) {
            val elemObj = elementsArray.getJSONObject(i)
            val typeStr = elemObj.getString("type")
            val type = try {
                ComponentType.valueOf(typeStr)
            } catch (e: Exception) {
                ComponentType.TEXT_LABEL
            }
            val imgUri = if (elemObj.has("imageUri")) elemObj.getString("imageUri") else null
            elements.add(
                UiElement(
                    id = elemObj.getString("id"),
                    type = type,
                    label = elemObj.optString("label", "Element"),
                    xDp = elemObj.optDouble("xDp", 20.0).toFloat(),
                    yDp = elemObj.optDouble("yDp", 20.0).toFloat(),
                    widthDp = elemObj.optInt("widthDp", type.defaultWidthDp),
                    heightDp = elemObj.optInt("heightDp", type.defaultHeightDp),
                    fontSizeSp = elemObj.optInt("fontSizeSp", 16),
                    zOrder = elemObj.optInt("zOrder", i),
                    showBorder = elemObj.optBoolean("showBorder", true),
                    imageUri = imgUri
                )
            )
        }
        return MockupDesign(
            id = id,
            title = title,
            timestamp = timestamp,
            elements = elements
        )
    }
}

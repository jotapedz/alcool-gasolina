package com.example.exemplosimplesdecompose.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class FuelPreferencesRepository(context: Context) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun useSeventyFivePercent(): Boolean = prefs.getBoolean(KEY_USE_SEVENTY_FIVE_PERCENT, false)

    fun saveUseSeventyFivePercent(useSeventyFivePercent: Boolean) {
        prefs.edit().putBoolean(KEY_USE_SEVENTY_FIVE_PERCENT, useSeventyFivePercent).apply()
    }

    fun getPostos(): List<Posto> {
        val savedJson = prefs.getString(KEY_POSTOS, "[]") ?: "[]"
        val jsonArray = JSONArray(savedJson)
        val postos = mutableListOf<Posto>()

        for (index in 0 until jsonArray.length()) {
            val item = jsonArray.optJSONObject(index) ?: continue
            postos += item.toPosto()
        }

        return postos.sortedByDescending { it.dataInformacaoMillis }
    }

    fun getPostoById(id: String): Posto? = getPostos().firstOrNull { it.id == id }

    fun upsertPosto(posto: Posto) {
        val postos = getPostos().toMutableList()
        val existingIndex = postos.indexOfFirst { it.id == posto.id }

        if (existingIndex >= 0) {
            postos[existingIndex] = posto
        } else {
            postos += posto
        }

        savePostos(postos)
    }

    fun deletePosto(id: String) {
        val postos = getPostos().filterNot { it.id == id }
        savePostos(postos)
    }

    private fun savePostos(postos: List<Posto>) {
        val jsonArray = JSONArray()
        postos.forEach { posto -> jsonArray.put(posto.toJson()) }
        prefs.edit().putString(KEY_POSTOS, jsonArray.toString()).apply()
    }

    private fun JSONObject.toPosto(): Posto {
        return Posto(
            id = optString("id"),
            nome = optString("nome"),
            alcool = optDouble("alcool"),
            gasolina = optDouble("gasolina"),
            dataInformacaoMillis = optLong("dataInformacaoMillis"),
            latitude = if (has("latitude") && !isNull("latitude")) optDouble("latitude") else null,
            longitude = if (has("longitude") && !isNull("longitude")) optDouble("longitude") else null
        )
    }

    private fun Posto.toJson(): JSONObject {
        return JSONObject().apply {
            put("id", id)
            put("nome", nome)
            put("alcool", alcool)
            put("gasolina", gasolina)
            put("dataInformacaoMillis", dataInformacaoMillis)
            put("latitude", latitude ?: JSONObject.NULL)
            put("longitude", longitude ?: JSONObject.NULL)
        }
    }

    companion object {
        private const val PREFS_NAME = "alcool_gasolina_prefs"
        private const val KEY_USE_SEVENTY_FIVE_PERCENT = "use_seventy_five_percent"
        private const val KEY_POSTOS = "postos"
    }
}

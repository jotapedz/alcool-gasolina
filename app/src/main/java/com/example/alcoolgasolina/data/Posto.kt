package com.example.alcoolgasolina.data

import java.util.UUID

data class Posto(
    val id: String = UUID.randomUUID().toString(),
    val nome: String,
    val alcool: Double = 0.0,
    val gasolina: Double = 0.0,
    val dataInformacaoMillis: Long = System.currentTimeMillis(),
    val latitude: Double? = null,
    val longitude: Double? = null
) {
    constructor(nome: String, coordenadas: Coordenadas) : this(
        nome = nome,
        latitude = coordenadas.latitude,
        longitude = coordenadas.longitude
    )
}

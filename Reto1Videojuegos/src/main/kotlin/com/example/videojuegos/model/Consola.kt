package com.example.videojuegos.model

/** @author Constantin Laurentiu David
 * Entidad: Consola
 */

data class Consola(
    val idConsola: Int,
    val nombre: String,
    val fabricante: String,
    val generacion: Int,
    val ventas: Double
)
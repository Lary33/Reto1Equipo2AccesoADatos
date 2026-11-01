package com.example.videojuegos.model

/**
 * @author Jesús Blázquez
 * Entidad: Periféricos
 */
data class Periferico(
    var idPeriferico: Int = 0,
    var modelo: String = "",
    var marca: String = "",
    var dpiMaximo: Int = 0,
    var pesoGramos: Double = 0.0
)

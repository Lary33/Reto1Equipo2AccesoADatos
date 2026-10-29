package com.example.videojuegos.model

/** @author Mario Handarici
 * Entidad: Videojuegos
 */

data class Videojuego(
    var idVideojuego: Int = 0,
    var titulo: String = "",
    var estudio: String = "",
    var lanzamiento: Int = 0,
    var precio: Double = 0.0
)

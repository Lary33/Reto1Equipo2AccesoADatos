package com.example.videojuegos.repository

import com.example.videojuegos.model.Videojuego
import org.springframework.stereotype.Repository
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

@Repository
class VideojuegoRepository {
    private val ruta: Path = Path.of("datos", "Videojuegos.csv")

    init {
        Files.createDirectories(ruta.parent)
        if (Files.notExists(ruta)) {
            javaClass.getResourceAsStream("/datos/Videojuegos.csv").use { origen ->
                if (origen != null) Files.copy(origen, ruta) else Files.createFile(ruta)
            }
        }
    }

    fun listar(): List<Videojuego> = Files.readAllLines(ruta, StandardCharsets.UTF_8)
        .filter { it.isNotBlank() }
        .map { linea ->
            val campos = linea.split(';')
            require(campos.size == 5) { "Fila incorrecta en el CSV: $linea" }
            Videojuego(
                campos[0].toInt(), campos[1], campos[2],
                campos[3].toInt(), campos[4].toDouble()
            )
        }

    fun buscar(id: Int): Videojuego? = listar().find { it.idVideojuego == id }

    @Synchronized
    fun guardar(videojuego: Videojuego) {
        val juegos = listar().toMutableList()
        if (videojuego.idVideojuego == 0) {
            videojuego.idVideojuego = (juegos.maxOfOrNull { it.idVideojuego } ?: 0) + 1
            juegos.add(videojuego)
        } else {
            val posicion = juegos.indexOfFirst { it.idVideojuego == videojuego.idVideojuego }
            require(posicion >= 0) { "No existe ese videojuego" }
            juegos[posicion] = videojuego
        }
        escribir(juegos)
    }

    @Synchronized
    fun borrar(id: Int) {
        escribir(listar().filter { it.idVideojuego != id })
    }

    private fun escribir(juegos: List<Videojuego>) {
        val lineas = juegos.map {
            "${it.idVideojuego};${it.titulo};${it.estudio};${it.lanzamiento};${it.precio}"
        }
        Files.write(ruta, lineas, StandardCharsets.UTF_8)
    }
}

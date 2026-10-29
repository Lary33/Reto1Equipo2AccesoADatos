
package com.example.videojuegos.repository

import com.example.videojuegos.model.Videojuego
import org.springframework.stereotype.Repository
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Mario Handarici
 * Entidad: Videojuegos
 */
@Repository
class VideojuegoRepository {

    private val ruta: Path = Path.of(
        "src", "main", "resources", "data", "Videojuegos.csv"
    )

    init {
        Files.createDirectories(ruta.parent)

        if (Files.notExists(ruta)) {
            val origen = javaClass.getResourceAsStream(
                "/data/Videojuegos.csv"
            )

            if (origen != null) {
                origen.use {
                    Files.copy(it, ruta)
                }
            } else {
                Files.createFile(ruta)
            }
        }
    }

    @Synchronized
    fun listar(): List<Videojuego> {

        val archivo: File = ruta.toFile()

        return archivo.readLines(StandardCharsets.UTF_8)
            .filter { it.isNotBlank() }
            .map { linea ->

                val campos = linea.split(";")

                require(campos.size == 5) {
                    "Fila incorrecta en el CSV: $linea"
                }

                Videojuego(
                    idVideojuego = campos[0].toInt(),
                    titulo = campos[1],
                    estudio = campos[2],
                    lanzamiento = campos[3].toInt(),
                    precio = campos[4].toDouble()
                )
            }
    }

    fun buscar(id: Int): Videojuego? {
        return listar().find {
            it.idVideojuego == id
        }
    }

    @Synchronized
    fun guardar(videojuego: Videojuego) {

        val juegos = listar().toMutableList()

        if (videojuego.idVideojuego == 0) {

            val nuevoId = (
                    juegos.maxOfOrNull { it.idVideojuego } ?: 0
                    ) + 1

            videojuego.idVideojuego = nuevoId
            juegos.add(videojuego)

        } else {

            val posicion = juegos.indexOfFirst {
                it.idVideojuego == videojuego.idVideojuego
            }

            require(posicion >= 0) {
                "No existe ese videojuego"
            }

            juegos[posicion] = videojuego
        }

        escribir(juegos)
    }

    @Synchronized
    fun borrar(id: Int) {

        val juegos = listar().filter {
            it.idVideojuego != id
        }

        escribir(juegos)
    }

    private fun escribir(juegos: List<Videojuego>) {

        val lineas = juegos.map {
            "${it.idVideojuego};${it.titulo};${it.estudio};${it.lanzamiento};${it.precio}"
        }

        Files.write(
            ruta,
            lineas,
            StandardCharsets.UTF_8
        )
    }
}

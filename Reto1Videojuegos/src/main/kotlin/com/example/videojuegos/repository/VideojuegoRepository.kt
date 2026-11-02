
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

    // Localiza el CSV existente desde la carpeta del proyecto o desde su carpeta padre.
    // No crea otra carpeta data si IntelliJ cambia el directorio de ejecución.
    private val ruta: Path = listOf(
        Path.of("src", "main", "resources", "data", "Videojuegos.csv"),
        Path.of("Reto1Videojuegos", "src", "main", "resources", "data", "Videojuegos.csv")
    ).firstOrNull { Files.isRegularFile(it) }
        ?: error("No se encuentra src/main/resources/data/Videojuegos.csv. Comprueba el directorio de trabajo.")

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

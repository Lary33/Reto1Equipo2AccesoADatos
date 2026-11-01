package com.example.videojuegos.repository

import com.example.videojuegos.model.Consola
import org.springframework.stereotype.Repository
import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Constantin Laurentiu David
 * Entidad: Consola
 */

@Repository
class ConsolaRepository {

    private val ruta: Path = Path.of(
        "src", "main", "resources", "data", "Consola.csv"
    )

    init {
        Files.createDirectories(ruta.parent)

        if (Files.notExists(ruta)) {
            val origen = javaClass.getResourceAsStream(
                "/data/Consola.csv"
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
    fun listar(): List<Consola> {

        val archivo: File = ruta.toFile()

        return archivo.readLines(StandardCharsets.UTF_8)
            .filter { it.isNotBlank() }
            .map { linea ->

                val campos = linea.split(";")

                require(campos.size == 5) {
                    "Fila incorrecta en el CSV: $linea"
                }

                Consola(
                    idConsola = campos[0].toInt(),
                    nombre = campos[1],
                    fabricante = campos[2],
                    generacion = campos[3].toInt(),
                    ventas = campos[4].toDouble()
                )
            }
    }

    fun buscar(id: Int): Consola? {
        return listar().find {
            it.idConsola == id
        }
    }

    @Synchronized
    fun guardar(consola: Consola) {

        val consolas = listar().toMutableList()

        if (consola.idConsola == 0) {

            val nuevoId = (
                    consolas.maxOfOrNull { it.idConsola } ?: 0
                    ) + 1

            // Consola tiene todos sus campos como val, así que se crea una copia con el id nuevo
            consolas.add(consola.copy(idConsola = nuevoId))

        } else {

            val posicion = consolas.indexOfFirst {
                it.idConsola == consola.idConsola
            }

            require(posicion >= 0) {
                "No existe esa consola"
            }

            consolas[posicion] = consola
        }

        escribir(consolas)
    }

    @Synchronized
    fun borrar(id: Int) {

        val consolas = listar().filter {
            it.idConsola != id
        }

        escribir(consolas)
    }

    private fun escribir(consolas: List<Consola>) {

        val lineas = consolas.map {
            "${it.idConsola};${it.nombre};${it.fabricante};${it.generacion};${it.ventas}"
        }

        Files.write(
            ruta,
            lineas,
            StandardCharsets.UTF_8
        )
    }
}

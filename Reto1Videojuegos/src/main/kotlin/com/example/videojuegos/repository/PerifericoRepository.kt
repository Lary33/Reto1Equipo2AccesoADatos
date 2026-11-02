package com.example.videojuegos.repository

import com.example.videojuegos.model.Periferico
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import org.springframework.stereotype.Repository
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

/**
 * @author Jesús Blázquez
 * Entidad: Periféricos
 */
@Repository
class PerifericoRepository {

    // Se utiliza siempre el CSV existente, sin crear carpetas duplicadas.
    private val ruta: Path = listOf(
        Path.of("src", "main", "resources", "data", "Perifericos.csv"),
        Path.of("Reto1Videojuegos", "src", "main", "resources", "data", "Perifericos.csv")
    ).firstOrNull { Files.isRegularFile(it) }
        ?: error("No se encuentra src/main/resources/data/Perifericos.csv. Comprueba el directorio de trabajo.")

    @Synchronized
    fun listar(): List<Periferico> {
        val lector = csvReader { delimiter = ';' }
        val filas: List<List<String>> = lector.readAll(ruta.toFile())

        return filas.filter { it.isNotEmpty() && it[0].isNotBlank() }.map { campos ->
            require(campos.size == 5) { "Fila incorrecta en Perifericos.csv: $campos" }
            Periferico(
                campos[0].toInt(),
                campos[1],
                campos[2],
                campos[3].toInt(),
                campos[4].toDouble()
            )
        }
    }

    fun buscar(id: Int): Periferico? = listar().find { it.idPeriferico == id }

    @Synchronized
    fun guardar(periferico: Periferico) {
        val perifericos = listar().toMutableList()

        if (periferico.idPeriferico == 0) {
            periferico.idPeriferico = (perifericos.maxOfOrNull { it.idPeriferico } ?: 0) + 1
            perifericos.add(periferico)
        } else {
            val posicion = perifericos.indexOfFirst { it.idPeriferico == periferico.idPeriferico }
            require(posicion >= 0) { "No existe ese periférico" }
            perifericos[posicion] = periferico
        }

        escribir(perifericos)
    }

    @Synchronized
    fun borrar(id: Int) {
        escribir(listar().filter { it.idPeriferico != id })
    }

    private fun escribir(perifericos: List<Periferico>) {
        val filas = perifericos.map {
            listOf(
                it.idPeriferico.toString(), it.modelo, it.marca,
                it.dpiMaximo.toString(), it.pesoGramos.toString()
            )
        }
        val archivo: File = ruta.toFile()
        csvWriter { delimiter = ';' }.writeAll(filas, archivo)
    }
}

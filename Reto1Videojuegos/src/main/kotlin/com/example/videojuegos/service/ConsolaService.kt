package com.example.videojuegos.service

/** @author Constantin Laurentiu David
 * Entidad: Consola
 */

import com.example.videojuegos.model.Consola
import com.example.videojuegos.repository.ConsolaRepository
import org.springframework.stereotype.Service

@Service
class ConsolaService(private val repository: ConsolaRepository) {
    fun listar(): List<Consola> = repository.listar()
    fun buscar(id: Int): Consola? = repository.buscar(id)
    fun guardar(consola: Consola) = repository.guardar(consola)
    fun borrar(id: Int) = repository.borrar(id)
}

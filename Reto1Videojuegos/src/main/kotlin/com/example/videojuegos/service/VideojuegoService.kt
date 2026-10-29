package com.example.videojuegos.service

/** @author Mario Handarici
 * Entidad: Videojuegos
 */

import com.example.videojuegos.model.Videojuego
import com.example.videojuegos.repository.VideojuegoRepository
import org.springframework.stereotype.Service

@Service
class VideojuegoService(private val repository: VideojuegoRepository) {
    fun listar(): List<Videojuego> = repository.listar()
    fun buscar(id: Int): Videojuego? = repository.buscar(id)
    fun guardar(videojuego: Videojuego) = repository.guardar(videojuego)
    fun borrar(id: Int) = repository.borrar(id)
}

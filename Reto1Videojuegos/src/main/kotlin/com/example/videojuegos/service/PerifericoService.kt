package com.example.videojuegos.service

import com.example.videojuegos.model.Periferico
import com.example.videojuegos.repository.PerifericoRepository
import org.springframework.stereotype.Service

/**
 * @author Jesús Blázquez
 * Entidad: Periféricos
 */
@Service
class PerifericoService(private val repository: PerifericoRepository) {
    fun listar(): List<Periferico> = repository.listar()
    fun buscar(id: Int): Periferico? = repository.buscar(id)
    fun guardar(periferico: Periferico) = repository.guardar(periferico)
    fun borrar(id: Int) = repository.borrar(id)
}

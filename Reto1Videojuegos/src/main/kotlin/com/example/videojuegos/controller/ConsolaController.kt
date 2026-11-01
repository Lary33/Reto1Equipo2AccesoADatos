package com.example.videojuegos.controller

/** @author Constantin Laurentiu David
 * Entidad: Consola
 */

import com.example.videojuegos.model.Consola
import com.example.videojuegos.service.ConsolaService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping

@Controller
class ConsolaController(private val service: ConsolaService) {
    @GetMapping("/consolas")
    fun listar(model: Model): String {
        model.addAttribute("consolas", service.listar())
        return "consolas"
    }

    @GetMapping("/consolas/{id}")
    fun detalle(@PathVariable id: Int, model: Model): String {
        val consola = service.buscar(id) ?: return "errorConsola"
        model.addAttribute("consola", consola)
        return "detalleConsola"
    }

    @GetMapping("/consolas/nuevo")
    fun nuevo(model: Model): String {
        model.addAttribute("consola", Consola(0, "", "", 0, 0.0))
        model.addAttribute("tituloPagina", "Nueva consola")
        return "formConsola"
    }

    @GetMapping("/consolas/editar/{id}")
    fun editar(@PathVariable id: Int, model: Model): String {
        val consola = service.buscar(id) ?: return "errorConsola"
        model.addAttribute("consola", consola)
        model.addAttribute("tituloPagina", "Editar consola")
        return "formConsola"
    }

    @PostMapping("/consolas/guardar")
    fun guardar(
        @ModelAttribute("consola") consola: Consola,
        resultado: BindingResult,
        model: Model
    ): String {
        if (resultado.hasErrors() || consola.nombre.isBlank() || consola.fabricante.isBlank() ||
            consola.generacion <= 0 || consola.ventas < 0 ||
            ';' in consola.nombre || ';' in consola.fabricante
        ) {
            model.addAttribute("tituloPagina", "Revisar consola")
            model.addAttribute("error", "Comprueba los datos. El nombre y el fabricante no pueden contener punto y coma.")
            return "formConsola"
        }
        if (consola.idConsola != 0 && service.buscar(consola.idConsola) == null) {
            return "errorConsola"
        }
        service.guardar(consola)
        return "redirect:/consolas"
    }

    @PostMapping("/consolas/borrar/{id}")
    fun borrar(@PathVariable id: Int): String {
        service.borrar(id)
        return "redirect:/consolas"
    }
}

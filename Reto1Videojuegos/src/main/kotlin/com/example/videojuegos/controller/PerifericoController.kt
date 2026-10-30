package com.example.videojuegos.controller

import com.example.videojuegos.model.Periferico
import com.example.videojuegos.service.PerifericoService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping

/**
 * @author Jesús Blázquez
 * Entidad: Periféricos
 */
@Controller
class PerifericoController(private val service: PerifericoService) {

    @GetMapping("/perifericos")
    fun listar(model: Model): String {
        model.addAttribute("perifericos", service.listar())
        return "perifericos"
    }

    @GetMapping("/perifericos/{id}")
    fun detalle(@PathVariable id: Int, model: Model): String {
        val periferico = service.buscar(id) ?: return "errorPeriferico"
        model.addAttribute("periferico", periferico)
        return "detallePeriferico"
    }

    @GetMapping("/perifericos/nuevo")
    fun nuevo(model: Model): String {
        model.addAttribute("periferico", Periferico())
        model.addAttribute("tituloPagina", "Nuevo periférico")
        return "formPeriferico"
    }

    @GetMapping("/perifericos/editar/{id}")
    fun editar(@PathVariable id: Int, model: Model): String {
        val periferico = service.buscar(id) ?: return "errorPeriferico"
        model.addAttribute("periferico", periferico)
        model.addAttribute("tituloPagina", "Editar periférico")
        return "formPeriferico"
    }

    @PostMapping("/perifericos/guardar")
    fun guardar(
        @ModelAttribute("periferico") periferico: Periferico,
        resultado: BindingResult,
        model: Model
    ): String {
        if (resultado.hasErrors() || periferico.modelo.isBlank() || periferico.marca.isBlank() ||
            periferico.dpiMaximo <= 0 || periferico.pesoGramos <= 0 ||
            ';' in periferico.modelo || ';' in periferico.marca
        ) {
            model.addAttribute("tituloPagina", "Revisar periférico")
            model.addAttribute("error", "Comprueba los datos del periférico.")
            return "formPeriferico"
        }

        if (periferico.idPeriferico != 0 && service.buscar(periferico.idPeriferico) == null) {
            return "errorPeriferico"
        }

        service.guardar(periferico)
        return "redirect:/perifericos"
    }

    @PostMapping("/perifericos/borrar/{id}")
    fun borrar(@PathVariable id: Int): String {
        service.borrar(id)
        return "redirect:/perifericos"
    }
}

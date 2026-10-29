package com.example.videojuegos.controller

import com.example.videojuegos.model.Videojuego
import com.example.videojuegos.service.VideojuegoService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping

@Controller
class VideojuegoController(private val service: VideojuegoService) {
    @GetMapping("/videojuegos")
    fun listar(model: Model): String {
        model.addAttribute("videojuegos", service.listar())
        return "videojuegos"
    }

    @GetMapping("/videojuegos/{id}")
    fun detalle(@PathVariable id: Int, model: Model): String {
        val videojuego = service.buscar(id) ?: return "errorVideojuego"
        model.addAttribute("videojuego", videojuego)
        return "detalleVideojuego"
    }

    @GetMapping("/videojuegos/nuevo")
    fun nuevo(model: Model): String {
        model.addAttribute("videojuego", Videojuego())
        model.addAttribute("tituloPagina", "Nuevo videojuego")
        return "formVideojuego"
    }

    @GetMapping("/videojuegos/editar/{id}")
    fun editar(@PathVariable id: Int, model: Model): String {
        val videojuego = service.buscar(id) ?: return "errorVideojuego"
        model.addAttribute("videojuego", videojuego)
        model.addAttribute("tituloPagina", "Editar videojuego")
        return "formVideojuego"
    }

    @PostMapping("/videojuegos/guardar")
    fun guardar(
        @ModelAttribute("videojuego") videojuego: Videojuego,
        resultado: BindingResult,
        model: Model
    ): String {
        if (resultado.hasErrors() || videojuego.titulo.isBlank() || videojuego.estudio.isBlank() ||
            videojuego.lanzamiento <= 0 || videojuego.precio < 0 ||
            ';' in videojuego.titulo || ';' in videojuego.estudio
        ) {
            model.addAttribute("tituloPagina", "Revisar videojuego")
            model.addAttribute("error", "Comprueba los datos. El título y el estudio no pueden contener punto y coma.")
            return "formVideojuego"
        }
        if (videojuego.idVideojuego != 0 && service.buscar(videojuego.idVideojuego) == null) {
            return "errorVideojuego"
        }
        service.guardar(videojuego)
        return "redirect:/videojuegos"
    }

    @PostMapping("/videojuegos/borrar/{id}")
    fun borrar(@PathVariable id: Int): String {
        service.borrar(id)
        return "redirect:/videojuegos"
    }
}

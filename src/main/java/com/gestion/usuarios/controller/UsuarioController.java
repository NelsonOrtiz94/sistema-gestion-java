package com.gestion.usuarios.controller;

import com.gestion.usuarios.entity.Usuario;
import com.gestion.usuarios.service.UsuarioService;
import com.gestion.usuarios.util.paginacion.PageRender;
import com.gestion.usuarios.util.reportes.UsuarioExporterExcel;
import com.gestion.usuarios.util.reportes.UsuarioExporterPDF;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }


    @GetMapping("/ver/{id}")
    public String verDetallesDelUsuario(@PathVariable(value = "id") Long id, Map<String, Object> modelo, RedirectAttributes flash) {
        Usuario usuario = (Usuario) usuarioService.findOne(id);
        if (usuario == null) {
            flash.addAttribute("error", "El usuario no existe en la base de datos");
            return "redirect:/listar";
        }

        modelo.put("usuario", usuario);
        modelo.put("titulo", "Detalles del usuario " + usuario.getNombre());
        return "ver";
    }

    @GetMapping({"/", "/listar", ""})
    public String listarUsuarios(@RequestParam(name = "page", defaultValue = "0") int page, Model modelo) {
        Pageable pageRequest = PageRequest.of(page, 5);
        Page<Usuario> usuarios = usuarioService.findAll(pageRequest);
        PageRender<Usuario> pageRender = new PageRender<>("/listar", usuarios);

        modelo.addAttribute("titulo", "Listado de usuarios");
        modelo.addAttribute("usuarios", usuarios);
        modelo.addAttribute("page", pageRender);

        // Corregir la ruta de la vista para que coincida con la ubicación de la plantilla
        return "/listar";
    }

    @GetMapping({"/form"})
    public String mostrarFormularioDeRegistrarUsuario(Map<String, Object> modelo) {
        Usuario usuario = new Usuario();
        modelo.put("usuario", usuario);
        modelo.put("titulo", "Registro de usuario");
        return "form";
    }

    @PostMapping("/form")
    public String guardarUsuario(@Valid Usuario usuario, BindingResult result, Model modelo, RedirectAttributes flash, SessionStatus status) {
        if (result.hasErrors()) {
            modelo.addAttribute("titulo", "Registro de usuario");
            return "form";
        }

        String mensaje = (usuario.getId() != null) ? "El usuario ha sido editado con exito" : "Usuario registrado con exito";

        usuarioService.save(usuario);
        status.setComplete();
        flash.addAttribute("success", mensaje);
        return "redirect:/listar";
    }

    @GetMapping("/form/{id}")
    public String editarUsuario(@PathVariable(value = "id") Long id, Map<String, Object> modelo, RedirectAttributes flash) {
        Usuario usuario = null;
        if (id > 0) {
            usuario = (Usuario) usuarioService.findOne(id);
            if (usuario == null) {
                flash.addAttribute("error", "El ID del usuario no existe en la base de datos");
                return "redirect:/listar";
            }
        } else {
            flash.addAttribute("error", "El ID del usuario no puede ser cero");
            return "redirect:/listar";
        }

        modelo.put("usuario", usuario);
        modelo.put("titulo", "Editar usuario");
        return "form";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable(value = "id") Long id, RedirectAttributes flash) {
        if (id > 0) {
            usuarioService.delete(id);
            flash.addAttribute("success", "Usuario eliminado con exito");
        }
        return "redirect:/listar";
    }

    @GetMapping("/exportarPDF")
    public void exportarListadoDeUsuariosEnPDF(HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String fechaActual = dateFormatter.format(new Date());

        String cabecera = "Content-Disposition";
        String valor = "attachment; filename=Usuarios_" + fechaActual + ".pdf";

        response.setHeader(cabecera, valor);

        List<Usuario> usuarios = usuarioService.findAll();

        UsuarioExporterPDF exporter = new UsuarioExporterPDF(usuarios);
        exporter.exportar(response);
    }

    @GetMapping("/exportarExcel")
    public void exportarListadoDeUsuariosEnExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");

        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String fechaActual = dateFormatter.format(new Date());

        String cabecera = "Content-Disposition";
        String valor = "attachment; filename=Usuarios_" + fechaActual + ".xlsx";

        response.setHeader(cabecera, valor);

        List<Usuario> usuarios = usuarioService.findAll();

        UsuarioExporterExcel exporter = new UsuarioExporterExcel(usuarios);
        exporter.exportar(response);
    }
}

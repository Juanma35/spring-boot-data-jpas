package com.bolsadeideas.springboot.app.controllers;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import com.bolsadeideas.springboot.app.models.entity.inventario_rack;
import com.bolsadeideas.springboot.app.models.service.IEquiposService;

@Controller
public class InventarioRackController {
	
	@Autowired
	public IEquiposService inventarioService;
	
	@RequestMapping(value = "/listarIn", method = RequestMethod.GET)
	public String listar(Model model) {

		model.addAttribute("titulo", "listado de Equipo en Red");
		model.addAttribute("inventario", inventarioService.findAll());

		// DEVUELVE LOS DATOS QUE ENVIA A LA VISTA listarInventario.html
		return "listarInventarioRack";// html
	}

	// METODOS QUE SACA LOS FORMULARIOS

	@RequestMapping(value = "/formIn") // url
	public String crear(Map<String, Object> model) {

		inventario_rack inventario = new inventario_rack();
		model.put("inventario", inventario);
		model.put("titulo", "Crear Equipo para inventariar");

		// SACA EL FORMULARIO Y LO ENVIA A LA VISTA formArticulo

		return "formInventarioRack";// html
	}
	
	@RequestMapping(value = "/formIn", method = RequestMethod.POST)//url
	public String guardar(@Valid inventario_rack inventarios, BindingResult result, Model model, RedirectAttributes flash, SessionStatus status) {

		if (result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de Inventario");
			
			
			return "formIn";//HTML
		}
	String mensajeFlash = (inventarios.getId() != null)? "Equipo editado con éxito!" : "Cliente creado con éxito!";

	inventarioService.save(inventarios);
		status.setComplete();
		flash.addFlashAttribute("success", mensajeFlash);
		return "redirect:listarIn";//url
	}
	

}

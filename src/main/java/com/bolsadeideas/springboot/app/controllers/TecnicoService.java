package com.bolsadeideas.springboot.app.controllers;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import com.bolsadeideas.springboot.app.models.entity.Tecnico;
import com.bolsadeideas.springboot.app.models.service.Tecnico.ITecnicoService;

@Controller
@SessionAttributes("tecnico")
public class TecnicoService {
	
	@Autowired
	private ITecnicoService tecnicoService;
	
	
	//	METODO QUE LISTA LOS RESULTADOS DE LA BSE DE DATOS
	
	@RequestMapping(value ="/listarT",method =RequestMethod.GET)//url
	public String listar(Model model){
		
		
		model.addAttribute("titulo","listado de Informaticos en plantilla");
		model.addAttribute("tecnico",tecnicoService.findAll());
		
		//DEVUELVE LOS DATOS QUE ENVIA A LA VISTA listarTecnico.html
		return "listarTecnico";//html		
			
	}
	
	//METODOS QUE SACA LOS FORMULARIOS
	
	@RequestMapping(value = "/formT")//url
	public String crear(Map<String, Object> model) {

		Tecnico tecnico= new Tecnico();
		model.put("tecnico", tecnico);
		model.put("titulo", "Crear Tecnico");
		
		//SACA EL FORMULARIO Y LO ENVIA A LA VISTA formTecnico
		
		return "formTecnico";//html
	}
	
	//METODOS QUE GUARDA LOS DATOS DEL FORMULARIO
	
	@RequestMapping(value = "/formT", method = RequestMethod.POST)
	public String guardar(@Valid Tecnico tecnico, BindingResult result, Model model, RedirectAttributes flash, SessionStatus status) {

		if (result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de Tecnico");
			
			//****SI HAY UN ERROR DEVUELVE EL FORMULARIO CON CASILLA QUE TIENE ERROR 
			
			return "formTecnico";//HTML
		}
		String mensajeFlash = (tecnico.getId() != null)? "Tecnico editado con éxito!" : "Tecnio creado con éxito!";

		//SI TODO ES CORRECTO GUARDA LOS DATOS EN LA BBDD
		tecnicoService.save(tecnico);
		status.setComplete();
		flash.addFlashAttribute("success", mensajeFlash);
		
		//DEVUELVE LA LISTA DE DATOS QUE ACUDE ALA METODO ListarT
		return "redirect:/listarT";
	}
	
	//METODO QUE RECOGE EL ID DEL TECNICO PARA PODER MODIFICAR Y LOS GUARDA
	
	@RequestMapping(value="/formT/{id}")//url
	public String editar(@PathVariable(value="id") Long id, Map<String, Object> model, RedirectAttributes flash) {
		
		Tecnico tecnico = null;
		
		if(id > 0) {
			
			//RECOGE EL ID Y MUESTRA EL FORMULARIO CON LOS DATOS RECOGIDOS 
			tecnico = tecnicoService.findOne(id);
			if(tecnico == null) {
				
				//SI HAY UN ERROR VUELVE AL LISTADO Y SALTA UN ERROR
				flash.addFlashAttribute("error", "El ID del tecnico  no existe en la BBDD!");
				return "redirect:/listarT";//url
			}
		} else {
			flash.addFlashAttribute("error", "El ID del tecnico no puede ser cero!");
			return "redirect:/listarT";//url
		}
		//SI TODO ES CORRECTO GUARDA LOS DATOS Y MUESTRA LA LISTA ACTUALIZDA CON LOS DATOS
		
		model.put("tecnico", tecnico);
		model.put("titulo", "Editar Cliente");
		return "formTecnico";//html
	}

	// METODO QUE ACTUA SOBRE EL BOTON ELIMINAR SOBRE EL ID Y ELIMINA LA INFORMACION DEVUELVE LA LISTA ACTUALIZADA
	@RequestMapping(value="/eliminarT/{id}")//url
	public String eliminar(@PathVariable(value="id") Long id, RedirectAttributes flash) {
		
		if(id > 0) {
			
			//BORRA LOS DATOS
			tecnicoService.delete(id);
			flash.addFlashAttribute("success", "Tecnico eliminado con éxito!");
		}
		//DEVUELVE LA LISTA ACTUALIZDA
		return "redirect:/listarT";//url
	}
	
	
}

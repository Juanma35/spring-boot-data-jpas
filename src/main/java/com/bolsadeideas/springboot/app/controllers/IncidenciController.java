package com.bolsadeideas.springboot.app.controllers;

import java.time.LocalDate;
import java.util.List;
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

import com.bolsadeideas.springboot.app.models.entity.Cliente;
import com.bolsadeideas.springboot.app.models.entity.Incidenci;

import com.bolsadeideas.springboot.app.models.entity.Servicio;
import com.bolsadeideas.springboot.app.models.entity.Tecnico;
import com.bolsadeideas.springboot.app.models.service.IClienteService;
import com.bolsadeideas.springboot.app.models.service.IIncidenciaService;
import com.bolsadeideas.springboot.app.models.service.IServicioService;
import com.bolsadeideas.springboot.app.models.service.Tecnico.ITecnicoService;

@Controller
public class IncidenciController {

	
	@Autowired
	private ITecnicoService tecnicoService;	
	
	@Autowired
	private IIncidenciaService incidenciaService;
	
	@Autowired
	private IServicioService servicioService;
	
	@Autowired
	private IClienteService clienteService;


	
	@RequestMapping(value= "/listarI" ,method = RequestMethod.GET)
	public String listar(Model model){
		

			
			model.addAttribute("titulo","listado de Incidencia");
			model.addAttribute("incidenci",incidenciaService.findAll());
			
			//DEVUELVE LOS DATOS QUE ENVIA A LA VISTA listarTecnico.html
			return "listarIncidencia";//html	
		}
		
		//METODOS QUE SACA LOS FORMULARIOS
		
		@RequestMapping(value = "/formI")//url
			public String crear(Map<String, Object> model) {

				Incidenci incidenci= new Incidenci();
				
				//nos trae la lista de tecnico a comboox a la vista
				List<Tecnico> list = tecnicoService.findAll();
				((Model) model).addAttribute("tecnicos", list);
				
				//nos trae la lista de tecnico a comboox a la vista
				List<Servicio> listS = servicioService.findAll();
				((Model) model).addAttribute("servicio", listS);
				
				//nos trae la lista de clientes a comboox a la vista
				List<Cliente> listC = clienteService.findAll();
				((Model) model).addAttribute("cliente", listC);
				
				
				//obtenemos fecha local
				((Model) model).addAttribute("fecha", LocalDate.now());
				
				model.put("incidenci",incidenci);
				model.put("titulo", "Crear Incidencia");
				
				//SACA EL FORMULARIO Y LO ENVIA A LA VISTA formTecnico
				
				return "formIncidencia";//html
			}
	
		
			//METODOS QUE GUARDA LOS DATOS DEL FORMULARIO
			
			@RequestMapping(value = "/formI", method = RequestMethod.POST)
			public String guardar(@Valid Incidenci incidenci, BindingResult result, Model model, RedirectAttributes flash, SessionStatus status) {

				if (result.hasErrors()) {
					model.addAttribute("titulo", "Formulario de Gestion de Incidencias");
					
					//****SI HAY UN ERROR DEVUELVE EL FORMULARIO CON CASILLA QUE TIENE ERROR 
					
					return "formIncidencia";//HTML
				}
				String mensajeFlash = (incidenci.getId() != null)? "Incidencia editada con éxito!" : "Incidencia creada con éxito!";

				//SI TODO ES CORRECTO GUARDA LOS DATOS EN LA BBDD
				incidenciaService.save(incidenci);
				status.setComplete();
				flash.addFlashAttribute("success", mensajeFlash);
				
				//DEVUELVE LA LISTA DE DATOS QUE ACUDE ALA METODO ListarT
				return "redirect:/listarI";
			}
	
}

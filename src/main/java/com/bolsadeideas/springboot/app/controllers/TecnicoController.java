package com.bolsadeideas.springboot.app.controllers;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.bolsadeideas.springboot.app.models.entity.Servicio;
import com.bolsadeideas.springboot.app.util.paginator.PageRender;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import com.bolsadeideas.springboot.app.models.entity.Tecnico;
import com.bolsadeideas.springboot.app.models.service.Tecnico.ITecnicoService;

@Controller
@SessionAttributes("tecnico")
public class TecnicoController {
	
	@Autowired
	private ITecnicoService tecnicoService;

	protected final Log logger = LogFactory.getLog(this.getClass());
	//	METODO QUE LISTA LOS RESULTADOS DE LA BSE DE DATOS

	@Autowired
	private MessageSource messageSource;

/**@RequestMapping(value ="/listarTe",method =RequestMethod.GET)//url
	public String listar(Model model){
		
		
		model.addAttribute("titulo","listado de Informaticos en plantilla");
		model.addAttribute("tecnico",tecnicoService.findAll());
		
		//DEVUELVE LOS DATOS QUE ENVIA A LA VISTA listarTecnico.html
		return "listarTecnico";//html		
			
	}**/


	@RequestMapping(value = {"/listarT"}, method = RequestMethod.GET)
	public String listartec(@RequestParam(name = "page", defaultValue = "0") int page, Model model,
						 Authentication authentication,
						 HttpServletRequest request,
						 Locale locale) {

		if(authentication != null) {
			logger.info("Hola usuario autenticado, tu username es: ".concat(authentication.getName()));
		}

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if(auth != null) {
			logger.info("Utilizando forma estática SecurityContextHolder.getContext().getAuthentication(): Usuario autenticado: ".concat(auth.getName()));
		}

		if(hasRole("ROLE_ADMIN")) {
			logger.info("Hola ".concat(auth.getName()).concat(" tienes acceso!"));
		} else {
			logger.info("Hola ".concat(auth.getName()).concat(" NO tienes acceso!"));
		}

		SecurityContextHolderAwareRequestWrapper securityContext = new SecurityContextHolderAwareRequestWrapper(request, "");

		if(securityContext.isUserInRole("ROLE_ADMIN")) {
			logger.info("Forma usando SecurityContextHolderAwareRequestWrapper: Hola ".concat(auth.getName()).concat(" tienes acceso!"));
		} else {
			logger.info("Forma usando SecurityContextHolderAwareRequestWrapper: Hola ".concat(auth.getName()).concat(" NO tienes acceso!"));
		}

		if(request.isUserInRole("ROLE_ADMIN")) {
			logger.info("Forma usando HttpServletRequest: Hola ".concat(auth.getName()).concat(" tienes acceso!"));
		} else {
			logger.info("Forma usando HttpServletRequest: Hola ".concat(auth.getName()).concat(" NO tienes acceso!"));
		}

		Pageable pageRequest = PageRequest.of(page, 6);

		Page<Tecnico> tecnicos = tecnicoService.findAll(pageRequest);

		PageRender<Tecnico> pageRender = new PageRender<Tecnico>("/listarT", tecnicos);
		model.addAttribute("titulo", messageSource.getMessage("text.tecnico.listar.titulo", null, locale));
		model.addAttribute("tecnico", tecnicos);
		model.addAttribute("page", pageRender);
		return "listarTecnico";
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
			
			//BORRAR LOS DATOS
			tecnicoService.delete(id);
			flash.addFlashAttribute("success", "Tecnico eliminado con éxito!");
		}
		//DEVUELVE LA LISTA ACTUALIZDA
		return "redirect:/listarT";//url
	}
	private boolean hasRole(String role) {

		SecurityContext context = SecurityContextHolder.getContext();

		if(context == null) {
			return false;
		}

		Authentication auth = context.getAuthentication();

		if(auth == null) {
			return false;
		}

		Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

		return authorities.contains(new SimpleGrantedAuthority(role));

	}
	
	
}

package com.bolsadeideas.springboot.app.controllers;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bolsadeideas.springboot.app.models.entity.Servicio;
import com.bolsadeideas.springboot.app.models.entity.Tecnico;
import com.bolsadeideas.springboot.app.models.service.IServicioService;
import com.bolsadeideas.springboot.app.models.service.Tecnico.ITecnicoService;
import com.bolsadeideas.springboot.app.util.paginator.PageRender;

@Controller
@SessionAttributes("servicio")
public class ServiceController {
	

	protected final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	private ITecnicoService tecnicoService;
	
	@Autowired
	private IServicioService servicioService;
	
	@Autowired
	private MessageSource messageSource;
	
	//metodo rest
	@GetMapping(value = "/listar-RestS")//url
	public @ResponseBody List<Servicio> listarRest() {
		
				return servicioService.findAll();//html
	}
	
	
	
	@RequestMapping(value = {"/listarS", "/"}, method = RequestMethod.GET)
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model,
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

		Page<Servicio> servicio = servicioService.findAll(pageRequest);
		
		PageRender<Servicio> pageRender = new PageRender<Servicio>("/listarS", servicio);
		model.addAttribute("titulo", messageSource.getMessage("text.cliente.listar.titulo", null, locale));
		model.addAttribute("servicio", servicio);
		model.addAttribute("page", pageRender);
		return "listarServicios";
	}
	
	
	
	@RequestMapping(value = "/formS")//url
	public String crear(Map<String, Object> model) {

		Servicio servicio = new Servicio();
		List<Tecnico> list = tecnicoService.findAll();
		model.put("servicio", servicio);
		((Model) model).addAttribute("tecnicos", list);
		
		model.put("titulo", "Crear servicio");
		 
		return "formServicios";//html
	}
	
	
	
	
	
	@RequestMapping(value="/formS/{id}")//url
	public String editar(@PathVariable(value="id") Long id, Map<String, Object> model, RedirectAttributes flash) {
		
		Servicio servicio = null;
		
		if(id > 0) {
			servicio = servicioService.findOne(id);
			if(servicio == null) {
				flash.addFlashAttribute("error", "El ID del cliente no existe en la BBDD!");
				return "redirect:/listarS";//url
			}
		} else {
			flash.addFlashAttribute("error", "El ID del cliente no puede ser cero!");
			return "redirect:/listarS";//url
		}
		model.put("servicio", servicio);
		model.put("titulo", "Editar Cliente");
		return "formServicios";//html
	}

	@RequestMapping(value = "/formS", method = RequestMethod.POST)//url
	public String guardar(@Valid Servicio servicio, BindingResult result, Model model, RedirectAttributes flash, SessionStatus status) {

		if (result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de Cliente");
			
			
			return "formServicios";//HTML
		}
	String mensajeFlash = (servicio.getId() != null)? "Cliente editado con éxito!" : "Cliente creado con éxito!";

		servicioService.save(servicio);
		status.setComplete();
		flash.addFlashAttribute("success", mensajeFlash);
		return "redirect:listarS";//url
	}
	
	
	
	// METODO QUE ACTUA SOBRE EL BOTON ELIMINAR SOBRE EL ID Y ELIMINA LA INFORMACION DEVUELVE LA LISTA ACTUALIZADA
	@RequestMapping(value="/eliminarS/{id}")//url
	public String eliminar(@PathVariable(value="id") Long id, RedirectAttributes flash) {
		
		if(id > 0) {
			
			//BORRA LOS DATOS
			servicioService.delete(id);
			flash.addFlashAttribute("success", "Servicio eliminado con éxito!");
		}
		//DEVUELVE LA LISTA ACTUALIZDA
		return "redirect:/listarS";//url
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

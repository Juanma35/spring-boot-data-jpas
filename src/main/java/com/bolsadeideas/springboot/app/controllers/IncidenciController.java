package com.bolsadeideas.springboot.app.controllers;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.bolsadeideas.springboot.app.util.paginator.PageRender;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

	protected final Log logger = LogFactory.getLog(this.getClass());
	
	@RequestMapping(value= "/listarI" ,method = RequestMethod.GET)
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page,Model model,
						 Authentication authentication,
						 HttpServletRequest request,
						 Locale locale){



		Pageable pageRequest = PageRequest.of(page, 4);

		Page<Incidenci> incidenci = incidenciaService.findAll(pageRequest);

		PageRender<Incidenci> pageRender = new PageRender<Incidenci>("/listarIncidencia", incidenci);

		model.addAttribute("titulo","listado de Incidencia");
		model.addAttribute("incidenci",incidenci);
		model.addAttribute("page", pageRender);
			//DEVUELVE LOS DATOS QUE ENVIA A LA VISTA listarTecnico.html
			return "listarIncidencia";//html	
		}
		
		//METODOS QUE SACA LOS FORMULARIOS

	@Secured("ROLE_ADMIN")
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





				model.put("incidenci",incidenci);
				model.put("titulo", "Crear Incidencia");
				
				//SACA EL FORMULARIO Y LO ENVIA A LA VISTA formTecnico
				
				return "formIncidencia";//html
			}
	
		
			//METODOS QUE GUARDA LOS DATOS DEL FORMULARIO

			@Secured("ROLE_ADMIN")
			@RequestMapping(value = "/formI", method = RequestMethod.POST)
			public String guardar(@Valid Incidenci incidenci, BindingResult result, Model model, RedirectAttributes flash, SessionStatus status) {

				if (result.hasErrors()) {
					model.addAttribute("titulo", "Formulario de Gestion de Incidencias");
					
					//****SI HAY UN ERROR DEVUELVE EL FORMULARIO CON CASILLA QUE TIENE ERROR 
					
					return "formIncidencia";//HTML
				}

				String mensajeFlash = (incidenci.getId() != null) ? "Incidencia editada con éxito!" : "Incidencia creada con éxito!";

				//SI TODO ES CORRECTO GUARDA LOS DATOS EN LA BBDD
				incidenciaService.save(incidenci);
				status.setComplete();
				flash.addFlashAttribute("success", mensajeFlash);
				
				//DEVUELVE LA LISTA DE DATOS QUE ACUDE ALA METODO ListarT
				return "redirect:/listarI";

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

package com.bolsadeideas.springboot.app.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import com.bolsadeideas.springboot.app.models.entity.Producto;

import com.bolsadeideas.springboot.app.models.service.IProductoService;

@Controller
@SessionAttributes("articulo")
public class ArticuloController {

	@Autowired
	private IProductoService articuloService;

	// mostrar por consola y un debug de los archivos
	private final Logger log = LoggerFactory.getLogger(getClass());

	// Cramos una cosantes de uploads

	private static final String UPLOADS_FOLDER = "uploads";

	// Le pasamos un PathVariable con el id de la clase artículos

	@GetMapping(value = "/verA/{id}")
	public String ver(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {

		// envocamos el metodo findOne del servicio de articulos para poder
		// obtener el articulo y nos devuelve el id correspondiente
		Producto articulo = articuloService.findOne(id);

		// si articulo es null redirecciona a la lista de nuevo
		if (articulo == null) {
			flash.addFlashAttribute("error", "El articulo no exite en la base de datos");

			return "redirect:/listarArticulo";
		}

		// Si todo esta bien nos muestra la foto con los datos del articulo
		// creamos una model para llevar a la vista la clase articulo y de este
		// modo poder recoger datos
		model.put("articulo", articulo);
		model.put("titulo", "Detalle del articulo " + articulo.getNombre());

		return "ver";

	}

	@RequestMapping(value = "/listarA", method = RequestMethod.GET)
	public String listar(Model model) {

		model.addAttribute("titulo", "listado de Articulos en Stock");
		model.addAttribute("articulo", articuloService.findAll());

		// DEVUELVE LOS DATOS QUE ENVIA A LA VISTA listarTecnico.html
		return "listarArticulo";// html
	}

	// METODOS QUE SACA LOS FORMULARIOS

	@RequestMapping(value = "/formA") // url
	public String crear(Map<String, Object> model) {

		Producto articulo = new Producto();
		model.put("articulo", articulo);
		model.put("titulo", "Crear Articulo");

		// SACA EL FORMULARIO Y LO ENVIA A LA VISTA formArticulo

		return "formArticulo";// html
	}

	// METODOS QUE GUARDA LOS DATOS DEL FORMULARIO

	@RequestMapping(value = "/formA", method = RequestMethod.POST)
	public String guardar(@Valid Producto articulo, BindingResult result, Model model,
			@RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status) {

		if (result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de Articulos");

			return "formArticulo";
		}
		// comprobaciones para añadir una foto despues de borrado

		if (!foto.isEmpty()) {

			// podemos eliminar la foto antigua
			if (articulo.getId() != null && articulo.getId() > 0 && articulo.getFoto() != null
					&& articulo.getFoto().length() > 0) {

				// pero hay que validar que la foto
				// obtenos el rootPaht y la direccion
				Path rootPath = Paths.get(UPLOADS_FOLDER).resolve(articulo.getFoto()).toAbsolutePath();
				// obtenemos el nombre del archivo
				File archivo = rootPath.toFile();

				// validamos si archivo existe
				if (archivo.exists() && archivo.canRead()) {
					// elimina archivo
					if (archivo.delete()) {
						flash.addFlashAttribute("info", "Foto" + articulo.getFoto() + "eliminado con éxito!");
					}
				}

			}

			String uniqueFilename = UUID.randomUUID().toString() + "_" + foto.getOriginalFilename();
			// definimos cual va a ser nuestro directorio en nuestro ordenador o
			// servidor

			Path rootPath = Paths.get(UPLOADS_FOLDER).resolve(uniqueFilename);
			Path rootAbsolutPath = rootPath.toAbsolutePath();

			// path relativo ala proyecto es el nombre del archivo
			log.info("rootPath:  " + rootPath);
			// path absoloto hasta nombre del proyecto y nombre del archivo
			log.info("rootAbsolutPath:  " + rootAbsolutPath);

			try {

				Files.copy(foto.getInputStream(), rootAbsolutPath);
				// añadimos un mensaje con flash
				flash.addFlashAttribute("info", "Ha subido correctamente '" + uniqueFilename + "'");

				// hay que pasar el nombre de la foto al cliente para quede
				// guardada en la base de datos
				// y poder recuperar la foto
				articulo.setFoto(uniqueFilename);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		String mensajeFlash = (articulo.getId() != null) ? "Articulo editado con éxito!" : "Articulo creado con éxito!";

		// SI TODO ES CORRECTO GUARDA LOS DATOS EN LA BBDD
		articuloService.save(articulo);
		status.setComplete();
		flash.addFlashAttribute("success", mensajeFlash);

		// DEVUELVE LA LISTA DE DATOS QUE ACUDE ALA METODO ListarA
		return "redirect:/listarA";
	}

	// METODO QUE RECOGE EL ID DEL TECNICO PARA PODER MODIFICAR Y LOS GUARDA
	@RequestMapping(value = "/formA/{id}") // url
	public String editar(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {

		Producto articulo = null;

		if (id > 0) {

			// RECOGE EL ID Y MUESTRA EL FORMULARIO CON LOS DATOS RECOGIDOS
			articulo = articuloService.findOne(id);
			if (articulo == null) {

				// SI HAY UN ERROR VUELVE AL LISTADO Y SALTA UN ERROR
				flash.addFlashAttribute("error", "El ID del tecnico  no existe en la BBDD!");
				return "redirect:/listarA";// url
			}
		} else {
			flash.addFlashAttribute("error", "El ID del tecnico no puede ser cero!");
			return "redirect:/listarA";// url
		}
		// SI TODO ES CORRECTO GUARDA LOS DATOS Y MUESTRA LA LISTA ACTUALIZDA
		// CON LOS DATOS

		model.put("articulo", articulo);
		model.put("titulo", "Editar Articulo");
		return "formArticulo";// html
	}

	// METODO QUE ACTUA SOBRE EL BOTON ELIMINAR SOBRE EL ID Y ELIMINA LA
	// INFORMACION DEVUELVE LA LISTA ACTUALIZADA
	@RequestMapping(value = "/eliminarA/{id}") // url
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash) {

		if (id > 0) {

			Producto articulo = articuloService.findOne(id);

			// BORRA LOS DATOS
			articuloService.delete(id);
			flash.addFlashAttribute("success", "Articulo eliminado con éxito!");

			Path rootPath = Paths.get(UPLOADS_FOLDER).resolve(articulo.getFoto()).toAbsolutePath();
			File archivo = rootPath.toFile();

			if (archivo.exists() && archivo.canRead()) {

				if (archivo.delete()) {
					flash.addFlashAttribute("info", "Foto" + articulo.getFoto() + "eliminado con éxito!");
				}
			}
		}
		// DEVUELVE LA LISTA ACTUALIZDA
		return "redirect:/listarA";// url
	}

}

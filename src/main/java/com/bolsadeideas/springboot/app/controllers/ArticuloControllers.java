package com.bolsadeideas.springboot.app.controllers;

import com.bolsadeideas.springboot.app.models.entity.Producto;
import com.bolsadeideas.springboot.app.models.service.IProductoService;
import com.bolsadeideas.springboot.app.models.service.IUploadFileService;
import com.bolsadeideas.springboot.app.util.paginator.PageRender;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Controller
@SessionAttributes("articulo")
public class ArticuloControllers {

    @Autowired
    private IProductoService articuloService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private IUploadFileService uploadFileService;

    // mostrar por consola y un debug de los archivos
    private final Logger log = LoggerFactory.getLogger(getClass());

    // Cramos una cosantes de uploads
    private static final String UPLOADS_FOLDER = "uploads";

    protected final Log logger = LogFactory.getLog(this.getClass());

    // Le pasamos un PathVariable con el id de la clase artículos


    @Secured({"ROLE_USER"})
    @GetMapping(value = "/uploadsA/{filename:.+}")
    public ResponseEntity<Resource> verFoto(@PathVariable String filename) {

        Resource recurso = null;

        try {
            recurso = uploadFileService.load(filename);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
                .body(recurso);
    }


    @GetMapping(value = "/verA/{id}")
    public String ver(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {

        // envocamos el metodo findOne del servicio de articulos para poder
        // obtener el articulo y nos devuelve el id correspondiente
        Producto articulo = articuloService.findOne(id);

        // si articulo es null redirecciona a la lista de nuevo
        if (articulo == null) {
            flash.addFlashAttribute("error", "El articulo no exite en la base de datos");

            return "redirect:/listarArticulos";
        }

        // Si todo esta bien nos muestra la foto con los datos del articulo
        // creamos una model para llevar a la vista la clase articulo y de este
        // modo poder recoger datos
        model.put("articulo", articulo);
        model.put("titulo", "Detalle del articulo " + articulo.getNombre());

        return "ver";

    }

    @RequestMapping(value = "/listarA", method = RequestMethod.GET)
    public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model, Authentication authentication,
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

        Pageable pageRequest = PageRequest.of(page, 4);

        Page<Producto> articulo = articuloService.findAll(pageRequest);

        //controlar la paginacion /listar vuelve a cagar la lista de articulos en el siguiente reglon
        PageRender<Producto> pageRender = new PageRender<Producto>("/listarA", articulo);
        model.addAttribute("titulo", messageSource.getMessage("text.articulo.listar.titulo", null, locale));
        model.addAttribute("articulo",articulo);
        model.addAttribute("page", pageRender);

        // DEVUELVE LOS DATOS QUE ENVIA A LA VISTA listarTecnico.html
        return "listarArticulos";// html
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
        //  TODO ES CORRECTO GUARDA LOS DATOS Y MUESTRA LA LISTA ACTUALIZDA
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

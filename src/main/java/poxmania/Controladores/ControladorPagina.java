package poxmania.Controladores;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import poxmania.Clases.Producto;
import poxmania.Clases.Usuario;
import poxmania.Repositorios.ProductoCarritoRepository;
import poxmania.Repositorios.ProductoRepository;
import poxmania.Repositorios.UsuarioRepository;

@Controller
public class ControladorPagina {
	@Autowired
	private ProductoRepository repoproductos;
	@Autowired
	private UsuarioRepository repousuarios;
	@Autowired
	private ProductoCarritoRepository repoproductoscarrito;
	private static final String FILES_FOLDER = "src/main/resources/static/imagenes/Productos/";	
	
	@RequestMapping("/")
	public ModelAndView inicio(HttpSession session) {		
		List<Producto> todoslosproductos=(List<Producto>)repoproductos.findAll();
		int i=todoslosproductos.size()-1;
		int contador=6;
		List<Producto> ultimosproductos=new ArrayList<Producto>();
		if (!todoslosproductos.isEmpty()){
			while((i>-1)&&(contador<12)){
				ultimosproductos.add(todoslosproductos.get(i));
				contador++;
				i--;
			}	
			return new ModelAndView("index")
				.addObject("productos",	ultimosproductos);
		}
		else return new ModelAndView("index")
			.addObject("productos", ultimosproductos).addObject("sinproductos", true);
	}
	
	@RequestMapping("/imagenes/Productos/{fileName}")
	public void handleFileDownload(@PathVariable String fileName, HttpServletResponse res) throws FileNotFoundException, IOException {
		File file = new File(FILES_FOLDER, fileName+".jpg");
		if (file.exists()) {
			res.setContentType("image/jpeg");
			res.setContentLength(new Long(file.length()).intValue());
			FileCopyUtils
					.copy(new FileInputStream(file), res.getOutputStream());
		} else {
			res.sendError(404, "File" + fileName + "(" + file.getAbsolutePath()
					+ ") does not exist");
		}
	}
	
	@RequestMapping("/mostrar")
	public ModelAndView mostrar(@RequestParam String id) {
		long idproducto=Long.parseLong(id);
		Producto producto = repoproductos.findOne(idproducto);
		return new ModelAndView("mostrar")
			.addObject("producto", producto);
	}
	
	@RequestMapping("/mostrarproductocarrito")
	public ModelAndView mostrarproductocarrito(@RequestParam String nombre){
		List<Producto> prod = (List<Producto>)repoproductos.findByNombre(nombre);
		Producto producto=prod.get(0);
		return new ModelAndView("mostrar")
			.addObject("producto", producto);
	}	
	
	@RequestMapping("/busqueda")
	public ModelAndView busqueda(HttpSession session, @RequestParam String nombre) {			
		List<Producto> productos = repoproductos.findByNombreIgnoreCaseContaining(nombre);
		String titulo=nombre+": productos "+nombre+" | Poxmania";
		session.setAttribute("productosfiltrados", productos);
		return new ModelAndView("listaproductos")
			.addObject("titulo", titulo)
			.addObject("listaproductos", productos);
	}
	
	@RequestMapping("/busquedaporcategoria")
	public ModelAndView busquedaporcategoria(@RequestParam String nombre, HttpSession session){
		List<Producto> productos = repoproductos.findByCategoria(nombre);
		session.setAttribute("productosfiltrados", productos);
		String titulo=nombre+" - Compra al mejor precio en Poxmania";
		return new ModelAndView("listaproductos")
			.addObject("titulo", nombre)
			.addObject("titulopagina", titulo)
			.addObject("listaproductos", productos);
	}
	
	@RequestMapping("administracion/")
	public ModelAndView administracion(HttpSession session){
		return new ModelAndView("administracion/administracion")
			.addObject("seccion", "Panel de administración");
	}
	
	@RequestMapping("/signin")
	public ModelAndView signin(@RequestParam String correo, HttpSession session){
		if (session.getAttribute("usuario")!=null){
			return new ModelAndView("redirect:/");
		}
		List<Usuario> usuario=repousuarios.findByCorreo(correo);
		if (!usuario.isEmpty()){
			return new ModelAndView("login")
				.addObject("errorcorreo", "Este e-mail ya ha sido utilizado");
		}
		else{
			return new ModelAndView("registro")
				.addObject("correo", correo);
		}
	 }
	 
	@RequestMapping("/carrito")
	public ModelAndView Carrito(HttpSession session) {	
		return new ModelAndView("micarrito");
	}  	  
	  
	@RequestMapping("/mostrarporcategoria")
	public ModelAndView mostrarporcategoria(@RequestParam String nombre, HttpSession session){
		List<Producto> lista=new ArrayList<Producto>();
		if (nombre.equals("Electrodomesticos")){
			lista.addAll(repoproductos.findByCategoria("Mantenimiento y limpieza"));
			lista.addAll(repoproductos.findByCategoria("Cocina"));
		}
		if (nombre.equals("Televisiones")){
			lista.addAll(repoproductos.findByCategoria("Televisor LED"));
			lista.addAll(repoproductos.findByCategoria("Televisor Ultra HD"));
			lista.addAll(repoproductos.findByCategoria("Accesorios"));
		}
		if (nombre.equals("Informatica")){
			lista.addAll(repoproductos.findByCategoria("Ordenador de sobremesa"));
			lista.addAll(repoproductos.findByCategoria("Portatiles"));
			lista.addAll(repoproductos.findByCategoria("Perifericos"));
		}
		if (nombre.equals("Videoconsolas")){
			lista.addAll(repoproductos.findByCategoria("Consolas"));
			lista.addAll(repoproductos.findByCategoria("Videojuegos"));
		}
		if (nombre.equals("Sonido")){
			lista.addAll(repoproductos.findByCategoria("Reproductores MP3"));
			lista.addAll(repoproductos.findByCategoria("Auriculares"));
		}
		if (nombre.equals("Fotografia y video")){
			lista.addAll(repoproductos.findByCategoria("Reflex y hibrida"));
			lista.addAll(repoproductos.findByCategoria("Objetivos y flashes"));
			lista.addAll(repoproductos.findByCategoria("Tarjetas de memoria"));
		}
		if (nombre.equals("Telefonia y GPS")){
			lista.addAll(repoproductos.findByCategoria("Telefonos"));
			lista.addAll(repoproductos.findByCategoria("Accesorios para movil"));
		}
		else{//si es una subcategoria
			lista.addAll(repoproductos.findByCategoria(nombre));
		}
		session.setAttribute("productosfiltrados", lista);
		return new ModelAndView("listaproductos")
			.addObject("titulo", nombre+" | Poxmania")
			.addObject("listaproductos", lista);
	}	 
	  	   
	@RequestMapping("/ordenarproductos")
	public ModelAndView ordenarproductos(@RequestParam String titulo, @RequestParam String modo, HttpSession session){
		@SuppressWarnings("unchecked")
		List<Producto> productosfiltrados=(List<Producto>)session.getAttribute("productosfiltrados");
		if (modo.equals("creciente")){
			Collections.sort(productosfiltrados);
		}
		else{
			Collections.sort(productosfiltrados, Collections.reverseOrder());
		}		  
		return new ModelAndView("listaproductos")
			.addObject("titulo", titulo)
			.addObject("listaproductos", productosfiltrados);
	} 
	  
	@RequestMapping("/buscarporrango")
	public ModelAndView buscarporrango(HttpSession session, @RequestParam String seccion, @RequestParam String desde, @RequestParam String hasta){
	List<Producto> productosfiltrados;
	double preciodesde;
	double preciohasta;
	try{
		preciodesde=Double.parseDouble(desde);
		preciohasta=Double.parseDouble(hasta);
	}catch(NumberFormatException e){
		return new ModelAndView("listaproductos")
			.addObject("titulo", seccion)
			.addObject("errorprecio", true)
			.addObject("listaproductos", repoproductos.findAll());
	}
	if (preciohasta==99999){
		productosfiltrados=repoproductos.findByPrecioGreaterThan(preciodesde);
	}
	else{
		productosfiltrados=repoproductos.findByPrecioBetween(preciodesde, preciohasta);
	}
	return new ModelAndView("listaproductos")
		.addObject("titulo", "Búsqueda de productos con precio entre "+preciodesde+"€ y "+preciohasta+"€ | Poxmania")
		.addObject("listaproductos", productosfiltrados);
	}	  
}
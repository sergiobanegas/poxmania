package poxmania.Controladores;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import poxmania.Clases.Pedido;
import poxmania.Clases.Producto;
import poxmania.Clases.ProductoCarrito;
import poxmania.Clases.Usuario;
import poxmania.Repositorios.ProductoCarritoRepository;
import poxmania.Repositorios.ProductoRepository;
import poxmania.Repositorios.UsuarioRepository;

@Controller
public class ControladorAdministrador {
	@Autowired
	private ProductoRepository repoproductos;
	@Autowired
	private UsuarioRepository repousuarios;
	@Autowired
	private ProductoCarritoRepository repoproductoscarrito;
	private static final String FILES_FOLDER = "src/main/resources/static/imagenes/Productos/";	
	
	@RequestMapping("administracion/nuevoproducto")
	public ModelAndView nuevoAnuncio(HttpSession session) {
		if (session.getAttribute("usuario")==null){
			return new ModelAndView("redirect:/");
		}
		else{
			Usuario usuario=(Usuario)session.getAttribute("usuario");
			if (!usuario.getPrivilegios()) return new ModelAndView("redirect:/");
		}
		return new ModelAndView("administracion/nuevoproducto")
			.addObject("seccion", "Nuevo producto");
	}
	
	@RequestMapping("administracion/insertar")
	public ModelAndView insertar(HttpSession session,@RequestParam String nombre,@RequestParam String categoria,@RequestParam String nombreimagen, @RequestParam String descripcion,@RequestParam String precio, @RequestParam int cantidad, @RequestParam MultipartFile imagen) {
		if (session.getAttribute("usuario")==null){
			return new ModelAndView("redirect:/");
		}
		else{
			Usuario usuario=(Usuario)session.getAttribute("usuario");
			if (!usuario.getPrivilegios()){
				return new ModelAndView("redirect:/");
			}
		}
			if (!repoproductos.findByNombre(nombre).isEmpty()){
				return new ModelAndView("administracion/nuevoproducto")
				.addObject("yaexiste", true)
				.addObject("productoainsertar", nombre)
				.addObject("seccion", "Nuevo producto");
			}
			double precioproducto;
			try{
				precioproducto=Double.parseDouble(precio);
			}catch(NumberFormatException e){
				return new ModelAndView("administracion/nuevoproducto")
					.addObject("errorprecio", true);
			}
			precioproducto=precioproducto*100;
			precioproducto=Math.round(precioproducto);
			precioproducto=precioproducto/100;			
			Producto producto=new Producto(nombre,categoria,nombreimagen, descripcion, precioproducto, cantidad);
			repoproductos.save(producto);
			String fileName = nombreimagen + ".jpg";
			try {
				File filesFolder = new File(FILES_FOLDER);
				File existe=new File(FILES_FOLDER+fileName);
				if (existe.exists()){
					return new ModelAndView("administracion/nuevoproducto")
					.addObject("errorimagen", true)
					.addObject("seccion", "Nuevo producto");
				}
				
				if (!filesFolder.exists()) {
					filesFolder.mkdirs();
				}
				File uploadedFile = new File(filesFolder.getAbsolutePath(), fileName);
				imagen.transferTo(uploadedFile);
				session.setAttribute("productos", repoproductos.findAll());
				return new ModelAndView("administracion/nuevoproducto")
					.addObject("correcto", true)
					.addObject("seccion", "Nuevo producto");
			} catch (Exception e) {
				return new ModelAndView("administracion/nuevoproducto")
					.addObject("correcto", false)
					.addObject("seccion", "Nuevo producto");
		}
	}
	
	@RequestMapping("administracion/modificarproductos")
	public ModelAndView modificarproductos(HttpSession session) {
		if (session.getAttribute("usuario")==null){
			return new ModelAndView("redirect:/");
		}
		else{
			Usuario usuario=(Usuario)session.getAttribute("usuario");
			if (!usuario.getPrivilegios()) return new ModelAndView("redirect:/");
		}
		return new ModelAndView("administracion/modificarproductos")
			.addObject("productos", repoproductos.findAll())
			.addObject("seccion", "Modificar productos");
	}
	
	@RequestMapping("administracion/modificarproductosfiltrados")
	public ModelAndView modificarproductosfiltrados(String nombre, HttpSession session) {
		if (session.getAttribute("usuario")==null){
			return new ModelAndView("redirect:/");
		}
		else{
			Usuario usuario=(Usuario)session.getAttribute("usuario");
			if (!usuario.getPrivilegios()) return new ModelAndView("redirect:/");
		}
		if(repoproductos.findByNombreIgnoreCaseContaining(nombre).isEmpty()){
	  		return new ModelAndView("administracion/modificarproductos")
	  			.addObject("noencontrado", "No hay productos con ese nombre")
	  			.addObject("seccion", "Modificación de productos");
	  	}
	  	else{
			return new ModelAndView("administracion/modificarproductos")
			.addObject("productos", repoproductos.findByNombreIgnoreCaseContaining(nombre))
			.addObject("seccion", "Modificar productos");
	  	}
	}
	
	@RequestMapping("administracion/mostrarproductoamodificar")
	public ModelAndView mostrarproductoamodificar(HttpSession session,@RequestParam long id){
		if (session.getAttribute("usuario")==null){
			return new ModelAndView("redirect:/");
		}
		else{
			Usuario usuario=(Usuario)session.getAttribute("usuario");
			if (!usuario.getPrivilegios()){
				return new ModelAndView("redirect:/");
			}
		}
		Producto producto=repoproductos.findOne(id);
		return new ModelAndView("administracion/productoamodificar")
			.addObject("seccion", "Modificar producto")
			.addObject("producto", producto);
	}	  
		
	@RequestMapping("administracion/modificar")
	public ModelAndView modificar(@RequestParam long id, @RequestParam String precio, @RequestParam int cantidad, @RequestParam String descripcion,@RequestParam String nombreimagen, @RequestParam MultipartFile imagen, HttpSession session){
		if (session.getAttribute("usuario")==null){
			return new ModelAndView("redirect:/");
		}
		else{
			Usuario usuario=(Usuario)session.getAttribute("usuario");
			if (!usuario.getPrivilegios()){
				return new ModelAndView("redirect:/");
			}
		}
		Producto producto=repoproductos.findOne(id);
		Double precioproducto;
		try{
			precioproducto=Double.parseDouble(precio);
		}catch(NumberFormatException e){
			return new ModelAndView("administracion/productoamodificar")
			.addObject("seccion", "Modificar producto")
			.addObject("producto", producto)
			.addObject("errorprecio", true);
		}
		if (!nombreimagen.equals("")){
			String imagenamodificar=FILES_FOLDER+nombreimagen+".jpg";
			File ficheroimagen=new File(imagenamodificar);
			if (ficheroimagen.exists()) 
				return new ModelAndView("administracion/productoamodificar")
					.addObject("seccion", "Modificar producto")
					.addObject("producto", producto)
					.addObject("imagenexiste", true);					
			}
			if (!imagen.isEmpty()){//si ha introducido una imagen nueva		  			  			
				try {
					File filesFolder = new File(FILES_FOLDER);
					String imagenamodificar;
					if (nombreimagen.equals("")){//si no ha introducido nuevo nombre
						imagenamodificar=producto.getImagen()+".jpg";
					}
					else{//si ha introducido nuevo nombre			  				
						imagenamodificar=nombreimagen+".jpg";		
					}
					String imagenproducto=producto.getImagen()+".jpg";
					File ficheroaborrar=new File(filesFolder.getAbsolutePath(), imagenproducto);
					ficheroaborrar.delete();//borras la antigua imagen		  			
					File uploadedFile = new File(filesFolder.getAbsolutePath(), imagenamodificar);
					imagen.transferTo(uploadedFile);
				} catch (Exception e) {
					return new ModelAndView("administracion/productoamodificar")
						.addObject("seccion", "Modificar producto")	
						.addObject("producto", producto)
						.addObject("error", true);
				}		  			
			}
			else{//Si no ha introducido nueva imagen, tendra que cambiarle el nombre
				if (!nombreimagen.equals("")){
					repoproductos.actualizarNombreimagen(id,  nombreimagen);
					try{
						String nombreantiguo=producto.getImagen()+".jpg";
						String nuevonombre=nombreimagen+".jpg";
						File filesFolder=new File(FILES_FOLDER);
						File imagenacambiar=new File(filesFolder.getAbsolutePath(), nombreantiguo);
						File imagennueva=new File(filesFolder.getAbsolutePath(), nuevonombre);
						imagenacambiar.renameTo(imagennueva);
					}catch(Exception e){
						return new ModelAndView("administracion/productoamodificar")
							.addObject("seccio", "Modificar producto")
							.addObject("producto", producto)
							.addObject("error",true);
					}
				}
			}
			if (cantidad!=0){
				repoproductos.actualizarUnidades(id, cantidad);
			}
			if (!nombreimagen.equals("")){
				repoproductos.actualizarNombreimagen(id,  nombreimagen);
			}
			if (!descripcion.equals("")){
				repoproductos.actualizarDescripcion(id, descripcion);
			}
			if(!precio.equals("")){		
				repoproductos.actualizarPrecio(id, precioproducto);
			}
			Producto nuevo=repoproductos.findOne(id);
			session.setAttribute("productos", repoproductos.findAll());
			return new ModelAndView("administracion/productoamodificar")
				.addObject("producto", nuevo)
				.addObject("seccion", "Modificar producto")
				.addObject("error", false);
	}
	
	@RequestMapping("administracion/borrarproducto")
	public ModelAndView borrarproducto(@RequestParam long id, HttpSession session){
		if (session.getAttribute("usuario")==null){
			return new ModelAndView("redirect:/");
		}
		else{
			Usuario usuario=(Usuario)session.getAttribute("usuario");
			if (!usuario.getPrivilegios()) return new ModelAndView("redirect:/");
		}
		Producto producto=repoproductos.findOne(id);
		repoproductos.delete(producto);
		session.setAttribute("productos", repoproductos.findAll());
		return new ModelAndView("administracion/modificarproductos")
			.addObject("productos", repoproductos.findAll())
			.addObject("seccion", "Modificar producto")
			.addObject("borrado", true);
	}
	
	@RequestMapping("administracion/prepararpedidos")
	public ModelAndView prepararpedidos(HttpSession session){
		if (session.getAttribute("usuario")==null){
			return new ModelAndView("redirect:/");
		}
		else{
			Usuario usuario=(Usuario)session.getAttribute("usuario");
			if (!usuario.getPrivilegios()) return new ModelAndView("redirect:/");
		}
		List<ProductoCarrito> productoscarrito=(List<ProductoCarrito>) repoproductoscarrito.findAll();
		if (!productoscarrito.isEmpty()){//si hay productos tipo carrito
			ArrayList<Pedido> listapedidos=new ArrayList<Pedido>();
			Pedido pedido=new Pedido();  
			if(productoscarrito.size()==1){//si solo hay un pedido, lo crea 
				Usuario usuario=repousuarios.findOne(productoscarrito.get(0).getIdusuario());	
				pedido=new Pedido(usuario, productoscarrito.get(0).getFecha(), productoscarrito.get(0).getEstado());
				pedido.getListaproductos().add(productoscarrito.get(0));
				listapedidos.add(pedido);
			}
			else{//si hay varios
				String fecha="";
				for(int i=0;i<productoscarrito.size();i++){
					if(!productoscarrito.get(i).getFecha().equals(fecha)){// cambia la fecha, es un nuevo pedido							  						  
						if (i!=0){
							listapedidos.add(pedido);
						}
						Usuario usuario=repousuarios.findOne(productoscarrito.get(i).getIdusuario());	
						pedido=new Pedido(usuario, productoscarrito.get(i).getFecha(), productoscarrito.get(i).getEstado());
						if (i==productoscarrito.size()-1){//si es el ultimo producto lo añade al pedido
							pedido.getListaproductos().add(productoscarrito.get(i));
							listapedidos.add(pedido);
						}
						else{
							pedido.getListaproductos().add(productoscarrito.get(i));
						}
					}
					else{//si no cambia la fecha, es el mismo pedido
						pedido.getListaproductos().add(productoscarrito.get(i));
						if(i==productoscarrito.size()-1){//si es el ultimo pedido lo añade
							listapedidos.add(pedido);
						}
					}
					fecha=productoscarrito.get(i).getFecha();
				}
			}
			ArrayList<Pedido> pedidossincarrito=new ArrayList<Pedido>();
			for (int i=0;i<listapedidos.size();i++){//ahora los ordena, primero los pedidos en trámite y después los pedidos preparados
				if((listapedidos.get(i).getEstado().equals("En trámite"))&&(!listapedidos.get(i).getEstado().equals("carrito"))){
					pedidossincarrito.add(listapedidos.get(i));
				}
			}
			for (int i=0;i<listapedidos.size();i++){
				if((listapedidos.get(i).getEstado().equals("Preparado"))&&(!listapedidos.get(i).getEstado().equals("carrito"))){
					pedidossincarrito.add(listapedidos.get(i));
				}
			}
			session.setAttribute("pedidos", pedidossincarrito);
		}
		else{
			session.setAttribute("pedidos", new ArrayList<Pedido>());
		}
		return new ModelAndView("administracion/prepararpedidos")
			.addObject("seccion", "Preparar pedidos");
	}
	
	@RequestMapping("administracion/prepararpedido")
		public ModelAndView prepararpedido(@RequestParam String fecha, HttpSession session){
		if (session.getAttribute("usuario")==null){
			return new ModelAndView("redirect:/");
		}
		else{
			Usuario usuario=(Usuario)session.getAttribute("usuario");
			if (!usuario.getPrivilegios()) return new ModelAndView("redirect:/");
		}
		List<ProductoCarrito> productospreparados =repoproductoscarrito.findByFecha(fecha);
		for(int i=0;i<productospreparados.size();i++){
			repoproductoscarrito.preparar(productospreparados.get(i).getId());
		}
		List<ProductoCarrito> productoscarrito=(List<ProductoCarrito>) repoproductoscarrito.findAll();
		ArrayList<Pedido> listapedidos=new ArrayList<Pedido>();
		Pedido pedido=new Pedido();	
		fecha="";
		for(int i=0;i<productoscarrito.size();i++){
			if (!productoscarrito.get(i).getEstado().equals("carrito")){
				if(!productoscarrito.get(i).getFecha().equals(fecha)){// cambia la fecha, es un nuevo pedido							  						  
					if (!pedido.getListaproductos().isEmpty()){
						listapedidos.add(pedido);
						pedido=new Pedido();
					}
					Usuario usuario=repousuarios.findOne(productoscarrito.get(i).getIdusuario());						  
					pedido.setDireccion(usuario.getDireccion());
					pedido.setIdusuario(usuario.getId());
					pedido.setNombreusuario(usuario.getNombre());
					pedido.setTelefono(usuario.getTelefono());
					pedido.setFecha(productoscarrito.get(i).getFecha());
					pedido.setEstado(productoscarrito.get(i).getEstado());
					pedido.getListaproductos().add(productoscarrito.get(i));		
				}
				else{//si no cambia la fecha, es el mismo pedido
					pedido.getListaproductos().add(productoscarrito.get(i));
				}
			}
		}				  
		session.setAttribute("pedidos", listapedidos);
		return new ModelAndView("redirect:./prepararpedidos");		  
	}
}

package poxmania.Controladores;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import poxmania.Clases.Pedido;
import poxmania.Clases.Producto;
import poxmania.Clases.ProductoCarrito;
import poxmania.Clases.Usuario;
import poxmania.Repositorios.ProductoCarritoRepository;
import poxmania.Repositorios.ProductoRepository;
import poxmania.Repositorios.UsuarioRepository;

@Controller
public class ControladorUsuario {
	@Autowired
	private ProductoRepository repoproductos;
	@Autowired
	private UsuarioRepository repousuarios;
	@Autowired
	private ProductoCarritoRepository repoproductoscarrito;
	 
	@RequestMapping("/registrarusuario")
	public ModelAndView registrarusuario(@RequestParam String correo, @RequestParam String pass,@RequestParam String pass2, @RequestParam String nombre, @RequestParam String nombrecompleto, @RequestParam String telefono, @RequestParam String direccion, HttpSession session){
		if (session.getAttribute("usuario")!=null){
			return new ModelAndView("redirect:/");
		}
		if (pass.equals(pass2)){
			List<Usuario> usuario1=repousuarios.findByCorreo(correo);
			List<Usuario> usuario2=repousuarios.findByNombre(nombre);
			if (!usuario1.isEmpty()){
				return new ModelAndView("registro")
					.addObject("errorcorreo", "Este e-mail ya ha sido utilizado");
			}
			if (!usuario2.isEmpty()){
				return new ModelAndView("registro")
					.addObject("errornombre", "El nombre de usuario ya existe");
			}
			else{
				Usuario user=new Usuario(nombre, pass, nombrecompleto, telefono, direccion, correo);
				repousuarios.save(user);
				session.setAttribute("usuario", user);
				return new ModelAndView("redirect:/");
			}
		}
		else{
			return new ModelAndView("registro")
			.addObject("errorpass", "Las contraseñas no coinciden")
		  	.addObject("correo", correo);	  
		}
	}
	
	@RequestMapping("/validacion")
	public ModelAndView validacion(@RequestParam String correo, @RequestParam String pass,HttpSession session){
		Usuario user=new Usuario();	 
		if ((correo.equals("admin"))&&(pass.equals("1234"))){
			if(repousuarios.findByPrivilegios(true).isEmpty()){//si no hay ningún administrador lo crea
				user=new Usuario("admin", pass, "Sergio Banegas Cortijo", "Teléfono privado", "Shinjuku-ku - Tokio, Japón", "s.banegas@alumnos.urjc.es");
				user.setPrivilegios(true);
				repousuarios.save(user);
				correo=user.getCorreo();
			}
			else{
				List<Usuario> adm=(List<Usuario>)repousuarios.findByPrivilegios(true);
				Usuario admin=adm.get(0);
				pass=admin.getPass();
				correo=admin.getCorreo();
			}
		}
		else{
			List<Usuario> adm=(List<Usuario>)repousuarios.findByPrivilegios(true);
			Usuario admin=adm.get(0);
			if (correo.equals(admin.getNombre())){
				correo=admin.getCorreo();
			}
		}
		if ((!repousuarios.findByCorreoAndPass(correo,pass).isEmpty())){
			List<Usuario> usuarios=repousuarios.findByCorreoAndPass(correo, pass);
			user=usuarios.get(0);		   
			  session.setAttribute("usuario", user);
			  session.setMaxInactiveInterval(2000);//tiempo de la sesion
			  List<ProductoCarrito> pc=repoproductoscarrito.findByIdusuarioAndEstado(user.getId(), "carrito");
			  if (!pc.isEmpty()){//ya tenia un carrito guardado en la cuenta, lo guarda en el atributo carrito de la sesion
				  Pedido carritousuario=new Pedido(user, pc.get(0).getFecha(), "carrito");
				  for (int i=0;i<pc.size();i++){
					  pc.get(i).setIdusuario(user.getId());
					  carritousuario.getListaproductos().add(pc.get(i));
					  repoproductoscarrito.delete(pc.get(i));//borra el carrito guardado de la base de datos
				  }
				  Pedido carrito=(Pedido)session.getAttribute("carrito");
				  if (carrito!=null){//si a parte de tener un carrito de antes, ha introducido en el carrito productos sin logearse
					  boolean repetido;
					  for(int i=0;i<carrito.getListaproductos().size();i++){
						  carrito.getListaproductos().get(i).setIdusuario(user.getId());
						  repetido=false;
						  for(int j=0;j<carritousuario.getListaproductos().size();j++){
							  if (carritousuario.getListaproductos().get(j).getNombre().equals(carrito.getListaproductos().get(i).getNombre())){
								  carritousuario.getListaproductos().get(j).setCantidad(carritousuario.getListaproductos().get(j).getCantidad()+carrito.getListaproductos().get(i).getCantidad());
								  repetido=true;
							  }
						  }
						  if (repetido==false){
							  carritousuario.getListaproductos().add(carrito.getListaproductos().get(i));
						  }
					  }
				  }
				  session.setAttribute("carrito", carritousuario);
			  }
			  else{//si no tenia carrito en la cuenta
				  Pedido carrito=(Pedido)session.getAttribute("carrito");
				  if (carrito!=null){//no tenia un carrito en la cuenta pero ha introducido en el carrito productos sin logearse
					  carrito.setFecha(carrito.getFecha());
					  carrito.setIdusuario(user.getId());
					  for(int i=0;i<carrito.getListaproductos().size();i++){
						  carrito.getListaproductos().get(i).setIdusuario(user.getId());
					  }
					  session.setAttribute("carrito", carrito);
				  }					            	
			  }
			  return new ModelAndView("redirect:/");
		  } 
		  else{
			  return new ModelAndView("login")
			  	.addObject("error","Correo o contraseña incorrectos");      
		  }
	}	
	
	@RequestMapping("/logout")
	public ModelAndView logout(HttpSession session){	
		  if (session.getAttribute("usuario")==null){
			  return new ModelAndView("redirect:/");
		  }
		  Pedido carrito=(Pedido)session.getAttribute("carrito");
		  if (carrito!=null){
			  DateFormat fechaconformato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			  String fecha=fechaconformato.format(new Date());
			  if (!carrito.getListaproductos().isEmpty()){
				  for(int i=0;i<carrito.getListaproductos().size();i++){
					  carrito.getListaproductos().get(i).setFecha(fecha);
					  carrito.getListaproductos().get(i).setEstado("carrito");
					  repoproductoscarrito.save(carrito.getListaproductos().get(i));
				  }
			  }
		  }
		  session.setAttribute("usuario", null);
		  session.setAttribute("carrito", null);
		  return new ModelAndView("redirect:/");
	}
	  
	@RequestMapping("/micuenta")
	public ModelAndView micuenta(HttpSession session){
		if (session.getAttribute("usuario")==null){
			return new ModelAndView("login");
		}
		Usuario user=(Usuario)session.getAttribute("usuario");
		Pedido ultimopedido=new Pedido();
		List<ProductoCarrito> productoscarrito=repoproductoscarrito.findByIdusuario(user.getId());
		if (!productoscarrito.isEmpty()){
			ArrayList<Pedido> listapedidos=new ArrayList<Pedido>();
			Pedido pedido=new Pedido();
			String fecha="";
			  for(int i=0;i<productoscarrito.size();i++){
				  if(!productoscarrito.get(i).getFecha().equals(fecha)){// cambia la fecha, es un nuevo pedido	
					  pedido=new Pedido(user, productoscarrito.get(i).getFecha(), productoscarrito.get(i).getEstado());
					  pedido.getListaproductos().add(productoscarrito.get(i));	
					  listapedidos.add(pedido);
				  }
				  else  pedido.getListaproductos().add(productoscarrito.get(i));//si no cambia la fecha, sigue siendo el mismo pedido
				  fecha=productoscarrito.get(i).getFecha();
			  }
			  ultimopedido=listapedidos.get(listapedidos.size()-1);
		}
		return new ModelAndView("micuenta")
			.addObject("ultimopedido",ultimopedido);
	}
	
	@RequestMapping("/misdatospersonales")
	 public ModelAndView misdatospersonales(HttpSession session){
		  if (session.getAttribute("usuario")==null){
			  return new ModelAndView("redirect:/");
		  }
		  return new ModelAndView("misdatospersonales");
	  }
	  
	@RequestMapping ("/cambiardatospersonales")
	public ModelAndView cambiardatospersonales(HttpSession session, @RequestParam long id, @RequestParam String nombre, @RequestParam String nombrecompleto, @RequestParam String telefono, @RequestParam String correo){
		if (!nombre.equals("")){
			List<Usuario> usuarionombre=repousuarios.findByNombre(nombre);
			if (!usuarionombre.isEmpty()){
				return new ModelAndView("misdatospersonales")
					.addObject("errornombre", true);
			}
			repousuarios.cambiarNombre(id, nombre);
		}
		if(!correo.equals("")){
			List<Usuario> usuariocorreo=repousuarios.findByCorreo(correo);
			if (!usuariocorreo.isEmpty()){
				return new ModelAndView("misdatospersonales")
					.addObject("errorcorreo", true);
			}			
			repousuarios.cambiarCorreo(id, correo);
		}	
		if (!nombrecompleto.equals("")){
			repousuarios.cambiarNombrecompleto(id, nombrecompleto);
		}
		if (!telefono.equals("")){
			repousuarios.cambiarTelefono(id, telefono);
		}				
		Usuario user=repousuarios.findOne(id);
		session.setAttribute("usuario", user);	  
		return new ModelAndView("misdatospersonales")
			.addObject("exito", "Datos cambiados correctamente");
	}
	
	@RequestMapping ("/cambiarpass")
	public ModelAndView cambiardatospass(HttpSession session, @RequestParam long id, @RequestParam String passantigua, @RequestParam String pass1, @RequestParam String pass2){
		Usuario user=repousuarios.findOne(id);
		if (user.getPass().equals(passantigua)){
			if(pass1.equals(pass2)){
				if(pass1.equals(user.getPass())){
					return new ModelAndView("/misdatospersonales")
					.addObject("mensaje", "mismapass");
				}
				repousuarios.cambiarPass(id, pass1);
				user=repousuarios.findOne(id);
				session.setAttribute("usuario", user);
				return new ModelAndView("/misdatospersonales")
					.addObject("mensaje", "correcto");
			}
			else{
				return new ModelAndView("/misdatospersonales")
					.addObject("mensaje", "nocoinciden");		  
			}
		}
		else{
			return new ModelAndView("/misdatospersonales")
				.addObject("mensaje", "erronea");		  	  
		}
	}  

	@RequestMapping("/mispedidos")
	public ModelAndView mispedidos(HttpSession session) {
		if (session.getAttribute("usuario")==null){
			return new ModelAndView("redirect:/");
		}
		Usuario user = (Usuario) session.getAttribute("usuario");
		List<ProductoCarrito> productoscarrito=repoproductoscarrito.findByIdusuario(user.getId());
		if (!productoscarrito.isEmpty()){
			ArrayList<Pedido> listapedidos=new ArrayList<Pedido>();
			        Pedido pedido=new Pedido();
			        String fecha="";
			        for(int i=productoscarrito.size()-1;i>=0;i--){
			        	if(!productoscarrito.get(i).getFecha().equals(fecha)){// cambia la fecha, es un nuevo pedido	
			        		pedido=new Pedido(user, productoscarrito.get(i).getFecha(), productoscarrito.get(i).getEstado());
			        		pedido.getListaproductos().add(productoscarrito.get(i));	
			        		listapedidos.add(pedido);
			        	}
			        	else{//si no cambia la fecha, es el mismo pedido
			        		pedido.getListaproductos().add(productoscarrito.get(i));
			        	}
			        	fecha=productoscarrito.get(i).getFecha();
			        }
			        session.setAttribute("mispedidos", listapedidos);
		}
		return new ModelAndView("mispedidos");	
	}
	
	@RequestMapping("/meteralcarrito")
	public ModelAndView meteralcarrito(@RequestParam String identificador,HttpSession session){
		long id=Long.parseLong(identificador);		 
		Producto producto=repoproductos.findOne(id);		  
		Pedido carrito=(Pedido)session.getAttribute("carrito");
		Usuario user=(Usuario)session.getAttribute("usuario");
		DateFormat fechaconformato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String fecha=fechaconformato.format(new Date());
		if (user==null){
			user=new Usuario();		  
		}
		if (carrito==null){
			carrito=new Pedido(user, fecha, "carrito");
		}
		boolean repetido=false;
		for (int i=0;i<carrito.getListaproductos().size();i++){
			carrito.getListaproductos().get(i).setIdusuario(user.getId());;
			if (carrito.getListaproductos().get(i).getNombre().equals(producto.getNombre())){
				carrito.getListaproductos().get(i).setCantidad(carrito.getListaproductos().get(i).getCantidad()+1);
				repetido=true;
			}
		}
		if (repetido==false){
			ProductoCarrito productoagregado=new ProductoCarrito(producto, 1, user.getId());
			productoagregado.setEstado("carrito");
			carrito.getListaproductos().add(productoagregado);
		}
		session.setAttribute("carrito", carrito);
		return new ModelAndView("micarrito");	  		
	}
	
	@RequestMapping("/modificarcantidad")
	public ModelAndView modificarcantidad(@RequestParam String nombre, @RequestParam int cantidad, HttpSession session){
		Pedido carrito=(Pedido)session.getAttribute("carrito");
		List<Producto> prod=(List<Producto>)repoproductos.findByNombre(nombre);
		Producto producto=prod.get(0);
		String superado="";
		if(cantidad>0){
			int i=0;
			boolean encontrado=false;	
			while ((i<carrito.getListaproductos().size()&&(encontrado==false))){
				if (carrito.getListaproductos().get(i).getNombre().equals(nombre)){
					if(cantidad>producto.getUnidades()){//si pide mas unidades de las que hay
						carrito.getListaproductos().get(i).setCantidad(producto.getUnidades());	
						superado=carrito.getListaproductos().get(i).getNombre();
					}
					else{//si hay suficiente stock
						carrito.getListaproductos().get(i).setCantidad(cantidad);					  	
					}
					encontrado=true;
				}
				i++;
			}
			if (!superado.equals("")) {
				return new ModelAndView("micarrito")
					.addObject("superado", superado);
			}
			session.setAttribute("carrito", carrito);
		}
		else{//si mandamos el valor 0 desde la pagina se elimina del carrito
			for(int i=0;i<carrito.getListaproductos().size();i++){
				if (carrito.getListaproductos().get(i).getNombre().equals(nombre))
					carrito.getListaproductos().remove(i);
			}
			if(carrito.getListaproductos().size()==0){
				session.setAttribute("carrito", null);
			}
			else{
				session.setAttribute("carrito", carrito);
			}
		}
		return new ModelAndView("micarrito");
	}
	@RequestMapping("/realizarpedido")
	public ModelAndView realizarpedido(HttpSession session){
		Pedido carrito = (Pedido) session.getAttribute("carrito");	        
		Usuario user= (Usuario)session.getAttribute("usuario");
		if (user==null){
			return new ModelAndView("login")
				.addObject("pedidosinusuario", true);
		}
		else{
			if (carrito!=null){//si hay carrito		        		
				DateFormat fechaconformato = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String fecha=fechaconformato.format(new Date());
				for (int i=0;i<carrito.getListaproductos().size();i++){//por cada producto en el carrito		        		
					ProductoCarrito nuevo=new ProductoCarrito();
					nuevo.setNombre(carrito.getListaproductos().get(i).getNombre());
					nuevo.setFecha(fecha);
					nuevo.setDescripcion(carrito.getListaproductos().get(i).getDescripcion());
					nuevo.setCategoria(carrito.getListaproductos().get(i).getCategoria());
					nuevo.setEstado("En trámite");
					nuevo.setIdusuario(user.getId());
					nuevo.setCantidad(carrito.getListaproductos().get(i).getCantidad());
					nuevo.setImagen(carrito.getListaproductos().get(i).getImagen());
					nuevo.setPrecio(carrito.getListaproductos().get(i).getPrecio());
					repoproductoscarrito.save(nuevo);
					List<Producto> prod=(List<Producto>)repoproductos.findByNombre(carrito.getListaproductos().get(i).getNombre());
					Producto producto=prod.get(0);
					int nuevacantidad=producto.getUnidades() - carrito.getListaproductos().get(i).getCantidad();
					repoproductos.actualizarUnidades(producto.getId(), nuevacantidad);
				}      
				session.setAttribute("carrito",  null);
				return new ModelAndView("micarrito")
					.addObject("carritovaciopedido", false)
					.addObject("pedidorealizado", true);
			}	        	
			else{
				return new ModelAndView("micarrito")
					.addObject("carritovaciopedido", true);
			}
		}
	}	  	  
}
package poxmania.Clases;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ProductoCarrito implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;	
	private String fecha;
	private String estado;
	private String nombre;
	private String categoria;
	private String imagen;
	private String descripcion;
	private double precio;
	private int cantidad;
	private long idusuario;

	public ProductoCarrito() {
	}
	
	public ProductoCarrito(Producto producto, int cantidad, long idusuario){
		this.estado="carrito";
		this.nombre=producto.getNombre();
		this.categoria=producto.getCategoria();
		this.imagen=producto.getImagen();
		this.descripcion=producto.getDescripcion();
		this.precio=producto.getPrecio();
		this.cantidad=cantidad;
		this.idusuario=idusuario;		
	}
	
	public long getId() {
		return id;
	}
	
	public String getFecha() {
		return fecha;
	}
	
	public String getEstado() {
		return estado;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public String getCategoria() {
		return categoria;
	}
	
	public String getImagen() {
		return imagen;
	}
	
	public String getDescripcion() {
		return descripcion;
	}
	
	public double getPrecio() {
		return precio;
	}
	
	public int getCantidad() {
		return cantidad;
	}
	
	public long getIdusuario() {
		return idusuario;
	}
	
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}
	
	public void setImagen(String imagen) {
		this.imagen = imagen;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	public void setPrecio(double precio) {
		this.precio = precio;
	}
	
	public void setCantidad(int cantidad) {
		this.cantidad = cantidad;
	}
	
	public void setIdusuario(long idusuario) {
		this.idusuario = idusuario;
	}
}

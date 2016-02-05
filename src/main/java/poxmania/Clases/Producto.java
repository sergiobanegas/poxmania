package poxmania.Clases;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Producto implements Serializable,Comparable<Producto>{
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;	
	private String nombre;
	private String categoria;
	private String imagen;
	private String descripcion;
	private double precio;
	private int unidades;

	public Producto() {
	}

	public Producto(String nombre, String categoria, String imagen, String descripcion, double precio, int unidades) {
		this.nombre = nombre;
		this.categoria = categoria;
		this.imagen = imagen;
		this.descripcion=descripcion;
		this.precio=precio;
		this.unidades=unidades;
	}
	
	@Override
	public int compareTo(Producto p){
		double p1=this.getPrecio();
		double p2=p.getPrecio();
		if (p1>p2) return 1;
		if (p1<p2) return -1;
		else return 0;
	}
	
	public long getId() {
		return id;
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
	
	public int getUnidades() {
		return unidades;
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
	
	public void setUnidades(int unidades) {
		this.unidades = unidades;
	}	
}

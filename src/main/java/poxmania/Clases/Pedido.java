package poxmania.Clases;

import java.util.ArrayList;

public class Pedido {

	private ArrayList<ProductoCarrito> listaproductos=new ArrayList<ProductoCarrito>();
	private String fecha;
	private String estado="carrito";
	private long idusuario;
	private String nombreusuario;
	private String direccion;
	private String telefono;
	
	public Pedido() {
	}
	
	public Pedido(Usuario usuario, String fecha, String estado){
		this.fecha=fecha;
		this.estado=estado;
		this.idusuario=usuario.getId();
		this.nombreusuario=usuario.getNombrecompleto();
		this.direccion=usuario.getDireccion();
		this.telefono=usuario.getTelefono();	
	}
	
	public double factura(){
		double factura=0;
		for(int i=0;i<this.listaproductos.size();i++){
			factura=factura+(this.listaproductos.get(i).getCantidad()*this.listaproductos.get(i).getPrecio());
		}
		return factura;
	}
	public int numerodeproductos(){
		return this.listaproductos.size();
	}
	
	public ArrayList<ProductoCarrito> getListaproductos() {
		return listaproductos;
	}
	
	public String getFecha() {
		return fecha;
	}
	
	public String getEstado() {
		return estado;
	}
	
	public long getIdusuario() {
		return idusuario;
	}
	
	public String getNombreusuario() {
		return nombreusuario;
	}
	
	public String getDireccion() {
		return direccion;
	}
	
	public String getTelefono() {
		return telefono;
	}
	
	public void setListaproductos(ArrayList<ProductoCarrito> listaproductos) {
		this.listaproductos = listaproductos;
	}
	
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	public void setIdusuario(long idusuario) {
		this.idusuario = idusuario;
	}
	
	public void setNombreusuario(String nombreusuario) {
		this.nombreusuario = nombreusuario;
	}
	
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	
}

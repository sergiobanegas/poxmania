package poxmania.Clases;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class Usuario implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	private String correo;
	private String nombre;
	private String pass;
	private String nombrecompleto;
	private String telefono;
	private String direccion;
	private boolean privilegios;
	
	public Usuario() {
	}
	public Usuario(String nombre, String pass, String nombrecompleto, String telefono, String direccion, String correo){
		this.correo=correo;
		this.nombre=nombre;
		this.pass=pass;
		this.nombrecompleto=nombrecompleto;
		this.telefono=telefono;
		this.direccion=direccion;	
	}
	
	public long getId() {
		return id;
	}
	
	public String getCorreo() {
		return correo;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public String getPass() {
		return pass;
	}
	
	public String getNombrecompleto() {
		return nombrecompleto;
	}
	
	public String getTelefono() {
		return telefono;
	}
	
	public String getDireccion() {
		return direccion;
	}
	
	public boolean getPrivilegios(){
		return this.privilegios;
	}
	
	public void setCorreo(String correo) {
		this.correo = correo;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public void setPass(String pass) {
		this.pass = pass;
	}
	
	public void setNombrecompleto(String nombrecompleto) {
		this.nombrecompleto = nombrecompleto;
	}
	
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	
	public void setPrivilegios(boolean privilegios) {
		this.privilegios = privilegios;
	}	
}

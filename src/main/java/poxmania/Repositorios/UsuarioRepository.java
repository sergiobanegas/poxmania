package poxmania.Repositorios;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import poxmania.Clases.Usuario;

public interface UsuarioRepository extends CrudRepository<Usuario, Long> {
	List<Usuario> findByNombre(String nombre);
	List<Usuario> findByCorreoAndPass(String correo, String pass);
	List<Usuario> findByCorreo(String correo);
	List<Usuario> findByPrivilegios(boolean privilegios);
	
	@Modifying
	@Transactional
	@Query(value="UPDATE Usuario u SET u.nombre=:nombre where u.id=:id")
	void cambiarNombre(@Param("id")long id, @Param("nombre")String nombre);	
	
	@Modifying
	@Transactional
	@Query(value="UPDATE Usuario u SET u.nombrecompleto=:nombrecompleto where u.id=:id")
	void cambiarNombrecompleto(@Param("id")long id, @Param("nombrecompleto")String nombrecompleto);	
	
	@Modifying
	@Transactional
	@Query(value="UPDATE Usuario u SET u.telefono=:telefono where u.id=:id")
	void cambiarTelefono(@Param("id")long id, @Param("telefono")String telefono);	
	
	@Modifying
	@Transactional
	@Query(value="UPDATE Usuario u SET u.correo=:correo where u.id=:id")
	void cambiarCorreo(@Param("id")long id, @Param("correo")String correo);	
	
	@Modifying
	@Transactional
	@Query(value="UPDATE Usuario u SET u.pass=:pass where u.id=:id")
	void cambiarPass(@Param("id")long id, @Param("pass")String pass);
}
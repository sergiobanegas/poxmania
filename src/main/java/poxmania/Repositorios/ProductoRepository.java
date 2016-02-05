package poxmania.Repositorios;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import poxmania.Clases.Producto;

public interface ProductoRepository extends CrudRepository<Producto, Long> {
	List<Producto> findByNombre(String nombre);
	List<Producto> findByNombreIgnoreCaseContaining(String nombre);
	List<Producto> findByCategoria(String nombre);
	List<Producto> findByPrecioBetween(double preciodesde, double preciohasta);
	List<Producto> findByPrecioGreaterThan(double desde);
	
	@Modifying
	@Transactional
	@Query(value="UPDATE Producto p SET p.unidades=:unidades where p.id=:id")
	void actualizarUnidades(@Param("id")long id, @Param("unidades")int unidades);	
	
	@Modifying
	@Transactional
	@Query(value="UPDATE Producto p SET p.imagen=:nombreimagen where p.id=:id")
	void actualizarNombreimagen(@Param("id")long id, @Param("nombreimagen")String nombreimagen);	
	
	@Modifying
	@Transactional
	@Query(value="UPDATE Producto p SET p.descripcion=:descripcion where p.id=:id")
	void actualizarDescripcion(@Param("id")long id, @Param("descripcion")String descripcion);	
	
	@Modifying
	@Transactional
	@Query(value="UPDATE Producto p SET p.precio=:precio where p.id=:id")
	void actualizarPrecio(@Param("id")long id, @Param("precio")double precio);	
	
	@Modifying
	@Transactional
	@Query(value="UPDATE Producto p SET p.unidades=:unidades where p.id=:id")
	void actualizarPrecio(@Param("id")long id, @Param("unidades")int unidades);
}
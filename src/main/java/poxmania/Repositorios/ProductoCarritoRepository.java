package poxmania.Repositorios;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import poxmania.Clases.ProductoCarrito;

public interface ProductoCarritoRepository extends CrudRepository<ProductoCarrito, Long> {
	List<ProductoCarrito> findByNombre(String nombre);
	List<ProductoCarrito> findByIdusuario(long idusuario);
	List<ProductoCarrito> findByIdusuarioAndEstado(long idusuario, String estado);
	List<ProductoCarrito> findByFecha(String fecha);
	List<ProductoCarrito> findByEstado(String estado);
	
	@Modifying
	@Transactional
	@Query(value="UPDATE ProductoCarrito p SET p.estado='Preparado' where p.id=:id")
	void preparar(@Param("id")long id);		
}

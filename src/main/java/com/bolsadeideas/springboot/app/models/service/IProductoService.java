package com.bolsadeideas.springboot.app.models.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bolsadeideas.springboot.app.models.entity.Producto;

public interface IProductoService {
	
	public List<Producto> findAll();

	public void save(Producto articulo);
	
	public Producto findOne(Long id);
	
	public void delete(Long id);
	
	public Page<Producto> findAll(Pageable pageable);

}

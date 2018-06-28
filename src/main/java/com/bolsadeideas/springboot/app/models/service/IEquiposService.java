package com.bolsadeideas.springboot.app.models.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bolsadeideas.springboot.app.models.entity.inventario_rack;


public interface IEquiposService {


	public List<inventario_rack> findAll();

	public void save(inventario_rack equipos);
	
	public inventario_rack findOne(Integer id);
	
	public void delete(Integer id);
	
	public Page<inventario_rack> findAll(Pageable pageable);


}

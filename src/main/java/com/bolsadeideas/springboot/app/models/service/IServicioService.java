package com.bolsadeideas.springboot.app.models.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import com.bolsadeideas.springboot.app.models.entity.Servicio;


public interface IServicioService {
	
	
	public List<Servicio> findAll();
	
	public void save(Servicio servicio);
	
	public Servicio findOne(Long id);
	
	public void delete(Long id);

	
	public Page<Servicio> findAll(Pageable pageable);

}

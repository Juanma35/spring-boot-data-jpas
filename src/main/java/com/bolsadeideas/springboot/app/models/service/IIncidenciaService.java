package com.bolsadeideas.springboot.app.models.service;

import java.util.List;

import com.bolsadeideas.springboot.app.models.entity.Cliente;
import com.bolsadeideas.springboot.app.models.entity.Tecnico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bolsadeideas.springboot.app.models.entity.Incidenci;




public interface IIncidenciaService {
	

	public List<Incidenci> findAll();

	public void save(Incidenci incidenci);
	
	 Incidenci findOne(Long id);
	
	public void delete(Long id);
	
	public Page<Incidenci> findAll(Pageable pageable);




	
}

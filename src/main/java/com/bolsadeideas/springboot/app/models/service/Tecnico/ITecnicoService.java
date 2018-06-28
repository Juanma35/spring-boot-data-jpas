package com.bolsadeideas.springboot.app.models.service.Tecnico;



import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bolsadeideas.springboot.app.models.entity.Tecnico;


public interface ITecnicoService {
	
	public List<Tecnico> findAll();

	public void save(Tecnico tecnico);
	
	public Tecnico findOne(Long id);
	
	public void delete(Long id);
	
	public Page<Tecnico> findAll(Pageable pageable);

}

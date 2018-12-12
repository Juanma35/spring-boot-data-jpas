package com.bolsadeideas.springboot.app.models.service;

import java.util.List;

import com.bolsadeideas.springboot.app.models.entity.Cliente;
import com.bolsadeideas.springboot.app.models.entity.Tecnico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bolsadeideas.springboot.app.models.dao.IIncidencia;
import com.bolsadeideas.springboot.app.models.entity.Incidenci;

//para controlar las incidencias
@Service
public class IncidenciaServiceImpl implements IIncidenciaService {

	
	
	@Autowired
	private IIncidencia incidenciaDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Incidenci> findAll() {
		// TODO Auto-generated method stub
		return (List<Incidenci>) incidenciaDao.findAll();
	}

	@Override
	@Transactional
	public void save(Incidenci incidenci) {
		incidenciaDao.save(incidenci);
		
	}

	@Override
	@Transactional(readOnly = true)
	public Incidenci findOne(Long id) {
		// TODO Auto-generated method stub
		return incidenciaDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		incidenciaDao.deleteById(id);
		
	}

	@Override
	public Page<Incidenci> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return incidenciaDao.findAll(pageable);
	}






}

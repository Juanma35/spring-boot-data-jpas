package com.bolsadeideas.springboot.app.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bolsadeideas.springboot.app.models.dao.IEquiposDaso;
import com.bolsadeideas.springboot.app.models.entity.inventario_rack;


//para controlar el invertario de rack
@Service
public class ServiceEquipoImple implements IEquiposService {

	@Autowired
	public 	IEquiposDaso equipoDao;

	@Override
	public List<inventario_rack> findAll() {
		// TODO Auto-generated method stub
		return (List<inventario_rack>) equipoDao.findAll();
	}

	@Override
	public void save(inventario_rack equipos) {
	
		equipoDao.save(equipos);
		
	}

	@Override
	public inventario_rack findOne(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(Integer id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Page<inventario_rack> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	


}

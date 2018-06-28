package com.bolsadeideas.springboot.app.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bolsadeideas.springboot.app.models.dao.ISercivioDao;
import com.bolsadeideas.springboot.app.models.entity.Servicio;


@Service
public class ServiceServicioImpl implements IServicioService{

	@Autowired
	private  ISercivioDao serviceDao;
	@Override
	public List<Servicio> findAll() {
		// TODO Auto-generated method stub
		return (List<Servicio>) serviceDao.findAll();
	}

	@Override
	public void save(Servicio servicio) {
		// TODO Auto-generated method stub
		serviceDao.save(servicio);
	}

	@Override
	public Servicio findOne(Long id) {
		// TODO Auto-generated method stub
		return serviceDao.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		// TODO Auto-generated method stub
		serviceDao.deleteById(id);
	}

	@Override
	public Page<Servicio> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return serviceDao.findAll(pageable);
	}


}

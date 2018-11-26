package com.bolsadeideas.springboot.app.models.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.bolsadeideas.springboot.app.models.dao.IProductoDao;

import com.bolsadeideas.springboot.app.models.entity.Producto;


@Service
public class ProductoServiceImpl implements IProductoService{

	@Autowired
	private IProductoDao productoDao;
	
	@Override
	@Transactional(readOnly = true)
	public List<Producto> findAll() {
		// TODO Auto-generated method stub
		return (List<Producto>) productoDao.findAll();
	}

	@Override
	@Transactional
	public void save(Producto producto) {
		// TODO Auto-generated method stub
		productoDao.save(producto);
	}

	@Override
	@Transactional(readOnly = true)
	public Producto findOne(Long id) {
		// TODO Auto-generated method stub
		return productoDao.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		// TODO Auto-generated method stub
		productoDao.deleteById(id);
	}

	@Override
	public Page<Producto> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return  productoDao.findAll(pageable);
	}

	
	

}

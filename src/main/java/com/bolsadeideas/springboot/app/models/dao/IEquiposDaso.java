package com.bolsadeideas.springboot.app.models.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.bolsadeideas.springboot.app.models.entity.inventario_rack;


public interface IEquiposDaso extends PagingAndSortingRepository<inventario_rack,Long> {
	
	

}

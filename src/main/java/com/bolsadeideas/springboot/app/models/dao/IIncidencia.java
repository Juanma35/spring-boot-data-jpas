package com.bolsadeideas.springboot.app.models.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.bolsadeideas.springboot.app.models.entity.Incidenci;


public interface IIncidencia extends PagingAndSortingRepository<Incidenci, Long>{

}

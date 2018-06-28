package com.bolsadeideas.springboot.app.models.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.bolsadeideas.springboot.app.models.entity.Tecnico;

public interface ITecnicoDao extends PagingAndSortingRepository<Tecnico, Long>{

}

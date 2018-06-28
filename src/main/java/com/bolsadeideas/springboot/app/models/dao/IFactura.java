package com.bolsadeideas.springboot.app.models.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.bolsadeideas.springboot.app.models.entity.Factura;

public interface IFactura extends PagingAndSortingRepository<Factura, Long>{}

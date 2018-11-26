package com.bolsadeideas.springboot.app.models.service.Tecnico;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bolsadeideas.springboot.app.models.dao.ITecnicoDao;
import com.bolsadeideas.springboot.app.models.entity.Tecnico;

@Service
public class TecnicoServiceImple implements ITecnicoService {

	@Autowired
	private ITecnicoDao tecnicoDao;
	
	@Override
	public List<Tecnico> findAll() {
		// TODO Auto-generated method stub
		return (List<Tecnico>) tecnicoDao.findAll();
	}

	@Override
	public void save(Tecnico tecnico) {
		tecnicoDao.save(tecnico);

	}

	@Override
	public Tecnico findOne(Long id) {
		
		return tecnicoDao.findById(id).orElse(null);
	}

	@Override
	public void delete(Long id) {
		tecnicoDao.deleteById(id);

	}

	@Override
	public Page<Tecnico> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return tecnicoDao.findAll(pageable);
	}

}

package com.pinguela.yourpc.dao;

import java.util.List;

import org.hibernate.Session;

import com.pinguela.DataException;
import com.pinguela.yourpc.model.Country;

public interface CountryDAO {
	
	public List<Country> findAll(Session session)
			throws DataException;

}

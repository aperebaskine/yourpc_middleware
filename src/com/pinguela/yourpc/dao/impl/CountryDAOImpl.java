package com.pinguela.yourpc.dao.impl;

import java.util.List;

import org.hibernate.Session;

import com.pinguela.DataException;
import com.pinguela.yourpc.dao.CountryDAO;
import com.pinguela.yourpc.model.AbstractCriteria;
import com.pinguela.yourpc.model.Country;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class CountryDAOImpl
extends AbstractDAO<String, Country>
implements CountryDAO {
		
	public CountryDAOImpl() {
	}
	
	@Override
	public List<Country> findAll(Session session) throws DataException {
		return super.findBy(session, null);
	}

	@Override
	protected List<Predicate> getCriteria(CriteriaBuilder builder, Root<Country> root,
			AbstractCriteria<Country> criteria) {
		return null;
	}
	
	@Override
	protected void groupByCriteria(CriteriaBuilder builder, CriteriaQuery<Country> query, Root<Country> root,
			AbstractCriteria<Country> criteria) {
		// Unused	
	}

}

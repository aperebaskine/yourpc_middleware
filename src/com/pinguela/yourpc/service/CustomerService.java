package com.pinguela.yourpc.service;

import java.util.List;

import com.pinguela.DataException;
import com.pinguela.ServiceException;
import com.pinguela.yourpc.model.Customer;
import com.pinguela.yourpc.model.CustomerCriteria;

public interface CustomerService {
	
	public Customer login(String email, String password)
			throws ServiceException, DataException;

	public Integer register(Customer c)
			throws ServiceException, DataException;

	public Customer findById(Integer customerId)
			throws ServiceException, DataException;
	
	public Customer findByEmail(String email)
			throws ServiceException, DataException;

	public List<Customer> findBy(CustomerCriteria criteria)
			throws ServiceException, DataException;
	
	public Boolean update(Customer c)
			throws ServiceException, DataException;
	
	public Boolean updatePassword(Integer customerId, String password)
			throws ServiceException, DataException;

	public Boolean delete(Integer customerId)
			throws ServiceException, DataException;

}

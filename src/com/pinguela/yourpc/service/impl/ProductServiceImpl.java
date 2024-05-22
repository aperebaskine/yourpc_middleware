package com.pinguela.yourpc.service.impl;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pinguela.DataException;
import com.pinguela.ServiceException;
import com.pinguela.yourpc.dao.ProductDAO;
import com.pinguela.yourpc.dao.impl.ProductDAOImpl;
import com.pinguela.yourpc.model.Product;
import com.pinguela.yourpc.model.ProductCriteria;
import com.pinguela.yourpc.model.ProductRanges;
import com.pinguela.yourpc.model.Results;
import com.pinguela.yourpc.service.ProductService;
import com.pinguela.yourpc.util.JDBCUtils;

public class ProductServiceImpl implements ProductService {

	private static Logger logger = LogManager.getLogger(ProductServiceImpl.class);
	private ProductDAO productDAO = null;

	public ProductServiceImpl() {
		productDAO = new ProductDAOImpl();
	}

	public Long create(Product p)  
			throws ServiceException, DataException {
		
		if (p == null) {
			throw new IllegalArgumentException("Product cannot be null.");
		}

		Connection conn = null;
		Long id = null;
		boolean commit = false;

		try {
			conn = JDBCUtils.getConnection();
			conn.setAutoCommit(JDBCUtils.NO_AUTO_COMMIT);
			id = productDAO.create(conn, p);
			commit = id != null;
			return id;

		} catch (SQLException sqle) {
			logger.fatal(sqle);
			throw new ServiceException(sqle);
		} finally {
			JDBCUtils.close(conn, commit);
		}
	}

	@Override
	public Boolean update(Product p)  
			throws ServiceException, DataException {
		
		if (p == null) {
			throw new IllegalArgumentException("Product cannot be null.");
		}

		Connection conn = null;
		boolean commit = false;

		try {
			conn = JDBCUtils.getConnection();
			conn.setAutoCommit(JDBCUtils.NO_AUTO_COMMIT);
			if (productDAO.update(conn, p)) {
				commit = true;
				return true;
			} else {
				return false;	
			}
		} catch (SQLException sqle) {
			logger.fatal(sqle);
			throw new ServiceException(sqle);
		} finally {
			JDBCUtils.close(conn, commit);
		}
	}
	
	@Override
	public Boolean delete(Long productId)  
			throws ServiceException, DataException {
		
		if (productId == null) {
			throw new IllegalArgumentException("Product ID cannot be null.");
		}

		Connection conn = null;
		boolean commit = false;

		try {
			conn = JDBCUtils.getConnection();
			conn.setAutoCommit(JDBCUtils.NO_AUTO_COMMIT);
			commit = true;
			return productDAO.delete(conn, productId);
			
		} catch (SQLException sqle) {
			logger.fatal(sqle);
			throw new ServiceException(sqle);
		} finally {
			JDBCUtils.close(conn, commit);
		}
	}

	@Override
	public Product findById(Long id)  
			throws ServiceException, DataException {

		Connection conn = null;

		try {
			conn = JDBCUtils.getConnection();
			return productDAO.findById(conn, id);

		} catch (SQLException sqle) {
			logger.fatal(sqle);
			throw new ServiceException(sqle);
		} finally {
			JDBCUtils.close(conn);
		}
	}

	@Override
	public Results<Product> findBy(ProductCriteria criteria, int startPos, int pageSize)  
			throws ServiceException, DataException {
		
		if (criteria == null) {
			throw new IllegalArgumentException("Criteria cannot be null.");
		}

		Connection conn = null;

		try {
			conn = JDBCUtils.getConnection();
			return productDAO.findBy(conn, criteria, startPos, pageSize);

		} catch (SQLException sqle) {
			logger.fatal(sqle);
			throw new ServiceException(sqle);
		} finally {
			JDBCUtils.close(conn);
		}
	}
	
	@Override
	public ProductRanges getRanges(ProductCriteria criteria) throws ServiceException, DataException {
		
		Connection conn = null;

		try {
			conn = JDBCUtils.getConnection();
			return productDAO.getRanges(conn, criteria);

		} catch (SQLException sqle) {
			logger.fatal(sqle);
			throw new ServiceException(sqle);
		} finally {
			JDBCUtils.close(conn);
		}
	}

}
package p08_dao;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;

import p04_bean.Customer;

/**
 * ClassName: CustomerDAOImpl.java
 * PackageName: p08_dao
 * Description: 可使用 BaseDAO(通用的增刪改查)的方法來實作 CustomerDAO 方法
 */
public class CustomerDAOImpl extends BaseDAO<Customer> implements CustomerDAO {

	@Override
	public void insert(Connection conn, Customer cust) {
		String sql = "INSERT INTO customers(name, email, birth) VALUES(?, ?, ?)";
		update(conn, sql, cust.getName(), cust.getEmail(), cust.getBirth());
	}

	@Override
	public void deleteById(Connection conn, int id) {
		String sql = "DELETE FROM customers WHERE id = ?";
		update(conn, sql, id);
	}

	@Override
	public void update(Connection conn, Customer cust) {
		String sql = "UPDATE customers SET name = ?, email = ?, birth = ? WHERE id = ?";
		update(conn, sql, cust.getName(), cust.getEmail(), cust.getBirth(), cust.getId());
	}

	@Override
	public Customer getCustomerById(Connection conn, int id) {
		String sql = "SELECT id, name, email, birth FROM customers WHERE id = ?";
		Customer customer = getInstance(conn, sql, id);
		return customer;
	}

	@Override
	public List<Customer> getAll(Connection conn) {
		String sql = "SELECT id, name, email, birth FROM customers";
		List<Customer> list = getForList(conn, sql);
		return list;
	}

	@Override
	public Long getCount(Connection conn) {
		String sql = "SELECT count(*) FROM customers";
		return getValue(conn, sql);
	}

	@Override
	public Date getMaxBirth(Connection conn) {
		String sql = "SELECT max(birth) FROM customers";
		return getValue(conn, sql);
	}

}

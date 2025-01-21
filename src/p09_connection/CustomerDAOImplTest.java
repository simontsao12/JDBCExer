package p09_connection;

import java.sql.Connection;

import org.junit.jupiter.api.Test;

import p04_bean.Customer;
import p08_dao.CustomerDAOImpl;
import p09_util.JDBCUtils;

class CustomerDAOImplTest {
	
	private CustomerDAOImpl dao = new CustomerDAOImpl();
	
	@Test
	void testGetCustomerById1() {
		Connection conn = null;
		try {
			conn = JDBCUtils.getConnection1();
			Customer cust = dao.getCustomerById(conn, 12);
			System.out.println(cust);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtils.closeResource(conn, null);
		}
	}
	
	@Test
	void testGetCustomerById2() {
		Connection conn = null;
		try {
			conn = JDBCUtils.getConnection2();
			Customer cust = dao.getCustomerById(conn, 12);
			System.out.println(cust);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtils.closeResource(conn, null);
		}
	}

	@Test
	void testGetCustomerById3() {
		Connection conn = null;
		try {
			conn = JDBCUtils.getConnection3();
			Customer cust = dao.getCustomerById(conn, 12);
			System.out.println(cust);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			JDBCUtils.closeResource(conn, null);
		}
	}
}
package p07_dao.junit;

import java.sql.Connection;
import java.sql.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import p02_util.JDBCUtils;
import p04_bean.Customer;
import p07_dao.CustomerDAOImpl;

class CustomerDAOImplTest {
	
	private CustomerDAOImpl dao = new CustomerDAOImpl();
	
	@Test
	void testInsert() {
		Connection conn = null;
		try {
			conn = JDBCUtils.getConnection();
			Customer cust = new Customer(1, "小飛", "xx@cc.com", new Date(43534646435L));
			dao.insert(conn, cust);
			System.out.println("添加成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			JDBCUtils.closeResource(conn, null);
		}
	}

	@Test
	void testDeleteById() {
		Connection conn = null;
		try {
			conn = JDBCUtils.getConnection();
			dao.deleteById(conn, 13);
			System.out.println("刪除成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			JDBCUtils.closeResource(conn, null);
		}
	}

	@Test
	void testUpdateConnectionCustomer() {
		Connection conn = null;
		try {
			conn = JDBCUtils.getConnection();
			Customer cust = new Customer(9, "莫札札", "mxx@cc.com", new Date(43534646435L));
			dao.update(conn, cust);
			System.out.println("修改成功");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			JDBCUtils.closeResource(conn, null);
		}
	}

	@Test
	void testGetCustomerById() {
		Connection conn = null;
		try {
			conn = JDBCUtils.getConnection();
			Customer cust = dao.getCustomerById(conn, 12);
			System.out.println(cust);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			JDBCUtils.closeResource(conn, null);
		}
	}

	@Test
	void testGetAll() {
		Connection conn = null;
		try {
			conn = JDBCUtils.getConnection();
			List<Customer> list = dao.getAll(conn);
			list.forEach(System.out::println);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			JDBCUtils.closeResource(conn, null);
		}
	}

	@Test
	void testGetCount() {
		Connection conn = null;
		try {
			conn = JDBCUtils.getConnection();
			Long count = dao.getCount(conn);
			System.out.println("表中紀錄數為: " + count);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			JDBCUtils.closeResource(conn, null);
		}
	}

	@Test
	void testGetMaxBirth() {
		Connection conn = null;
		try {
			conn = JDBCUtils.getConnection();
			Date maxBirth = dao.getMaxBirth(conn);
			System.out.println("表中最大生日為: " + maxBirth);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			JDBCUtils.closeResource(conn, null);
		}
	}

}

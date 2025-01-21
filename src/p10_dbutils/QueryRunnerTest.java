package p10_dbutils;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.jupiter.api.Test;

import p04_bean.Customer;
import p09_util.JDBCUtils;

/**
 * ClassName: QueryRunnerTest.java
 * PackageName: p10_dbutils
 * Description: commons dbutils 是 Apache 組織提供的一個開源的 JDBC 工具類庫, 封裝了針對資料庫的增刪改查操作.
 */
public class QueryRunnerTest {
	
	// 測試插入
	@Test
	public void testInsert() {
		Connection conn = null;
		try {
			QueryRunner runner = new QueryRunner();
			conn = JDBCUtils.getConnection3();
			String sql = "INSERT INTO customers(name, email, birth) VALUES(?, ?, ?)";
			int insertCount = runner.update(conn, sql, "坤坤", "kk@cc.com", "1994-09-08");
			System.out.println("添加了" + insertCount + "紀錄");
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {			
			JDBCUtils.closeResource(conn, null);
		}
	}
	
	// 測試查詢
	/*
	 * BeanHandler: 是 ResultSetHandler 介面的實現類, 用於封裝表中的一條紀錄.
	 */
	@Test
	public void testQuery1() {
		Connection conn = null;
		try {
			QueryRunner runner = new QueryRunner();
			conn = JDBCUtils.getConnection3();
			String sql = "SELECT id, name, email, birth FROM customers WHERE id = ?";
			BeanHandler<Customer> handler = new BeanHandler<>(Customer.class);
			Customer customer = runner.query(conn, sql, handler, 12);
			System.out.println(customer);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtils.closeResource(conn, null);
		}
	} 	
	
	/*
	 * BeanListHandler: 是 ResultSetHandler 介面的實現類, 用於封裝表中的多條紀錄構成的集合.
	 */
	@Test
	public void testQuery2() {
		Connection conn = null;
		try {
			QueryRunner runner = new QueryRunner();
			conn = JDBCUtils.getConnection3();
			String sql = "SELECT id, name, email, birth FROM customers WHERE id < ?";
			BeanListHandler<Customer> handler = new BeanListHandler<>(Customer.class);
			List<Customer> list = runner.query(conn, sql, handler, 12);
			list.forEach(System.out::println);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtils.closeResource(conn, null);
		}
	} 	
	
	/*
	 * MapHandler: 是 ResultSetHandler 介面的實現類, 對應表中的一條紀錄, 
	 * 將欄位及相應欄位的值作為 map 中的 key 和 value.
	 */
	@Test
	public void testQuery3() {
		Connection conn = null;
		try {
			QueryRunner runner = new QueryRunner();
			conn = JDBCUtils.getConnection3();
			String sql = "SELECT id, name, email, birth FROM customers WHERE id = ?";
			MapHandler handler = new MapHandler();
			Map<String, Object> map = runner.query(conn, sql, handler, 12);
			System.out.println(map);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtils.closeResource(conn, null);
		}
	} 

	/*
	 * MapListHandler: 是 ResultSetHandler 介面的實現類, 對應表中的多條紀錄, 
	 * 將欄位及相應欄位的值作為 map 中的 key 和 value, 將這些 map 添加到 List 中.
	 */
	@Test
	public void testQuery4() {
		Connection conn = null;
		try {
			QueryRunner runner = new QueryRunner();
			conn = JDBCUtils.getConnection3();
			String sql = "SELECT id, name, email, birth FROM customers WHERE id < ?";
			MapListHandler handler = new MapListHandler();
			List<Map<String, Object>> list = runner.query(conn, sql, handler, 12);
			list.forEach(System.out::println);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtils.closeResource(conn, null);
		}
	} 
	
	/*
	 * ScalarHandler: 用於查詢特殊值
	 * 查詢紀錄筆數
	 */
	@Test
	public void testQuery5() {
		Connection conn = null;
		try {
			QueryRunner runner = new QueryRunner();
			conn = JDBCUtils.getConnection3();
			String sql = "SELECT count(*) FROM customers";
			ScalarHandler<Long> handler = new ScalarHandler<>();
			Long count = runner.query(conn, sql, handler);
			System.out.println(count);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtils.closeResource(conn, null);
		}
	} 
	
	/*
	 * ScalarHandler: 用於查詢特殊值
	 * 找最大生日
	 */
	@Test
	public void testQuery6() {
		Connection conn = null;
		try {
			QueryRunner runner = new QueryRunner();
			conn = JDBCUtils.getConnection3();
			String sql = "SELECT max(birth) FROM customers";
			ScalarHandler<Date> handler = new ScalarHandler<>();
			Date maxBirth = runner.query(conn, sql, handler);
			System.out.println(maxBirth);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtils.closeResource(conn, null);
		}
	} 	

	/*
	 * 自定義的 ResultSetHandler 的實現類
	 */
	@Test
	public void testQuery7() {
		Connection conn = null;
		try {
			QueryRunner runner = new QueryRunner();
			conn = JDBCUtils.getConnection3();
			
			String sql = "SELECT id, name, email, birth FROM customers WHERE id = ?";
			ResultSetHandler<Customer> handler = new ResultSetHandler<>() { // 注意 <> 中類型在 JDK 7 不能省略
				// 相當於提供了 BeanHandler
				@Override
				public Customer handle(ResultSet rs) throws SQLException {
					if (rs.next()) {
						int id = rs.getInt("id");
						String name = rs.getString("name");
						String email = rs.getString("email");
						Date birth = rs.getDate("birth");
						Customer customer = new Customer(id, name, email, birth);
						return customer;
					}
					return null;
				}
				
			};
			Customer customer = runner.query(conn, sql, handler, 12);
			System.out.println(customer);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtils.closeResource(conn, null);
		}
	} 	
}

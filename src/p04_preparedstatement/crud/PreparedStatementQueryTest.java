package p04_preparedstatement.crud;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import p02_util.JDBCUtils;
import p04_bean.Customer;
import p04_bean.Order;

/**
 * ClassName: PreparedStatementQueryTest.java 
 * PackageName: p04_preparedstatement.crud
 * Description: 使用 PreparedStatement 實現不同表的通用的查詢操作
 */
public class PreparedStatementQueryTest {
	// 測試針對不同表的通用的查詢操作，返回表中的一條紀錄
	@Test
	public void testGetInstance() {
		String sql = "SELECT id, name, email, birth FROM customers WHERE id = ?";
		Customer customer = getInstance(Customer.class, sql, 9);
		System.out.println(customer);
		
		sql = "SELECT order_id orderId, order_name AS orderName, order_date AS orderDate FROM `order` WHERE order_id = ?";
		Order order = getInstance(Order.class, sql, 1);
		System.out.println(order);
	}
	
	/**
	 * 針對不同表的通用的查詢操作，返回表中的一條紀錄
	 * 使用泛型方法
	 */
	public <T> T getInstance(Class<T> clazz, String sql, Object ...args){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			// 1. 獲取資料庫的連接
			conn = JDBCUtils.getConnection();
			// 2. 預編譯 sql 語句，返回 PreparedStatement 實例
			ps = conn.prepareStatement(sql);
			// 3. 填充佔位符
			for(int i = 0; i < args.length; i++) {
				ps.setObject(i + 1, args[i]); // 注意索引
			}
			
			// 4. 執行並返回結果集
			rs = ps.executeQuery();
			// 獲取結果集的元數據: ResultSetMetaData
			ResultSetMetaData rsmd = rs.getMetaData();
			// 透過 ResultSetMetaData 獲取結果集的 column 數
			int columnCount = rsmd.getColumnCount();
			// 5. 處理結果集
			if(rs.next()) {
				// 建議先用無參建構子造物件，再用setter
				T t = clazz.getDeclaredConstructor().newInstance();
				// T t = clazz.newInstance(); // 已過時			
				// 處理結果集一條數據中的每一個column
				for (int i = 0; i < columnCount; i++) {
					// 獲取 column 值
					Object columnValue = rs.getObject(i + 1);
					
					// 獲取 column 別名
					String columnLabel = rsmd.getColumnLabel(i + 1);
					
					// 透過反射將 t 物件指定名 columnName 的屬性賦值為指定的值 columnValue
					Field field = clazz.getDeclaredField(columnLabel);
					field.setAccessible(true);
					field.set(t, columnValue);
				}
				return t;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 6. 資源的關閉
			JDBCUtils.closeResource(conn, ps, rs);			
		}
		return null;
	}
	// 測試針對不同表的通用的查詢操作，返回表中的多條紀錄.
	@Test
	public void testGetForList() {
		String sql = "SELECT id, name, email, birth FROM customers WHERE id < ?";
		List<Customer> list = getForList(Customer.class, sql, 9);
		list.forEach(System.out::println);
		
		sql = "SELECT order_id orderId, order_name AS orderName, order_date AS orderDate FROM `order`";
		List<Order> list1 = getForList(Order.class, sql); // 可變形參可支持不寫
		list1.forEach(System.out::println);
	}	
	
	/**
	 * 針對不同表的通用的查詢操作，返回表中的多條紀錄
	 * 使用泛型方法
	 */
	public <T> List<T> getForList(Class<T> clazz, String sql, Object ...args){
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			// 1. 獲取資料庫的連接
			conn = JDBCUtils.getConnection();
			// 2. 預編譯 sql 語句，返回 PreparedStatement 實例
			ps = conn.prepareStatement(sql);
			// 3. 填充佔位符
			for(int i = 0; i < args.length; i++) {
				ps.setObject(i + 1, args[i]); // 注意索引
			}
			
			// 4. 執行並返回結果集
			rs = ps.executeQuery();
			// 獲取結果集的元數據: ResultSetMetaData
			ResultSetMetaData rsmd = rs.getMetaData();
			// 透過 ResultSetMetaData 獲取結果集的 column 數
			int columnCount = rsmd.getColumnCount();
			
			// 創建集合物件
			ArrayList<T> list = new ArrayList<T>();
			// 5. 處理結果集
			while(rs.next()) {
				// 建議先用無參建構子造物件，再用 setter
				T t = clazz.getDeclaredConstructor().newInstance();
				// T t = clazz.newInstance(); // 已過時			
				// 處理結果集一條數據中的每一個 column: 給 t 物件指定的屬性賦值的過程
				for (int i = 0; i < columnCount; i++) {
					// 獲取 column 值
					Object columnValue = rs.getObject(i + 1);
					
					// 獲取 column 別名
					String columnLabel = rsmd.getColumnLabel(i + 1);
					
					// 透過反射將 t 物件指定名 columnName 的屬性賦值為指定的值 columnValue
					Field field = clazz.getDeclaredField(columnLabel);
					field.setAccessible(true);
					field.set(t, columnValue);
				}
				list.add(t);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 6. 資源的關閉
			JDBCUtils.closeResource(conn, ps, rs);			
		}
		return null;
	}
}

package p04_preparedstatement.crud;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import org.junit.jupiter.api.Test;

import p02_util.JDBCUtils;
import p04_bean.Order;

/**
 * ClassName: OrderForTest.java
 * PackageName: p04_preparedstatement.crud
 * Description: 針對 order 表的通用的查詢操作
 */
public class OrderForTest {
	
	/*
	 * 針對表的 column 名與類的屬性名不相同的情況
	 * 1. 宣告 sql 時使用類的屬性名來命名 column 的別名
	 * 2. 使用 ResultSetMetaData 時，使用 getColumnLabel() 替換 getColumnName() 獲取 column 別名
	 */
	@Test
	public void testOrderForQuery() {
		// 透過 as 別名方式改名稱( as 可加可不加，不加就空一格)，取得要透過 ResultSetMetaData 的 getColumnLabel
		String sql = "SELECT order_id orderId, order_name AS orderName, order_date AS orderDate FROM `order` WHERE order_id = ?";
		Order order = orderForQuery(sql, 1);
		System.out.println(order);
	}
	
	/**
	 * 針對 order 表的通用查詢操作
	 */
	public Order orderForQuery(String sql, Object ...args) {
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
				// 建議先用無參建構子造物件，再用 setter.
				Order order = new Order(); // 放這邊代表有查到結果才建物件
				// 處理結果集一條數據中的每一個 column
				for (int i = 0; i < columnCount; i++) {
					// 獲取 column 值: 透過 ResultSet
					Object columnValue = rs.getObject(i + 1);
					
					// 獲取每個 column 的 column 的 AS(別名)名稱: 透過 ResultSetMetaData 的 getColumnLabel
					// 獲取列的列名: getColumnName()，不推薦使用
					// 獲取列的別名: getColumnLabel()，沒有別名就是回傳列名
					String columnLabel = rsmd.getColumnLabel(i + 1);
					
					// 透過反射將物件指定名 columnName 的屬性賦值為指定的值 columnValue
					Field field = Order.class.getDeclaredField(columnLabel);
					field.setAccessible(true);
					field.set(order, columnValue);
				}
				return order;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 5. 資源的關閉
			JDBCUtils.closeResource(conn, ps, rs);			
		}
		return null;
	}
	
	// 一般非通用查詢
	@Test
	public void testQuery1() {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			// 1. 獲取資料庫的連接
			conn = JDBCUtils.getConnection();
			// 2. 預編譯 sql 語句，返回PreparedStatement實例
			String sql = "SELECT order_id, order_name, order_date FROM `order` WHERE order_id = ?";
			ps = conn.prepareStatement(sql);
			// 3. 填充佔位符
			ps.setObject(1, 1);
			// 4. 執行並返回結果集
			rs = ps.executeQuery();
			// 5. 處理結果集
			if(rs.next()) { 
				int id = (int)rs.getObject(1);
				String name = (String)rs.getObject(2);
				Date date = (Date)rs.getObject(3);
				
				Order order = new Order(id, name, date);
				System.out.println(order);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 5. 資源的關閉
			JDBCUtils.closeResource(conn, ps, rs);			
		}
	}
}
